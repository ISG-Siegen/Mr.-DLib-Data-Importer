package org.mrdlib;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Class containing the entry point of the console program.
 */
public class Main {
    // change for later releases
    private static final int version = 1;

    /**
     * Prints the given error message and exits the program with code 1 afterwards.
     * @param errorMessage error message to print
     */
    private static void printErrorAndExit(String errorMessage) {
        System.err.println(errorMessage);

        System.exit(1);
    }

    /**
     * Converts a given array of arguments (format: "key=value") to a key value map.
     * Arguments not containing character '=' are ignored.
     * @param args array of arguments (form: "key=value"
     * @return key value map
     */
    private static Map<String, String> convertArgsToMap(String[] args) {
        Map<String, String> map = new HashMap<>();

        for (String arg: args) {
            if (arg.contains("=")) {
                String[] parts = arg.split("=");

                map.put(parts[0], parts[1]);
            }
        }

        return map;
    }

    /**
     * Entry point of the console program.
     * @param args list of arguments passed at start up.
     */
    public static void main(String[] args) {
        ResourceBundle messages = ResourceBundle.getBundle("messages");

        // check if arguments have been passed
        if (args.length < 1) {
            printErrorAndExit(messages.getString("errorNoArgumentsPassed"));
        }

        // check if printing out help is requested
        if (args[0].equals("--help")) {
            System.out.println(messages.getString("help"));
        }

        // handle arguments
        Map<String, String> argumentsMap = convertArgsToMap(args);

        // check if all needed arguments have been passed
        if (argumentsMap.get("baseUrl") == null) {
            // TODO: check validity

            printErrorAndExit(messages.getString("errorNoBaseUrlPassed"));
        }

        if (argumentsMap.get("metadataFormat") == null) {
            // TODO: check validity

            printErrorAndExit(messages.getString("errorNoMetadataFormatPassed"));
        }

        if (argumentsMap.get("outputDirectoryPath") == null) {
            // TODO: check validity

            printErrorAndExit(messages.getString("errorNoOutputDirectoryPathPassed"));
        }

        Harvester harvester = new Harvester(argumentsMap.get("baseUrl"), argumentsMap.get("outputDirectoryPath"));

        if (argumentsMap.get("from") == null) {
            System.out.println("harvesting all available data");

            // if no from argument is passed, harvest all data
            harvester.harvestAll(argumentsMap.get("metadataFormat"));
        } else {
            System.out.println("harvesting available data since " + argumentsMap.get("from"));

            // otherwise harvest all data since given date
            harvester.harvestFrom(argumentsMap.get("metadataFormat"), argumentsMap.get("from"));
        }
    }
}
