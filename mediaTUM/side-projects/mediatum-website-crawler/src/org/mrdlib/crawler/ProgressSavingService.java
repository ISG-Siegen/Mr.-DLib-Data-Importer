package org.mrdlib.crawler;

import org.mrdlib.helper.ConsoleOutputService;

import java.io.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

/**
 * Static class offering the service to save progress of crawling mediaTUM's website.
 */
class ProgressSavingService {

    /**
     * Writes a given line to the given file.
     * @param line Line to write.
     * @param progressFilePath File to write to.
     */
    static void writeLineToProgressFile(String line, String progressFilePath) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(progressFilePath, true));
            writer.append(line);
            writer.newLine();
            writer.close();
        } catch (IOException e) {
            ConsoleOutputService.printOutError("Error while saving progress to file " + progressFilePath + ".", e);
        }
    }

    /**
     * Writes a given array list of lines to the given file.
     * @param lines Lines to write.
     * @param progressFilePath File to write to.
     */
    private static void writeLinesToProgressFile(ArrayList<String> lines, String progressFilePath) {
        try {
            PrintWriter writer = new PrintWriter(progressFilePath, "UTF-8");

            for (String line: lines) {
                writer.println(line);
            }

            writer.close();
        } catch (IOException e) {
            ConsoleOutputService.printOutError("Error while saving progress to file " + progressFilePath + ".", e);
        }
    }

    /**
     * Returns the index of the last line of a progress file containing a page.
     * @param progressFileLines Lines to process.
     * @return Index of last line containing a page.
     */
    private static int getLastLineOfProgressFileContainingExploredPageIndex(ArrayList<String> progressFileLines) {
        int lastLineContainingPageIndex = 0;
        int i = 0;

        for (String line : progressFileLines) {
            System.out.println(line);

            if (!Character.isDigit(line.charAt(0))) {
                lastLineContainingPageIndex = i;
            }

            i++;
        }

        return lastLineContainingPageIndex;
    }

    /**
     * Discards the last data set of an array list of lines read from a file containing saved progress.
     * @param progressFileLines Lines to process.
     * @return Array list cleared of last data set.
     */
    private static ArrayList<String> discardProgressFilesLastDataSet(ArrayList<String> progressFileLines) {
        ArrayList<String> progressFileLinesWithDiscardedLastDataSet = new ArrayList<>();

        for (int i = 0; i < getLastLineOfProgressFileContainingExploredPageIndex(progressFileLines); i++) {
            progressFileLinesWithDiscardedLastDataSet.add(progressFileLines.get(i));
        }

        return progressFileLinesWithDiscardedLastDataSet;
    }

    /**
     * Returns the non-empty lines of the given file.
     * @param progressFile File to read in.
     * @return An array list containing all non-empty lines of the read-in file.
     */
    private static ArrayList<String> getAllProgressFileLines(File progressFile) {
        ArrayList<String> progressFileLines = new ArrayList<>();

        try {
            Scanner scanner = new Scanner(progressFile);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                if (!Objects.equals(line, "")) {
                    progressFileLines.add(line);
                }
            }
        } catch (FileNotFoundException e) {
            ConsoleOutputService.printOutError("Error while reading in progress from file.", e);
        }

        return progressFileLines;
    }

    /**
     * Returns an array list containing an entry for each restored progress node. These can either be node ids or page
     * addresses. The last (possibly corrupted) data set is discarded.
     * @param progressFile File to read in.
     * @param progressFilePath Path of file to read in.
     * @return Array list.
     */
    static ArrayList<String> readInProgress(File progressFile, String progressFilePath) {
        ArrayList<String> progressFileLines = ProgressSavingService.getAllProgressFileLines(progressFile);
        ArrayList<String> trimmedProgressFileLines = discardProgressFilesLastDataSet(progressFileLines);

        // save file without corrupt data
        writeLinesToProgressFile(trimmedProgressFileLines, progressFilePath);

        return trimmedProgressFileLines;
    }

}
