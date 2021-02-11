module josch.services.factory {
    exports josch.services.factory;

    // Requires the interfaces.
    requires josch.services.interfaces;

    // Requires the settings.
    requires josch.model;

    // Requires the implementations.
    requires josch.services.extraction.hackolade;
    requires josch.services.persistency;
    requires josch.services.validation;
    requires josch.services.comparison.jsonsubschema;
    requires josch.services.comparison.ijssubset;
    requires josch.services.extraction.jsi;
}