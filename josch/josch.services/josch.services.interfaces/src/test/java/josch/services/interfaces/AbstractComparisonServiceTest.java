package josch.services.interfaces;

import josch.model.dto.DifferenceDto;
import josch.model.dto.SettingsDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * This tests the public non abstract methods of the {@code AbstractComparisonService} excluding the
 * {@code contains()} method, as these are tested within the implementations. Private and protected
 * methods are helper methods and they are being tested implicitly. Therefore only the syntactical
 * equality is being tested.<br>
 * Note that only valid draft-04 are being passed to this service therefore only valid draft-04 are
 * being used.
 */
public class AbstractComparisonServiceTest {

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
   * Tests for equal schemas.
   */
  @Test
  public void equals_S1EqualsS2() {
    AbstractComparisonService service = getEmptyService();

    // Test the Method.
    DifferenceDto differences = service.equals(S1, S2);

    // Expected results.
    Assertions.assertTrue(differences.hasNone());
    Assertions.assertFalse(differences.hasLeftDifferences());
    Assertions.assertFalse(differences.hasRightDifferences());
  }

  /**
   * Tests for sub-equal schemas.
   */
  @Test
  public void equals_S3SubsetS1() {
    AbstractComparisonService service = getEmptyService();

    // Test the Method.
    DifferenceDto differences = service.equals(S1, S3);

    // Expected results: S1 is superset of S3 -> S1 (left) has differences.
    Assertions.assertFalse(differences.hasNone());
    Assertions.assertTrue(differences.hasLeftDifferences());
    Assertions.assertFalse(differences.hasRightDifferences());
  }

  /**
   * Tests for sub-equal schemas.
   */
  @Test
  public void equals_S4DiffersS1() {
    AbstractComparisonService service = getEmptyService();

    // Test the Method.
    DifferenceDto differences = service.equals(S1, S4);

    // Expected results: both sides have differences
    Assertions.assertFalse(differences.hasNone());
    Assertions.assertTrue(differences.hasLeftDifferences());
    Assertions.assertTrue(differences.hasRightDifferences());
  }

  /**
   * Returns a new comparison service with empty settings that does not use any tools.
   */
  private AbstractComparisonService getEmptyService() {
    return new AbstractComparisonService(new SettingsDto()) {
      @Override
      protected String getMessage(Process p) {
        return null;
      }

      @Override
      public String runScript() {
        return null;
      }
    };
  }
}
