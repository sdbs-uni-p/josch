package josch.presentation.gui.controller;

import josch.services.factory.AbstractServiceFactory;
import josch.services.interfaces.AbstractComparisonService;
import josch.services.interfaces.IDatabaseService;
import josch.services.interfaces.IExtractionService;
import josch.services.interfaces.IValidationService;

/**
 * This AbstractDatabaseController class is used as a super class for individual Controllers that
 * are interacting with the underlying database.
 *
 * @author Kai Dauberschmidt
 */
public abstract class AbstractDatabaseController extends AbstractController {

  protected static final String DRAFT = String.join("\n", new String[] {
          "{" +
                  "  \"$schema\": \"http://json-schema.org/draft-04/schema#\"," +
                  "  \"description\": \"Modified JSON Schema draft v4 that includes the optional '$ref' and 'format'\"," +
                  "  \"definitions\": {" +
                  "    \"schemaArray\": {" +
                  "      \"type\": \"array\"," +
                  "      \"minItems\": 1," +
                  "      \"items\": { \"$ref\": \"#\" }" +
                  "    }," +
                  "    \"positiveInteger\": {" +
                  "      \"type\": \"integer\"," +
                  "      \"minimum\": 0" +
                  "    }," +
                  "    \"positiveIntegerDefault0\": {" +
                  "      \"allOf\": [ { \"$ref\": \"#/definitions/positiveInteger\" }, { \"default\": 0 } ]" +
                  "    }," +
                  "    \"simpleTypes\": {" +
                  "      \"enum\": [ \"array\", \"boolean\", \"integer\", \"null\", \"number\", \"object\", \"string\" ]" +
                  "    }," +
                  "    \"stringArray\": {" +
                  "      \"type\": \"array\"," +
                  "      \"items\": { \"type\": \"string\" }," +
                  "      \"minItems\": 1," +
                  "      \"uniqueItems\": true" +
                  "    }" +
                  "  }," +
                  "  \"type\": \"object\"," +
                  "  \"properties\": {" +
                  "    \"id\": {" +
                  "      \"type\": \"string\"," +
                  "      \"format\": \"uri\"" +
                  "    }," +
                  "    \"$schema\": {" +
                  "      \"type\": \"string\"," +
                  "      \"format\": \"uri\"" +
                  "    }," +
                  "    \"$ref\": {" +
                  "      \"type\": \"string\"" +
                  "    }," +
                  "    \"format\": {" +
                  "      \"type\": \"string\"" +
                  "    }," +
                  "    \"title\": {" +
                  "      \"type\": \"string\"" +
                  "    }," +
                  "    \"description\": {" +
                  "      \"type\": \"string\"" +
                  "    }," +
                  "    \"default\": { }," +
                  "    \"multipleOf\": {" +
                  "      \"type\": \"number\"," +
                  "      \"minimum\": 0," +
                  "      \"exclusiveMinimum\": true" +
                  "    }," +
                  "    \"maximum\": {" +
                  "      \"type\": \"number\"" +
                  "    }," +
                  "    \"exclusiveMaximum\": {" +
                  "      \"type\": \"boolean\"," +
                  "      \"default\": false" +
                  "    }," +
                  "    \"minimum\": {" +
                  "      \"type\": \"number\"" +
                  "    }," +
                  "    \"exclusiveMinimum\": {" +
                  "      \"type\": \"boolean\"," +
                  "      \"default\": false" +
                  "    }," +
                  "    \"maxLength\": { \"$ref\": \"#/definitions/positiveInteger\" }," +
                  "    \"minLength\": { \"$ref\": \"#/definitions/positiveIntegerDefault0\" }," +
                  "    \"pattern\": {" +
                  "      \"type\": \"string\"," +
                  "      \"format\": \"regex\"" +
                  "    }," +
                  "    \"additionalItems\": {" +
                  "      \"anyOf\": [" +
                  "        { \"type\": \"boolean\" }," +
                  "        { \"$ref\": \"#\" }" +
                  "      ]," +
                  "      \"default\": { }" +
                  "    }," +
                  "    \"items\": {" +
                  "      \"anyOf\": [" +
                  "        { \"$ref\": \"#\" }," +
                  "        { \"$ref\": \"#/definitions/schemaArray\" }" +
                  "      ]," +
                  "      \"default\": { }" +
                  "    }," +
                  "    \"maxItems\": { \"$ref\": \"#/definitions/positiveInteger\" }," +
                  "    \"minItems\": { \"$ref\": \"#/definitions/positiveIntegerDefault0\" }," +
                  "    \"uniqueItems\": {" +
                  "      \"type\": \"boolean\"," +
                  "      \"default\": false" +
                  "    }," +
                  "    \"maxProperties\": { \"$ref\": \"#/definitions/positiveInteger\" }," +
                  "    \"minProperties\": { \"$ref\": \"#/definitions/positiveIntegerDefault0\" }," +
                  "    \"required\": { \"$ref\": \"#/definitions/stringArray\" }," +
                  "    \"additionalProperties\": {" +
                  "      \"anyOf\": [" +
                  "        { \"type\": \"boolean\" }," +
                  "        { \"$ref\": \"#\" }" +
                  "      ]," +
                  "      \"default\": { }" +
                  "    }," +
                  "    \"definitions\": {" +
                  "      \"type\": \"object\"," +
                  "      \"additionalProperties\": { \"$ref\": \"#\" }," +
                  "      \"default\": { }" +
                  "    }," +
                  "    \"properties\": {" +
                  "      \"type\": \"object\"," +
                  "      \"additionalProperties\": { \"$ref\": \"#\" }," +
                  "      \"default\": { }" +
                  "    }," +
                  "    \"patternProperties\": {" +
                  "      \"type\": \"object\"," +
                  "      \"additionalProperties\": { \"$ref\": \"#\" }," +
                  "      \"default\": { }" +
                  "    }," +
                  "    \"dependencies\": {" +
                  "      \"type\": \"object\"," +
                  "      \"additionalProperties\": {" +
                  "        \"anyOf\": [" +
                  "          { \"$ref\": \"#\" }," +
                  "          { \"$ref\": \"#/definitions/stringArray\" }" +
                  "        ]" +
                  "      }" +
                  "    }," +
                  "    \"enum\": {" +
                  "      \"type\": \"array\"," +
                  "      \"minItems\": 1," +
                  "      \"uniqueItems\": true" +
                  "    }," +
                  "    \"type\": {" +
                  "      \"anyOf\": [" +
                  "        { \"$ref\": \"#/definitions/simpleTypes\" }," +
                  "        {" +
                  "          \"type\": \"array\"," +
                  "          \"items\": { \"$ref\": \"#/definitions/simpleTypes\" }," +
                  "          \"minItems\": 1," +
                  "          \"uniqueItems\": true" +
                  "        }" +
                  "      ]" +
                  "    }," +
                  "    \"allOf\": { \"$ref\": \"#/definitions/schemaArray\" }," +
                  "    \"anyOf\": { \"$ref\": \"#/definitions/schemaArray\" }," +
                  "    \"oneOf\": { \"$ref\": \"#/definitions/schemaArray\" }," +
                  "    \"not\": { \"$ref\": \"#\" }" +
                  "  }," +
                  "  \"dependencies\": {" +
                  "    \"exclusiveMaximum\": [ \"maximum\" ]," +
                  "    \"exclusiveMinimum\": [ \"minimum\" ]" +
                  "  }," +
                  "  \"default\": { }" +
                  "}"
  });

  /**
   * Gets an extractionService
   */
  IExtractionService getExtractionService() {
    return AbstractServiceFactory.getExtractionService(getSettings());
  }

  /**
   * Gets a database service
   */
  IDatabaseService getDbService() {
    return AbstractServiceFactory.getDatabaseService(getSettings());
  }

  /**
   * Gets a validation service
   */
  IValidationService getValidationService() {
    return AbstractServiceFactory.getValidationService(getSettings());
  }

  /**
   * Gets a comparison service.
   */
  AbstractComparisonService getComparisonService() {
    return AbstractServiceFactory.getComparisonService(getSettings());
  }
}
