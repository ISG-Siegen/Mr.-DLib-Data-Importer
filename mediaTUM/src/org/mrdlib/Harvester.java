package org.mrdlib;

import org.mrdlib.model.Identification;
import org.mrdlib.model.MetadataFormat;
import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.time.YearMonth;
import java.util.Date;

/**
 * An instance of this class offers the functionality to harvest an OAI-PMH.
 * For details regarding OAI-PMH see: http://www.openarchives.org/OAI/openarchivesprotocol.html.
 */
public class Harvester {
    // holds identification of repository to be harvested
    private Identification identification;

    // holds meta data formats provided by the OAI-PMH
    private MetadataFormat[] metadataFormats;

    // holds path of directory to write XML files to
    private String outputDirectoryPath;

    /**
     * Returns the data retrieved from a given URL as an InputStream.
     * @param Url URL to retrieve data from
     * @return retrieved data as InputStream
     */
    private InputStream getInputStreamFromUrl(String Url) {
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
    private Document getDocumentFromInputStream(InputStream inputStream) {
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

    private String formatDate(int year, int month) {
        return year + "-" + String.format("%02d", month);
    }

    private String formatDay(int day) {
        return String.format("%02d", day);
    }

    private int getDaysOfMonth(String month) {
        String[] monthParts = month.split("-");

        int year = Integer.parseInt(monthParts[0]);
        int monthNo = Integer.parseInt(monthParts[1]);

        return YearMonth.of(year, monthNo).lengthOfMonth();
    }

    /**
     * Retrieves an OAI-PMH's identification from the given baseUrl.
     * @param baseUrl baseUrl of OAI-PMH
     * @return OAI-PMH's identification
     */
    private Identification retrieveOaiPmhIdentification(String baseUrl) {
        InputStream inputStream = getInputStreamFromUrl(baseUrl + "?verb=Identify");
        Document document = getDocumentFromInputStream(inputStream);

        Identification identification = new Identification();

        identification.repositoryName = document.getElementsByTagName("repositoryName").item(0).getTextContent();
        identification.baseUrl = document.getElementsByTagName("baseURL").item(0).getTextContent();
        identification.protocolVersion = document.getElementsByTagName("protocolVersion").item(0).getTextContent();
        identification.adminEmail = document.getElementsByTagName("adminEmail").item(0).getTextContent();
        identification.earliestDatestamp = document.getElementsByTagName("earliestDatestamp").item(0).getTextContent();
        identification.deletedRecord = document.getElementsByTagName("deletedRecord").item(0).getTextContent();
        identification.granularity = document.getElementsByTagName("granularity").item(0).getTextContent();
        identification.description_oaiIdentifier_scheme = document.getElementsByTagName("scheme").item(0).getTextContent();
        identification.description_oaiIdentifier_repositoryIdentifier = document.getElementsByTagName("repositoryIdentifier").item(0).getTextContent();
        identification.description_oaiIdentifier_delimiter = document.getElementsByTagName("delimiter").item(0).getTextContent();
        identification.description_oaiIdentifier_sampleIdentifier = document.getElementsByTagName("sampleIdentifier").item(0).getTextContent();

        // TODO: check validity

        return identification;
    }

    /**
     * Retrieves an OAI-PMH's meta data formats from the given baseUrl.
     * @param baseUrl baseUrl of OAI-PMH
     * @return meta data formats
     */
    private MetadataFormat[] retrieveOaiPmhMetadataFormats(String baseUrl) {
        InputStream inputStream = getInputStreamFromUrl(baseUrl + "?verb=ListMetadataFormats");
        Document document = getDocumentFromInputStream(inputStream);

        NodeList metadataFormatNodeList = document.getElementsByTagName("metadataFormat");
        int numMetadataFormats = metadataFormatNodeList.getLength();

        MetadataFormat[] oaiPmhMetadataFormats = new MetadataFormat[numMetadataFormats];

        for (int i = 0; i < numMetadataFormats; i++) {
            oaiPmhMetadataFormats[i] = new MetadataFormat();

            oaiPmhMetadataFormats[i].metadataPrefix = document.getElementsByTagName("metadataPrefix").item(i).getTextContent();
            oaiPmhMetadataFormats[i].schema = document.getElementsByTagName("schema").item(i).getTextContent();
            oaiPmhMetadataFormats[i].metadataNamespace = document.getElementsByTagName("metadataNamespace").item(i).getTextContent();

            // TODO: check validity
        }

        return oaiPmhMetadataFormats;
    }

    /**
     * Constructs a new Harvester object. It collects information about the OAI interface and saves it into attributes
     * of the object.
     * @param baseUrl baseUrl of OAI-PMH
     * @param outputDirectoryPath path to save harvested xml files to
     */
    public Harvester(String baseUrl, String outputDirectoryPath) {
        identification = retrieveOaiPmhIdentification(baseUrl);
        metadataFormats = retrieveOaiPmhMetadataFormats(baseUrl);

        this.outputDirectoryPath = outputDirectoryPath;
    }

    /**
     * Harvests all data provided in a given meta data format of a given month (format yyyy-mm).
     * @param metadataFormat meta data format to harvest
     * @param month month (format yyyy-mm) from which to harvest
     */
    private void harvestMonth(String metadataFormat, String month) {
        // TODO: check parameters

        // construct OAI query string
        String url = identification.baseUrl + "?verb=ListRecords&metadataPrefix=oai_dc&from=" + month + "-01&until=" + month + "-" + formatDay(getDaysOfMonth(month));

        // print queries to console
        System.out.println("[" + new Date() + "] query: " + url);

        // create file
        String fileName = metadataFormat + "_" + month + ".xml";
        File file = new File(outputDirectoryPath + "/" + fileName);

        try {
            // actual collection of data
            FileUtils.copyURLToFile(new URL(url), file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Harvests all data provided by the repository in the given meta data format.
     * @param metadataFormat meta data format to harvest
     */
    public void harvestAll(String metadataFormat) {
        // TODO: check parameters

        harvestFrom(metadataFormat, identification.earliestDatestamp);
    }

    /**
     * Harvests all data provided in a given meta data format from a given date (format yyyy-mm-dd) until today.
     * @param metadataFormat meta data format to harvest
     * @param from date (format yyyy-mm) from which until today to harvest
     */
    public void harvestFrom(String metadataFormat, String from) {
        // TODO: check parameters

        // TODO: use Calendar library instead of Date
        int currentYear =  new Date().getYear() + 1900;
        int currentMonth =  new Date().getMonth() + 1;

        // TODO: possibly move to separate method
        String[] fromParts = from.split("-");

        int startYear = Integer.parseInt(fromParts[0]);
        int startMonth = Integer.parseInt(fromParts[1]);

        // start year
        if (startYear == currentYear) {
            for (int month=startMonth; month <= currentMonth; month++) {
                harvestMonth(metadataFormat, formatDate(startYear, month));
            }
        } else {
            for (int month=startMonth; month <= 12; month++) {
                harvestMonth(metadataFormat, formatDate(startYear, month));
            }
        }

        // full years
        for (int year=startYear + 1; year < currentYear; year++) {
            for (int month=1; month <= 12; month++) {
                harvestMonth(metadataFormat, formatDate(year, month));
            }
        }

        // current year
        for (int month=1; month <= currentMonth; month++) {
            harvestMonth(metadataFormat, formatDate(currentYear, month));
        }
    }
}
