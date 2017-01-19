package org.mrdlib.model;

/**
 * Holds data of a OAI-MPH identification
 */
public class Identification {
    public String repositoryName;
    public String baseUrl;
    public String protocolVersion;
    public String adminEmail;
    public String earliestDatestamp;
    public String deletedRecord;
    public String granularity;
    public String description_oaiIdentifier_scheme;
    public String description_oaiIdentifier_repositoryIdentifier;
    public String description_oaiIdentifier_delimiter;
    public String description_oaiIdentifier_sampleIdentifier;

    /**
     * Returns a readable string representation of an OAI identification.
     * @return a string representation of an OAI identification
     */
    @Override
    public String toString() {
        return "Identification (" +
                "repositoryName: '" + repositoryName + "', " +
                "baseUrl: '" + baseUrl + "', " +
                "protocolVersion: '" + protocolVersion + "', " +
                "adminEmail: '" + adminEmail + "', " +
                "earliestDatestamp: '" + earliestDatestamp + "', " +
                "deletedRecord: '" + deletedRecord + "', " +
                "granularity: '" + granularity + "', " +
                "description_oaiIdentifier_scheme: '" + description_oaiIdentifier_scheme + "', " +
                "description_oaiIdentifier_repositoryIdentifier: '" + description_oaiIdentifier_repositoryIdentifier + "', " +
                "description_oaiIdentifier_delimiter: '" + description_oaiIdentifier_delimiter + "', " +
                "description_oaiIdentifier_sampleIdentifier: '" + description_oaiIdentifier_sampleIdentifier + "')";
    }
}