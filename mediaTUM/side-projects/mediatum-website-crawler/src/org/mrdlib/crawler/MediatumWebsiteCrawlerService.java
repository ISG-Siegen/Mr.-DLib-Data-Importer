package org.mrdlib.crawler;

import org.mrdlib.helper.ConsoleOutputService;
import org.mrdlib.helper.WebsiteRetrievalService;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Static class offering the service to print out node ids of publications which are harvested from the mediaTUM website.
 */
public class MediatumWebsiteCrawlerService {

    /**
     * Returns true if the given line contains a link, otherwise false.
     * @param line Line to check.
     * @return True or false.
     */
    private static boolean containsLineLink(String line) {
        return line.contains("<a href=");
    }

    /**
     * Extracts a link from a given line of text.
     * @param line Line to extract link from.
     * @return Extracted link.
     */
    private static String extractLinkFromLine(String line) {
        return line.split("<a href=\"")[1].split("\"")[0];
    }

    /**
     * Returns true if the link contains elements indicating that the link is invalid, otherwise false.
     * @param link Link to check.
     * @return True or false.
     */
    private static boolean isLinkInvalid(String link) {
        return link.equals("/") ||
                link.contains("@") ||
                link.contains("/?id=") ||
                link.contains(".pdf") ||
                link.contains(".pdf") ||
                link.contains("://");
    }

    /**
     * Returns a node id from a given link.
     * @param baseUrl Base URL of mediaTUM.
     * @param link Link to extract node id from.
     * @return Node id if found, otherwise null.
     */
    private static String extractNodeIdFromLink(String baseUrl, String link) {
        String nodeId = null;

        // extract node id
        String workedOnLink = (baseUrl + link).split("\\?amp")[0];

        if (workedOnLink.contains("show_id=")) {
            nodeId = link.split("show_id=")[1].split("&amp;")[0];
        }

        return nodeId;
    }

    /**
     * Returns true if the given link points to a node, otherwise false.
     * @param baseUrl Base URL of mediaTUM.
     * @param link Link to check.
     * @return True or false.
     */
    private static boolean doesLinkPointToANode(String baseUrl, String link) {
        String nodeId = extractNodeIdFromLink(baseUrl, link);

        // a node id has been found, thus the link points to a node
        return nodeId != null;
    }

    /**
     * Returns the trimmed link to a page, that can be stored in the hash map containing pages to explore.
     * @param baseUrl Base URL of mediaTUM.
     * @param link Link to trim.
     * @return Trimmed link.
     */
    private static String getTrimmedLinkToPage(String baseUrl, String link) {
        return ((baseUrl + link).split("\\?amp")[0]).split("\\?")[0];
    }

    /**
     * Crawls given page. Adds newly found links that are not yet inserted into hash map pages. Prints out node ids
     * which have not yet been found.
     * @param baseUrl Base URL of mediaTUM.
     * @param pages HashMap of pages to crawl.
     * @param nodeIds Array list containing found node ids.
     * @param page Page to crawl.
     * @param outputFilePath Path of file to write crawled data to.
     */
    private static void crawlPage(String baseUrl, HashMap<String, ExplorationState> pages, ArrayList<String> nodeIds,
                                  String page, String outputFilePath) {
        // mark page as explored
        pages.replace(page, ExplorationState.EXPLORED);

        // save progress in output file, mark entry as explored
        ProgressSavingService.writeLineToProgressFile("[explored] " + page, outputFilePath);

        // get page as raw text
        InputStream inputStream = WebsiteRetrievalService.getInputStreamFromUrl(page);

        try {
            String line;
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            // go through page line by line
            while ((line = bufferedReader.readLine()) != null) {
                // check if line contains a link
                if (containsLineLink(line)) {
                    // extract link
                    String link = extractLinkFromLine(line);

                    // make sure the link is not invalid
                    if (!isLinkInvalid(link)) {
                        // check if link points to a node
                        if (doesLinkPointToANode(baseUrl, link)) {
                            // extract node id
                            String nodeId = extractNodeIdFromLink(baseUrl, link);

                            // check if node has not already been found
                            if (!nodeIds.contains(nodeId)) {
                                // save it in array list
                                nodeIds.add(nodeId);

                                // save progress in output file
                                ProgressSavingService.writeLineToProgressFile(nodeId, outputFilePath);

                                // for keeping track of progress
                                ConsoleOutputService.printOutStatus("new node has been found, id: " + nodeId);
                            }
                        }

                        // add link to pages to explore if it is not yet in the list
                        String trimmedLinkToPage = getTrimmedLinkToPage(baseUrl, link);

                        if (!pages.containsKey(trimmedLinkToPage)) {
                            pages.putIfAbsent(trimmedLinkToPage, ExplorationState.NOT_EXPLORED);

                            // save progress in output file
                            ProgressSavingService.writeLineToProgressFile("[unexplored] " + trimmedLinkToPage, outputFilePath);

                            // for keeping track of progress
                            ConsoleOutputService.printOutStatus("new page has been found: " + trimmedLinkToPage);
                        }
                    }
                }
            }
        }
        catch (IOException e) {
            ConsoleOutputService.printOutError("Error while retrieving page " + page + ".", e);
        }
    }

