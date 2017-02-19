package org.mrdlib;

import org.mrdlib.harvester.OaiHarvester;
import org.mrdlib.helper.ConsoleOutputService;

import java.util.HashMap;
import java.util.Map;

/**
 * This tool harvests publication meta data of mediaTUM.
 * It supports two different sources of information: 1) an OAI interface, 2) a custom ws_export service of mediaTUM.
 */
public class Main {

    /**
     * Runnable main function.
     * @param args Arguments that need to be passed are:
     *             <ul>
     *               <li>for OAI:
     *                 <ol>
     *                   <li>base URL of OAI service</li>
     *                   <li>meta data format of OAI service</li>
     *                   <li>path of directory to write data to</li>
     *                 </ol>
     *               </li>
     *             <li>for ws_export:
     *               <ol>
     *                 <li>path to file containing node ids (each one in a new line) whose relative nodes (children and
     *                 parents and those children and parents recursively) to harvest</li>
     *                 </ol>
     *               </li>
     *             </ul>
     */
    public static void main(String[] args) {
        int numArguments = args.length;

        // check if the correct number of arguments has been passed to the program
        if (numArguments != 1 && numArguments != 3) {
            ConsoleOutputService.printOutError("Error: Incorrect arguments passed to program. You need to either pass: " +
                    "a) 1) base URL of OAI service, 2) meta data format of OAI service, 3) path of directory to write " +
                    "data to or b) 1) path to file containing node ids (each one in a new line) whose relative nodes " +
                    "(children and parents and those children and parents recursively) to harvest.");

            // end program
            System.exit(1);
        }

        // distinguish between harvesting OAI or ws_export
        if (numArguments == 3) {
            // OAI
            // read in arguments
            String baseUrl = args[0];
            String metadataFormat = args[1];
            String outputDirectoryPath = args[2];

            // harvest
            OaiHarvester.harvest(baseUrl, metadataFormat, outputDirectoryPath);
        } else {
            // ws_export
            // read in arguments
            String fileContainingNodesIdsPath = args[0];

            // TODO: add ws_export harvester
        }
    }

}
