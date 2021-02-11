module josch.services.validation {

    // Show to factory.
    exports josch.services.validation to josch.services.factory;

    // External sources
    requires org.everit.json.schema;
    requires json;

    // Requires its interface and access to persistency layer.
    requires josch.services.interfaces;
    requires josch.persistency.interfaces;
    requires josch.persistency.factory;

    // Requires the dto and system constants.
    requires josch.model;
}