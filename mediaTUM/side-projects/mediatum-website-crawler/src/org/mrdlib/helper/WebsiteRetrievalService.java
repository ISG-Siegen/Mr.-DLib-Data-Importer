package org.mrdlib.helper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Provides helper functions for retrieving data from a website.
 */
public class WebsiteRetrievalService {

    /**
     * Returns the data retrieved from a given URL as an InputStream.
     * @param url URL to retrieve data from.
     * @return Retrieved data as InputStream.
     */
    public static InputStream getInputStreamFromUrl(String url) {
        URLConnection connection = null;
        try {
            connection = new URL(url).openConnection();
        } catch (IOException e) {
            ConsoleOutputService.printOutError("Error while connecting to URL " + url + ".", e);
        }

        InputStream inputStream = null;
        try {
            assert connection != null;
            inputStream = connection.getInputStream();
        } catch (IOException e) {
            ConsoleOutputService.printOutError("Error while getting the InputStream of URL " + url + ".", e);
        }

        return inputStream;
    }

}
