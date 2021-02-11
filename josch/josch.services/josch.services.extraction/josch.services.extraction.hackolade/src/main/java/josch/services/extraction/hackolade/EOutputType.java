package josch.services.extraction.hackolade;

/**
 * This enumeration is used to determine the output types of the script. The types are being
 * returned in lower case when the toString() method is being called.
 */
public enum EOutputType {
    JSONSCHEMA {
        @Override
        public String toString() {
            return JSONSCHEMA.name().toLowerCase();
        }
    },
    SCRIPT {
        @Override
        public String toString() {
            return SCRIPT.name().toLowerCase();
        }
    }
}
