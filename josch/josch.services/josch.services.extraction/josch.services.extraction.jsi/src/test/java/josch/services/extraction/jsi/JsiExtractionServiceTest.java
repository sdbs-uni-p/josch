package josch.services.extraction.jsi;

import josch.model.dto.CollectionDto;
import josch.model.dto.ExtractionDto;
import josch.model.enums.ESystemConstants;
import josch.test.MongoDB.MongoBaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * This class tests the public methods of the {@code JsiExtractionService} using the jsi tool. The
 * service generates a JSON Schema and a Validator Schema.
 */
public class JsiExtractionServiceTest extends MongoBaseTest {

  JsiExtractionService service;

  /** The JSON schema */
  private static final String JSI_SCHEMA =
      String.join(
          "\n",
          new String[] {
            "{"
                + "  \"$schema\" : \"http://json-schema.org/draft-04/schema#\","
                + "  \"type\" : \"object\","
                + "  \"properties\" : {"
                + "    \"year\" : {"
                + "      \"type\" : \"integer\""
                + "    },"
                + "    \"name\" : {"
                + "      \"anyOf\" : [ {"
                + "        \"type\" : \"object\","
                + "        \"properties\" : {"
                + "          \"nick\" : {"
                + "            \"type\" : \"string\""
                + "          },"
                + "          \"first\" : {"
                + "            \"type\" : \"string\""
                + "          }"
                + "        },"
                + "        \"additionalProperties\" : {"
                + "          \"type\" : \"string\""
                + "        },"
                + "        \"required\" : [ \"nick\", \"first\" ]"
                + "      }, {"
                + "        \"type\" : \"string\""
                + "      } ]"
                + "    },"
                + "    \"language\" : {"
                + "      \"type\" : \"string\""
                + "    },"
                + "    \"_id\" : {"
                + "      \"type\" : \"object\","
                + "      \"properties\" : {"
                + "        \"$oid\" : {"
                + "          \"type\" : \"string\""
                + "        }"
                + "      },"
                + "      \"additionalProperties\" : {"
                + "        \"type\" : \"string\""
                + "      },"
                + "      \"required\" : [ \"$oid\" ]"
                + "    },"
                + "    \"class\" : {"
                + "      \"type\" : [ \"string\", \"integer\" ]"
                + "    }"
                + "  },"
                + "  \"additionalProperties\" : {"
                + "    \"type\" : [ \"string\", \"integer\", \"object\" ]"
                + "  },"
                + "  \"required\" : [ \"year\", \"name\", \"_id\", \"class\" ]"
                + "}"
          });

  /** The JSI_SCHEMA as a proprietary validator */
  private static final String JSI_VALIDATOR =
      "{\"$jsonSchema\":{\"type\":\"object\","
          + "\"properties\":{\"year\":{\"type\":\"number\"},\"name\":{\"anyOf\":[{\"type\":"
          + "\"object\",\"properties\":{\"nick\":{\"type\":\"string\"},\"first\":{\"type\":"
          + "\"string\"}},\"additionalProperties\":{\"type\":\"string\"},\"required\":"
          + "[\"nick\",\"first\"]},{\"type\":\"string\"}]},\"language\":{\"type\":\"string\"},"
          + "\"_id\":{\"properties\":{\"$oid\":{\"type\":\"string\"}},\"additionalProperties\":"
          + "{\"type\":\"string\"},\"required\":[\"$oid\"],\"bsonType\":[\"object\",\"objectId\"]},"
          + "\"class\":{\"type\":[\"string\",\"number\"]}},\"additionalProperties\":{\"type\":"
          + "[\"string\",\"number\",\"object\"]},\"required\":[\"year\",\"name\",\"_id\",\"class\"]"
          + "}}";

  /** Set up the database */
  @BeforeEach
  public void setupService() {
    setup();

    ExtractionDto extractionDto = settings.getExtraction();
    extractionDto.setDbInstance("localhost");
    extractionDto.setMethod("absolute");
    extractionDto.setSize(100);
    extractionDto.setContainer(DATABASE);
    extractionDto.setEntity(COLLECTION);
    settings.setExtraction(extractionDto);

    service = new JsiExtractionService(settings);
  }

  /** Tests whether a json schema draft-04 gets retrieved using JSI */
  @Test
  public void getJsonSchema_validContainer() {
    String schema = service.getJsonSchema();
    Assertions.assertEquals(JSI_SCHEMA.replace(" ", ""), schema);
  }

  /** Tests whether a MongoDB JSON schema gets retrieved using JSI. */
  @Test
  public void getValidator() {
    String schema = service.getValidator();
    Assertions.assertEquals(JSI_VALIDATOR, schema);
  }

  /** Tests whether no schema gets retrieved when the input is flawed. */
  @Test
  public void getSchema_invalidInput() {
    List<String> results = new ArrayList<>();

    // Flawed settings
    CollectionDto collectionDto = settings.getCollection();
    collectionDto.setName("IDoNotExist");
    settings.setCollection(collectionDto);
    service = new JsiExtractionService(settings);

    // Actual tests.
    results.add(service.getJsonSchema()); // Negative test flawed container JSON Schema.
    results.add(service.getValidator()); // Negative test flawed container validator.

    for (String result : results) {
      Assertions.assertTrue(result.contains(ESystemConstants.ERROR.getValue()));
    }
  }
}
