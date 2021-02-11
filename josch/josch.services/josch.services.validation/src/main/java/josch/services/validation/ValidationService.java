package josch.services.validation;

import josch.model.dto.CollectionDto;
import josch.model.dto.SettingsDto;
import josch.model.dto.ValidationDto;
import josch.model.enums.EDatabaseSystems;
import josch.model.enums.ESystemConstants;
import josch.persistency.factory.AbstractClientFactory;
import josch.persistency.interfaces.IClient;
import josch.services.interfaces.IValidationService;
import org.everit.json.schema.Schema;
import org.everit.json.schema.SchemaException;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Iterator;

/**
 * This {@code validationService} is a service to validate JSON documents as Strings against schemas
 * as Strings. There are three cases:
 *
 * <ul>
 *   <li>The schema is a $jsonSchema. In this case the validation is handled server-side.
 *   <li>The schema is a valid JSON Schema of draft-07, -06 or -04. These are the drafts that are
 *       being supported by the used framework <em>Justify</em>. All frameworks on json-schema.org
 *       only support up to these drafts. They are validated on spot.
 *   <li>Other schemas, like other drafts and pseudo schemas. These are not being validated against
 *       because no Java Framework supports them.
 * </ul>
 *
 * @author Kai Dauberschmidt
 */
public class ValidationService implements IValidationService {

  /** The settings required to connect to a database */
  private final SettingsDto settings;

  /** The constructor with settings. Used in the actual implementation. */
  public ValidationService(SettingsDto settings) {
    this.settings = settings;
  }

  /** {@inheritDoc} */
  public String validate(String document, String schema) {
    String message;

    // If this is a MongoDB specific validation let the database do the work.
    if (schema.contains("$jsonSchema")) {
      assert settings != null;
      IClient client = AbstractClientFactory.getClient(settings);
      message = client.validate(document, schema);
      client.close();

      // If this is a JSON Schema according to drafts
    } else if (schema.contains("$schema")
        && (schema.contains("json-schema.org/draft-04/schema")
            || schema.contains("json-schema.org/draft-06/schema")
            || schema.contains("json-schema.org/draft-07/schema"))) {
      try {
        // Get JSON Objects
        JSONObject objectSchema = new JSONObject(schema);
        JSONObject objectDocument = new JSONObject(document);

        // Validate
        Schema jsonSchema = SchemaLoader.load(objectSchema);
        jsonSchema.validate(objectDocument);
        message = "The schema validates this document.";
      } catch (ValidationException | SchemaException | JSONException e) {
        e.printStackTrace();
        message = ESystemConstants.ERROR.getValue() + e.getMessage();
      }
    } else {
      message = ESystemConstants.ERROR.getValue() + "The schema entered is not being supported.";
    }

    return message;
  }

  /** {@inheritDoc}
   * @return*/
  public ValidationDto validate(String schema) {

    String message = "";
    boolean hasError = false;
    ValidationDto result;

    // Get the collection and client.
    assert settings != null;
    CollectionDto collection = settings.getCollection();
    IClient client = AbstractClientFactory.getClient(settings);

    // Define numbers that are used as relative indication.
    long amountDocuments = collection.getCount();
    long invalidDocuments;

    // If MongoDB System and MongoDB validator let MongoDB validate.
    if (settings.getDbms().equals(EDatabaseSystems.MONGO) && isMongoSchema(schema)) {
      result = client.validateAll(collection.getName(), schema);
      client.close();

      // If the schema is supported: validate manually.
    } else if (isJSONSchema(schema)) {
      result = new ValidationDto();
      invalidDocuments = 0;

      // Get the schema as a Schema object.
      try {
        Schema jsonSchema = SchemaLoader.load(new JSONObject(schema));
        Iterator<String> it = client.getDocumentIterator(collection.getName(), false);
        String currentDocument;
        JSONObject document;

        // Check each object.
        do {
          currentDocument = it.next();
          if (currentDocument != null) {
            document = new JSONObject(currentDocument);
            try {
              jsonSchema.validate(document);
            } catch (ValidationException e) {
              result.addInvalid(currentDocument);
              invalidDocuments++;
            }
          }
        } while (it.hasNext());

        result.setAmountInvalidDocuments(invalidDocuments);
      } catch (JSONException | SchemaException e) {
        e.printStackTrace();
        hasError = true;
        message = ESystemConstants.ERROR.getValue() + e.getMessage();
      }


    } else {
      result = new ValidationDto();
      hasError = true;
      message = ESystemConstants.ERROR.getValue() + "The schema entered is not being supported.";
    }

    // The result message.
    if (!hasError && result.getAmountInvalidDocuments() > 0) {
      long validDocuments = amountDocuments - result.getAmountInvalidDocuments();
      message =
          "Out of "
              + amountDocuments
              + " documents validate "
              + validDocuments
              + " against this schema. (That's "
              + percent(validDocuments, amountDocuments)
              + "%.)";
    } else if(!hasError) {
      message = "All documents in this collection validate against the schema.";
    }

    client.close();
    result.setNotification(message);
    return result;
  }

  /**
   * Helper method to determine whether a given schema is a supported JSON Schema. The drafts 04, 06
   * and 07 are currently supported.
   *
   * @param schema The schema as a string to inspect.
   * @return {@code true} if the schema is valid, {@code false} else.
   */
  private boolean isJSONSchema(String schema) {
    return schema.contains("$schema")
        && (schema.contains("json-schema.org/draft-04/schema")
            || schema.contains("json-schema.org/draft-06/schema")
            || schema.contains("json-schema.org/draft-07/schema"));
  }

  /**
   * Helper method to determine whether a given schema is a MongoDB Validator schema.
   *
   * @param schema The schema as a string to inspect.
   * @return {@code true} if the schema is a MongoDB validator schema, {@code false} else.
   */
  private boolean isMongoSchema(String schema) {
    return schema.contains("$jsonSchema");
  }

  /**
   * Helper method to do a percentage calculation. Calculates the percentage (p) with a given base
   * value, i.e. the set as a whole (G) and a given percent value, i.e. members of a set with a
   * desired attribute (W) in the following manor: p = W / G.
   *
   * @param W The amount of set members with a certain attribute, i.e. the percent value.
   * @param G The whole set, i.e. the base value.
   * @return The percentage as in a dd.dd format.
   */
  private double percent(long W, long G) {
    double value = ((double) W / G) * 100;
    BigDecimal bigDecimal = new BigDecimal(Double.toString(value));
    bigDecimal = bigDecimal.setScale(2, RoundingMode.HALF_UP);
    return bigDecimal.doubleValue();
  }
}
