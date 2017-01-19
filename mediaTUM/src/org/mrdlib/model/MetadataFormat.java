package org.mrdlib.model;

/**
 * Holds data of an OAI metadata object.
 */
public class MetadataFormat {
    public String metadataPrefix;
    public String schema;
    public String metadataNamespace;

    /**
     * Returns a readable string representation of an OAI metadata object.
     * @return a string representation of an OAI metadata object
     */
    @Override
    public String toString() {
        return "MetadataFormat (" +
                "metadataPrefix: '" + metadataPrefix + "', " +
                "schema: '" + schema + "', " +
                "metadataNamespace: '" + metadataNamespace + "')";
    }
}
