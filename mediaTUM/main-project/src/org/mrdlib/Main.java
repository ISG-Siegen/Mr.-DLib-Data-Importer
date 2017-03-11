package org.mrdlib;

import org.mrdlib.harvester.OaiHarvester;
import org.mrdlib.harvester.WsExportHarvester;
import org.mrdlib.helper.ConsoleOutputService;

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
     *                   <li>"OAI"</li>
     *                   <li>base URL of OAI service</li>
     *                   <li>meta data format of OAI service</li>
     *                   <li>path of directory to write data to</li>
     *                 </ol>
     *               </li>
     *             <li>for ws_export:
     *               <ol>
     *                 <li>"ws_export"</li>
     *                 <li>base URL of ws_export service</li>
     *                 <li>number of nodes to fetch at once (e.g. 10)</li>
     *                 <li>path to file containing node ids (each one in a new line) whose relative nodes (children and
     *                 parents and those children and parents recursively) to harvest, progress is saved in this file
     *                 as well</li>
     *                 <li>whether only children should be harvested ('true') or children and parents ('false')</li>
     *                 </ol>
     *               </li>
     *             </ul>
     */
    public static void main(String[] args) {
        int numArguments = args.length;

        // check if the correct number of arguments has been passed to the program
        if (numArguments != 4 && numArguments != 5) {
            ConsoleOutputService.printOutError("Error: Incorrect arguments passed to program. You need to either pass: " +
                    "a) 1) 'OAI' 2) base URL of OAI service, 3) meta data format of OAI service, 4) path of directory " +
                    "to write data to or b) 1) 'ws_export', 2) base URL of ws_export service, 3) number of nodes to " +
                    "fetch at once (e.g. 10), 4) path to file containing node ids (each one in a new line) whose " +
                    "relative nodes (children and parents and those children and parents recursively) to harvest, " +
                    "5) whether only children should be harvested ('true') or children and parents ('false').");

            // end program
            System.exit(1);
        }

        // distinguish between harvesting OAI or ws_export
        String harvestingMode = args[0];

        switch (harvestingMode) {
            case "OAI": {
                // OAI
                // read in arguments
                String baseUrl = args[1];
                String metadataFormat = args[2];
                String outputDirectoryPath = args[3];

                // harvest
                OaiHarvester.harvest(baseUrl, metadataFormat, outputDirectoryPath);
                break;
            }
            case "ws_export": {
                // ws_export
                // read in arguments
                String baseUrl = args[1];
                int numNodesToFetchAtOnce = Integer.parseInt(args[2]);
                String fileContainingNodesIdsPath = args[3];
                boolean harvestOnlyChildren = false;
                if (args[4].equals("true")) {
                    harvestOnlyChildren = true;
                }

                // harvest
                WsExportHarvester.harvest(baseUrl, numNodesToFetchAtOnce, fileContainingNodesIdsPath, harvestOnlyChildren);
                break;
            }
            default:
                ConsoleOutputService.printOutError("Error: no valid harvesting mode (either 'OAI' or 'ws_export' has been " +
                        "passed as first argument.");
                break;
        }
    }

}
