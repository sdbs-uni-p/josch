package josch.services.interfaces;

import josch.model.dto.ValidationDto;

/**
 * This {@code IValidationService} provides an interface to validate a JSON Document against a JSON
 * schema.
 *
 * @author Kai Dauberschmidt
 */
public interface IValidationService {

    /**
     * Validates a given JSON document against a given JSON schema and returns a message that
     * indicates whether the document is valid or not.
     *
     * @param schema The schema to validate against.
     * @param document The document to validate.
     * @return A message to indicate whether the document is valid or not.
     */
    String validate(String schema, String document);

    /**
     * Validates all JSON Documents of the collection against a given JSON schema and returns a
     * message that the amount of documents validate against the schema.
     *
     * @param schema The schema to validate against.
     * @return A message to indicate the amount of valid documents.
     */
    ValidationDto validate(String schema);
}
