module josch.services.extraction.jsi {
    exports josch.services.extraction.jsi;

    requires josch.services.interfaces;
    requires json.schema.inferrer;
    requires com.fasterxml.jackson.databind;
    requires josch.model;
    requires josch.services.persistency;
    requires josch.persistency.factory;
    requires josch.persistency.interfaces;
}