package josch.services.validation;

import josch.test.MongoDB.MongoBaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * This class is used as a test case for the ValidationService. The validation service provides the
 * functionality to validate documents against schemas via {@code validate(document, schema)}. This
 * method has 3 cases:
 *
 * <ul>
 *   <li>The schema is a $jsonSchema. This is being excluded from the test because its functionality
 *       is * handled server side and is being tested in Client Tests.
 *   <li>The schema is a valid JSON Schema of draft-07, -06 or -04. These are the drafts that are
 *       being supported by the used framework <em>Justify</em>. All frameworks on json-schema.org
 *       only support up to these drafts.
 *   <li>Other schemas, like other drafts and such. These are not being validated against because no
 *       Java Framework supports them.
 * </ul>
 *
 * @see josch.persistency.implementation.mongodb for the MongoDB tests on validation.
 */
public class ValidationServiceTest extends MongoBaseTest {

  /** Assures that the database is in the correct state. */
  @BeforeEach
  public void setupDatabase() {
    setup();
  }

  /**
   * A valid MongoDB JSON Schema for the collection that validates half the collection. The
   * documents with "class": "Preschool" will fail validation.
   */
  protected static final String HALF_VALIDATOR =
      String.join(
          "\n",
          new String[] {
            "{"
                + "  \"$jsonSchema\": {"
                + "    \"type\": \"object\","
                + "    \"title\": \"students\","
                + "    \"additionalProperties\": true,"
                + "    \"properties\":"
                + "    {"
                + "      \"year\": { \"type\": \"number\" },"
                + "      \"class\": { \"type\":  \"number\" },"
                + "      \"language\": { \"type\": \"string\" },"
                + "      \"name\": { \"anyOf\":"
                + "      ["
                + "        { \"type\": \"string\" },"
                + "        {"
                + "          \"type\": \"object\","
                + "          \"additionalProperties\": false,"
                + "          \"properties\":"
                + "          {"
                + "              \"first\": { \"type\": \"string\" },"
                + "              \"nick\": { \"type\": \"string\" }"
                + "          }"
                + "        }"
                + "        ]"
                + "      }"
                + "    },"
                + "    \"required\": [\"year\", \"class\" ]"
                + "  }"
                + "}"
          });

  /**
   * A valid JSON Schema draft-04 for the collection that validates half the collection. The
   * documents with "class": "Preschool" will fail validation.
   */
  protected static final String HALF_JSON_SCHEMA =
      String.join(
          "\n",
          new String[] {
            "{"
                + "  \"$schema\": \"http://json-schema.org/draft-04/schema#\","
                + "  \"type\": \"object\","
                + "  \"title\": \"students\","
                + "  \"additionalProperties\": true,"
                + "  \"properties\": {"
                + "    \"year\": { \"type\": \"number\" },"
                + "    \"class\": { \"type\": \"number\" },"
                + "    \"language\": { \"type\": \"string\" },"
                + "    \"name\": {"
                + "      \"anyOf\": ["
                + "        { \"type\": \"string\" },"
                + "        {"
                + "          \"type\": \"object\","
                + "          \"additionalProperties\": false,"
                + "          \"properties\": {"
                + "            \"first\": { \"type\": \"string\" },"
                + "            \"nick\": { \"type\": \"string\" }"
                + "          }"
                + "        }"
                + "      ]"
                + "    }"
                + "  },"
                + "  \"required\": [ \"year\", \"class\" ]"
                + "}"
          });

  /** An invalid JSON schema draft-04. Note that the type of year is not being supported. */
  final String INVALID_JSON_SCHEMA =
      String.join(
          "\n",
          new String[] {
            "{"
                + "  \"$schema\": \"http://json-schema.org/draft-04/schema#\","
                + "  \"type\": \"object\","
                + "  \"title\": \"students\","
                + "  \"additionalProperties\": true,"
                + "  \"properties\": {"
                + "    \"year\": { \"type\": \"int32\" },"
                + "    \"class\": { \"type\": \"number\" },"
                + "    \"language\": { \"type\": \"string\" },"
                + "    \"name\": {"
                + "      \"anyOf\": ["
                + "        { \"type\": \"string\" },"
                + "        {"
                + "          \"type\": \"object\","
                + "          \"additionalProperties\": false,"
                + "          \"properties\": {"
                + "            \"first\": { \"type\": \"string\" },"
                + "            \"nick\": { \"type\": \"string\" }"
                + "          }"
                + "        }"
                + "      ]"
                + "    }"
                + "  },"
                + "  \"required\": [ \"year\", \"class\" ]"
                + "}"
          });

