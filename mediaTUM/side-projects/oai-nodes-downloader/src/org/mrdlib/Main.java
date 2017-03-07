package org.mrdlib;

import org.apache.commons.io.FileUtils;
import org.mrdlib.helper.ConsoleOutputService;
import org.w3c.dom.Document;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import static org.mrdlib.helper.WebsiteRetrievalService.getDocumentFromInputStream;
import static org.mrdlib.helper.WebsiteRetrievalService.getInputStreamFromUrl;

/**
 * This auxiliary tool checks wether nodes retrieved from mediaTUM ws_export interface are available at mediaTUM's
 * OAI interface as well.
 */
public class Main {

    /**
     * Runnable main function.
     * @param args Arguments that need to be passed are:
     *             <ol>
     *               <li>path to directory containing one file for each non-OAI node</li>
     *               <li>path of directory to put output data to</li>
     *               <li>mediaTUM's OAI base URL, e.g. https://mediatum.ub.tum.de/oai/oai</li>
     *               <li>OAI metadata prefix, e.g. oai_dc</li>
     *             </ol>
     */
    public static void main(String[] args) {
        // check if the correct number of arguments has been passed to the program
        if (args.length != 4) {
            ConsoleOutputService.printOutError("Error: Incorrect arguments passed to program. You need to pass: " +
                    "1) path to directory containing one file for each non-OAI node, 2) path of directory to put " +
                    "output data to, 3) mediaTUM's OAI base URL, e.g. https://mediatum.ub.tum.de/oai/oai, " +
                    "4) OAI metadata prefix, e.g. oai_dc.");
            return;
        }

        // interpret first argument as path of directory containing harvested xml files
        String directory = args[0];
        // interpret second argument as path of directory to put output data to
        String outputFolder = args[1];
        // interpret third argument as mediaTUM's OAI base URL, e.g. https://mediatum.ub.tum.de/oai/oai
        String mediaTumBaseUrl = args[2];
        // interpret fourth argument as OAI metadata prefix
        String oaiMetadataPrefix = args[3];

        // get all files in directory
        File directoryFile = new File(directory);
        if (directoryFile == null) {
            ConsoleOutputService.printOutError("Error: No valid directory passed as argument.");
            return;
        }
        File[] files = directoryFile.listFiles();

        if (files == null) {
            ConsoleOutputService.printOutError("Error: No files to extract node ids from found in specified directory.");
            return;
        }

        ArrayList<String> nodeIds = new ArrayList<>();

        // find node ids in files
        for (int i = 0; i < files.length; i++) {
            nodeIds.add(files[i].getName().split(".xml")[0]);
        }

        // for tracking progress
        int i = 1;
        // for keeping track of errornous nodes
        ArrayList<String> flawedNodeIds = new ArrayList<>();

        // iterate over ws_export node ids
        for (String nodeId : nodeIds) {
            // construct query URL
            String url = mediaTumBaseUrl + "?verb=GetRecord&identifier=oai:mediatum.ub.tum.de:node/" + nodeId +
                    "&metadataPrefix=" + oaiMetadataPrefix;

            // retrieve data, collect metrics for checking validity
            int numErrorTags = 1;
            int numRecordHasNoXMLRepresentationTags = 1;

            try {
                Document document = getDocumentFromInputStream(getInputStreamFromUrl(url));
                numErrorTags = document.getElementsByTagName("error").getLength();
                numRecordHasNoXMLRepresentationTags =
                        document.getElementsByTagName("recordHasNoXMLRepresentation").getLength();
            } catch (Exception e) {
                ConsoleOutputService.printOutError("Error (Document)");
                flawedNodeIds.add(nodeId);
            }

            ConsoleOutputService.printOutStatus("[" + i + "/" + nodeIds.size() + "] " + nodeId);

            i++;

            // check validity
            if (numErrorTags == 0 && numRecordHasNoXMLRepresentationTags == 0) {
                ConsoleOutputService.printOutStatus("hit");

                File outputFile = new File(outputFolder + "/" + nodeId + ".xml");

                try {
                    FileUtils.copyURLToFile(new URL(url), outputFile);
                } catch (IOException e) {
                    ConsoleOutputService.printOutError("Error (FileUtils)");
                    flawedNodeIds.add(nodeId);
                }
            }
        }

        // after all nodes have been iterated over, report failed node collections
        ConsoleOutputService.printOutStatus("+++ FLAWED NODE IDS +++");

        for (String flawedNodeId : flawedNodeIds) {
            ConsoleOutputService.printOutStatus(flawedNodeId);
        }

    }

}
