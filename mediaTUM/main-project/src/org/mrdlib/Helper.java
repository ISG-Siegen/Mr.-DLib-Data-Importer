package org.mrdlib;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Provides helper functions for retrieving data from a website.
 */
public class Helper {

    /**
     * Returns the data retrieved from a given URL as an InputStream.
     * @param Url URL to retrieve data from
     * @return retrieved data as InputStream
     */
    public static InputStream getInputStreamFromUrl(String Url) {
        URLConnection connection = null;
        try {
            connection = new URL(Url).openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

        InputStream inputStream = null;
        try {
            inputStream = connection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return inputStream;
    }

    /**
     * Transforms an InputStream to a Document.
     * @param inputStream InputStream to transform
     * @return document that the InputStream has been transformed into
     */
    public static Document getDocumentFromInputStream(InputStream inputStream) {
        DocumentBuilderFactory factory = null;
        DocumentBuilder builder = null;
        Document document = null;
        try {
            factory = DocumentBuilderFactory.newInstance();
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        try {
            document = builder.parse(new InputSource(inputStream));
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return document;
    }

}