    /**
     * Returns the number of explored pages from the given hash map.
     * @param pages Pages to count explored pages in.
     * @return Number of explored pages.
     */
    private static long countExploredPages(HashMap<String, ExplorationState> pages) {
        long numExploredPages = 0;

        for (String currentPage : pages.keySet()) {
            if (pages.get(currentPage) == ExplorationState.EXPLORED) {
                numExploredPages++;
            }
        }

        return numExploredPages;
    }

    /**
     * Returns the URL of the first unexplored page in the passed hash map of pages. Returns null if no such page has
     * been found.
     * @param pages Hash map of pages to find first unexplored page in.
     * @return URL of the first unexplored page in the given hash map. Null if no unexplored page has been found.
     */
    private static String getFirstUnexploredPage(HashMap<String, ExplorationState> pages) {
        String firstUnexploredPage = null;

        for (String currentPage : pages.keySet()) {
            if (pages.get(currentPage) == ExplorationState.NOT_EXPLORED) {
                firstUnexploredPage = currentPage;
                break;
            }
        }

        return firstUnexploredPage;
    }

    /**
     * Crawls all pages of hash map pages that are not yet explored. Saves data to specified output file.
     * @param baseUrl Base URL of mediaTUM.
     * @param pages HashMap of pages to crawl.
     * @param nodeIds Array list containing found node ids.
     * @param outputFilePath Path of file to write crawled data to.
     */
    private static void crawlFirstUnexploredPage(String baseUrl, HashMap<String, ExplorationState> pages,
                                                 ArrayList<String> nodeIds, String outputFilePath) {
        String firstUnexploredPage = getFirstUnexploredPage(pages);

        // for keeping track of progress
        ConsoleOutputService.printOutStatus("[number of explored pages / number of all pages: " +
                countExploredPages(pages) + "/" + pages.size() + " | currently crawled page: " + firstUnexploredPage +
                "]");

        crawlPage(baseUrl, pages, nodeIds, firstUnexploredPage, outputFilePath);
    }

    /**
     * Crawls the websites of mediaTUM. It prints out every identified node id once.
     * @param baseUrl Base URL of mediaTUM, possibly "https://mediatum.ub.tum.de".
     * @param outputFilePath Path of file to write crawled data to. If the file is not empty, it is considered as a file
     *                       containing data of a previous run that shall be continued.
     */
    public static void crawlMediatum(String baseUrl, String outputFilePath) {
        // initialize data structure for keeping track of visited pages and pages to visit
        HashMap<String, ExplorationState> pages = new HashMap<>();

        // initialize data structure for keeping track of found node ids
        ArrayList<String> nodeIds = new ArrayList<>();

        // check if output file contains progress of run to continue
        File outputFile = new File(outputFilePath);

        if(outputFile.exists()) {
            // read in progress
            ArrayList<String> progressNodes = ProgressSavingService.readInProgress(outputFile, outputFilePath);

            // save progress' nodes
            for (String progressNode : progressNodes) {
                // distinguish node ids and pages
                if (Character.isDigit(progressNode.charAt(0))) {
                    // node id
                    nodeIds.add(progressNode);

                    // for keeping track
                    ConsoleOutputService.printOutStatus("restored: node id: " + progressNode);
                } else {
                    // page
                    if (progressNode.startsWith("[explored]")) {
                        pages.put(progressNode.replace("[explored] ", ""), ExplorationState.EXPLORED);

                        // for keeping track
                        ConsoleOutputService.printOutStatus("restored: EXPLROED PAGE: " + progressNode);
                    } else {
                        pages.put(progressNode.replace("[unexplored] ", ""), ExplorationState.NOT_EXPLORED);

                        // for keeping track
                        ConsoleOutputService.printOutStatus("restored: unexplored page: " + progressNode);
                    }
                }
            }

            // for keeping track
            ConsoleOutputService.printOutStatus("*** ALL PROGRESS RESTORED ***");
        } else {
            // save baseUrl as starting point
            pages.put(baseUrl, ExplorationState.NOT_EXPLORED);
        }

        // crawl website
        while (pages.containsValue(ExplorationState.NOT_EXPLORED)) {
            crawlFirstUnexploredPage(baseUrl, pages, nodeIds, outputFilePath);
        }
    }

}
