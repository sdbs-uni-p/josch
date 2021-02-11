package josch.services.comparison.isjsonsubset;

import josch.model.dto.SettingsDto;
import josch.model.enums.EContainmentRelations;
import josch.model.enums.EContainmentTools;
import josch.model.enums.EDatabaseSystems;
import josch.model.enums.EExtractionTools;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * This class is used to test the comparison service using the IJS tool. The script is being tested
 * here along side instead of using jest in order to avoid including too many NPM / Yarn modules.
 *
 * @author Kai Dauberschmidt
 */
public class IjsComparisonServiceTest {

    /** The settings */
    private SettingsDto settings;

    /** setup the path to the system. Note this has to be adjusted. */
    @BeforeEach
    public void setup() {
        settings = new SettingsDto(EDatabaseSystems.MONGO, EExtractionTools.HACK, EContainmentTools.IJS_SUBSET);
    settings.setIjsPath("F:/Uni Passau/josch/tools/IsJsonSchemaSubset");
    settings.setJssPath("F:/Uni Passau/josch/tools/JsonSubSchema");
    }

    /**
     * This equals S1 from the containment tool test
     */
    private static final String S1 =
            String.join(
                    "\n",
                    new String[]{
                            "{",
                            "\"$schema\": \"http://json-schema.org/draft-04/schema#\",",
                            "\"additionalProperties\": false,",
                            "\"type\": \"object\",",
                            "\"title\": \"students\",",
                            "\"properties\": {",
                            "\"year\": {",
                            "\"type\":[\"number\", \"string\"]",
                            "},",
                            "\"name\": {",
                            "\"type\": \"string\"",
                            "},",
                            "\"language\": {",
                            "\"type\": \"string\"",
                            "},",
                            "\"_id\": {",
                            "\"type\": [",
                            "\"object\",",
                            "\"string\",",
                            "\"number\"",
                            "]",
                            "},",
                            "\"class\": {",
                            "\"type\": \"integer\"",
                            "}",
                            "},",
                            "\"required\": [",
                            "\"_id\",",
                            "\"name\",",
                            "\"year\",",
                            "\"class\"",
                            "]",
                            "}"
                    });

    /**
     * S2 equals S1 but the name and language are swapped
     */
    private static final String S2 =
            String.join(
                    "\n",
                    new String[]{
                            "{",
                            "\"$schema\": \"http://json-schema.org/draft-04/schema#\",",
                            "\"additionalProperties\": false,",
                            "\"type\": \"object\",",
                            "\"title\": \"students\",",
                            "\"properties\": {",
                            "\"year\": {",
                            "\"type\":[\"number\", \"string\"]",
                            "},",
                            "\"language\": {",
                            "\"type\": \"string\"",
                            "},",
                            "\"name\": {",
                            "\"type\": \"string\"",
                            "},",
                            "\"_id\": {",
                            "\"type\": [",
                            "\"object\",",
                            "\"string\",",
                            "\"number\"",
                            "]",
                            "},",
                            "\"class\": {",
                            "\"type\": \"integer\"",
                            "}",
                            "},",
                            "\"required\": [",
                            "\"_id\",",
                            "\"name\",",
                            "\"year\",",
                            "\"class\"",
                            "]",
                            "}"
                    });

    /**
     * S3 is subset of S1 and S2 because the language is missing
     */
    private static final String S3 =
            String.join(
                    "\n",
                    new String[]{
                            "{",
                            "\"$schema\": \"http://json-schema.org/draft-04/schema#\",",
                            "\"additionalProperties\": false,",
                            "\"type\": \"object\",",
                            "\"title\": \"students\",",
                            "\"properties\": {",
                            "\"year\": {",
                            "\"type\":[\"number\", \"string\"]",
                            "},",
                            "\"name\": {",
                            "\"type\": \"string\"",
                            "},",
                            "\"_id\": {",
                            "\"type\": [",
                            "\"object\",",
                            "\"string\",",
                            "\"number\"",
                            "]",
                            "},",
                            "\"class\": {",
                            "\"type\": \"integer\"",
                            "}",
                            "},",
                            "\"required\": [",
                            "\"_id\",",
                            "\"name\",",
                            "\"year\",",
                            "\"class\"",
                            "]",
                            "}"
                    });

    /**
     * S4 is different to S1 - 3.
     */
    private static final String S4 =
            String.join(
                    "\n",
                    new String[]{
                            "{",
                            "\"$schema\": \"http://json-schema.org/draft-04/schema#\",",
                            "\"additionalProperties\": false,",
                            "\"type\": \"object\",",
                            "\"title\": \"employees\",",
                            "\"properties\": {",
                            "\"name\": {",
                            "\"type\": \"string\"",
                            "},",
                            "\"_id\": {",
                            "\"type\": [",
                            "\"object\",",
                            "\"string\",",
                            "\"number\"",
                            "]",
                            "},",
                            "\"income\": {",
                            "\"type\": \"integer\"",
                            "}",
                            "},",
                            "\"required\": [",
                            "\"_id\",",
                            "\"name\",",
                            "\"year\"",
                            "]",
                            "}"
                    });

    /**
     * Checks for equivalence on reflexive Schema.
     */
    @Test
    public void contains_ReflexiveS1() {
        IjsComparisonService service = getService();
        String result = service.contains(S1, S1);
        Assertions.assertEquals(EContainmentRelations.EQV.toString(), result);
    }

    /**
     * Checks for equivalence on two given schemas that are syntactically equal.
     */
    @Test
    public void contains_S2EquivalentS1() {
        IjsComparisonService service = getService();
        String result = service.contains(S1, S2);
        System.out.println(result);
        Assertions.assertEquals(EContainmentRelations.EQV.toString(), result);
    }

    /**
     * Checks for sub schema detection.
     */
    @Test
    public void contains_S3SubsetS1() {
        IjsComparisonService service = getService();
        String result = service.contains(S3, S1);
        Assertions.assertEquals(EContainmentRelations.SUB.toString(), result);
    }

    /**
     * Checks for super schema detection.
     */
    @Test
    public void contains_S1SupersetS3() {
        IjsComparisonService service = getService();
        String result = service.contains(S1, S3);
        Assertions.assertEquals(EContainmentRelations.SUP.toString(), result);
    }

    /**
     * Checks for contrary detection.
     */
    @Test
    public void contains_S4ContraryS1() {
        IjsComparisonService service = getService();
        String result = service.contains(S4, S1);
        Assertions.assertEquals(EContainmentRelations.NEQV.toString(), result);
    }

    /**
     * Returns a Ijs Comparison Service without any settings being set.
     */
    private IjsComparisonService getService() {
        return new IjsComparisonService(settings);
    }
}
