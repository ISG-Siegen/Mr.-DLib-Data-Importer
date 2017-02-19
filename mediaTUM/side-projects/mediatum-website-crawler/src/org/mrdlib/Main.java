package org.mrdlib;

import org.mrdlib.crawler.MediatumWebsiteCrawlerService;
import org.mrdlib.helper.ConsoleOutputService;

/**
 * This auxiliary tool crawls the website of mediaTUM for ids of nodes that may be retrieved via the ws_export service.
 */
public class Main {

    /**
     * Runnable main function.
     * @param args Arguments that need to be passed are: 1) mediaTUM base URL, 2) path of file to write output to.
     */
    public static void main(String[] args) {
        // check if the correct number of arguments has been passed to the program
        if (args.length != 2) {
            ConsoleOutputService.printOutError("Error: Incorrect arguments passed to program. You need to pass: 1) " +
                    "mediaTUM base URL, 2) path of file to write output to.");
            return;
        }

        // retrieve parameters
        String baseUrl = args[0];
        String outputFilePath = args[1];

        // call the service that crawls the website of mediaTUM
        MediatumWebsiteCrawlerService.crawlMediatum(baseUrl, outputFilePath);
    }

}