  /** A schema that is no JSON schema draft-04, -06 or -07, like a pseudo schema */
  final String PSEUDO_SCHEMA =
      String.join(
          "\n",
          new String[] {
            "{"
                + "  \"properties\": {"
                + "    \"year\": { \"type\": \"number\" },"
                + "    \"class\": { \"type\": [\"number\", \"string\"] },"
                + "    \"language\": { \"type\": \"string\" },"
                + "    \"name\": {"
                + "      \"anyOf\": ["
                + "        { \"type\": \"string\" },"
                + "        {"
                + "          \"type\": \"object\","
                + "          \"additionalProperties\": false,"
                + "          \"properties\": {"
                + "            \"first\": { \"type\": \"string\" },"
                + "            \"nick\": { \"type\": \"string\" }"
                + "          }"
                + "        }"
                + "      ]"
                + "    }"
                + "  },"
                + "  \"required\": [\"year\", \"class\" ]"
                + "}"
          });

  /** A document that validates against the HALF_SCHEMA */
  final String VALIDATE_DOCUMENT =
      "{ \"name\": {\"first\": \"Anton\", \"nick\": \"Toni\" }, \"year\": 2015, \"class\": 1 }";

  /** A document that does not validate against the HALF_SCHEMA */
  final String INVALIDATE_DOCUMENT =
      "{ \"name\": \"Paul\", \"year\": 2015, \"class\": \"Preschool\" }";

  /** Tests whether a valid document validates against the valid schema. */
  @Test
  public void validate_validDocument_validSchema() {
    ValidationService service = new ValidationService(settings);
    String expected = "The schema validates this document.";

    // Test the draft.
    String actual = service.validate(VALIDATE_DOCUMENT, HALF_JSON_SCHEMA);
    Assertions.assertEquals(expected, actual);

    // Test the validator.
    actual = service.validate(VALIDATE_DOCUMENT, HALF_VALIDATOR);
    Assertions.assertEquals(expected, actual);
  }

  /** Tests whether a invalid document invalidates against the valid schemas. */
  @Test
  public void validate_invalidDocument_validSchema() {
    ValidationService service = new ValidationService(settings);
    String expectedSchema = "Error! #/class: expected type: Number, found: String";
    String expectedValidator = "Document failed validation";

    // Test the draft
    String actual = service.validate(INVALIDATE_DOCUMENT, HALF_JSON_SCHEMA);
    Assertions.assertEquals(expectedSchema, actual);

    // Test the validator
    actual = service.validate(INVALIDATE_DOCUMENT, HALF_VALIDATOR);
    Assertions.assertEquals(expectedValidator, actual);
  }

  /** Tests whether a valid or invalid document causes an unsupported message. */
  @Test
  public void validate_pseudoSchema() {
    ValidationService service = new ValidationService(settings);
    String expected = "Error! The schema entered is not being supported.";

    // valid document but pseudo schema.
    String actual = service.validate(VALIDATE_DOCUMENT, PSEUDO_SCHEMA);
    Assertions.assertEquals(expected, actual);

    // invalid document and pseudo schema.
    actual = service.validate(INVALIDATE_DOCUMENT, PSEUDO_SCHEMA);
    Assertions.assertEquals(expected, actual);

    // validate all with pseudo schema.
    actual = service.validate(PSEUDO_SCHEMA).getNotification();
    Assertions.assertEquals(expected, actual);
  }

  /** Tests whether a valid or invalid document causes an unsupported message. */
  @Test
  public void validate_invalidSchema() {
    ValidationService service = new ValidationService(settings);
    String expected = "Error! #/properties/year: unknown type: [int32]";

    // valid document but invalid schema.
    String actual = service.validate(VALIDATE_DOCUMENT, INVALID_JSON_SCHEMA);
    Assertions.assertEquals(expected, actual);

    // both invalid document and invalid schema.
    actual = service.validate(INVALIDATE_DOCUMENT, INVALID_JSON_SCHEMA);
    Assertions.assertEquals(expected, actual);

    // validate all documents with invalid schema.
    actual = service.validate(INVALID_JSON_SCHEMA).getNotification();
    Assertions.assertEquals(expected, actual);
  }

  /** Tests whether a globally valid schema does validate all documents of the collection. */
  @Test
  public void validate_all_allValid() {
    ValidationService service = new ValidationService(settings);
    String expected = "All documents in this collection validate against the schema.";

    // valid document but invalid schema.
    String actual = service.validate(JSON_SCHEMA).getNotification();
    Assertions.assertEquals(expected, actual);

    // both invalid document and invalid schema.
    actual = service.validate(VALIDATOR).getNotification();
    Assertions.assertEquals(expected, actual);
  }

  /** Tests whether a half valid schema does validate half documents of the collection. */
  @Test
  public void validate_all_halfValid() {
    ValidationService service = new ValidationService(settings);
    String expected = "Out of 26 documents validate 13 against this schema. (That's 50.0%.)";

    // valid document but invalid schema.
    String actual = service.validate(HALF_JSON_SCHEMA).getNotification();
    Assertions.assertEquals(expected, actual);

    // both invalid document and invalid schema.
    actual = service.validate(HALF_VALIDATOR).getNotification();
    Assertions.assertEquals(expected, actual);
  }
}
