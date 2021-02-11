package josch.presentation.gui.model.utils;

/**
 * This enum holds the information about the third party open source software used within this
 * application.
 */
public enum ESoftware {
    JUNIT("JUnit Jupiter API", "5.6.2", "https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter-api", ELicenses.EPL_2),
    JSON("JSON In Java", "20160212", "https://mvnrepository.com/artifact/org.json/json", ELicenses.JSON),
    MAVEN("Apache Maven", "", "https://maven.apache.org/", ELicenses.APACHE_2),
    MDB_DRVR_SYNC("MongoDb Driver", "4.0.5", "https://mvnrepository.com/artifact/org.mongodb/mongodb-driver-sync", ELicenses.APACHE_2),
    JAVAFX("OpenJFX", "14", "https://mvnrepository.com/artifact/org.openjfx", ELicenses.GPL_2),
    RTFX("RichTextFX", "0.10.4", "https://mvnrepository.com/artifact/org.fxmisc.richtext/richtextfx", ELicenses.GPL_2),
    JSS("jsonsubschema", "hash 165f983", "https://github.com/IBM/jsonsubschema", ELicenses.APACHE_2),
    IJS("is-json-schema-subset", "latest", "https://github.com/haggholm/is-json-schema-subset", ELicenses.MIT),
    HACKOLADE("Hackolade", "", "https://hackolade.com", ELicenses.HACKOLADE),
    JSI("json-schema-inferrer", "0.1.3", "https://github.com/saasquatch/json-schema-inferrer", ELicenses.APACHE_2),
    GSON("Gson", "2.8.6", "https://mvnrepository.com/artifact/com.google.code.gson/gson", ELicenses.APACHE_2),
    GUAVA("Guava: Google Core Libraries For Java", "29.0-jre", "https://mvnrepository.com/artifact/com.google.guava/guava", ELicenses.APACHE_2),
    EVERIT("JSON Schema Validator", "1.12.1", "https://github.com/everit-org/json-schema", ELicenses.APACHE_2);

    /** The name of the software. */
    private final String NAME;

    /** The used version. */
    private final String VERSION;

    /** A reference to the software. */
    private final String SRC;

    /** The software's licence. */
    private final ELicenses LICENSE;

    /** Constructs an Enum */
    ESoftware(String name, String version, String source, ELicenses license) {
        this.NAME = name;
        this.VERSION = version;
        this.SRC = source;
        this.LICENSE = license;
    }

    /**
     * Gets the {@code LICENSE}
     *
     * @return The value of {@code LICENSE}
     */
    public ELicenses getLicense() {
        return LICENSE;
    }

    /**
     * Gets the {@code VERSION}
     *
     * @return The value of {@code VERSION}
     */
    public String getVersion() {
        return VERSION;
    }

    /**
     * Gets the {@code NAME}
     *
     * @return The value of {@code NAME}
     */
    public String getName() {
        return NAME;
    }

    /**
     * Gets the {@code SRC}
     *
     * @return The value of {@code SRC}
     */
    public String getSource() {
        return SRC;
    }
}
