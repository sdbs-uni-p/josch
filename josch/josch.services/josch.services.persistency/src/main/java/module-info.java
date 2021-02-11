module josch.services.persistency {

    // Show to factory.
    exports josch.services.persistency to josch.services.factory, josch.services.extraction.jsi;

    // Externals required for implementation
    requires com.google.gson;
    requires org.mongodb.bson;

    // Requires interface methods, DTOs, system constants and access to the persistency layer.
    requires josch.services.interfaces;
    requires josch.model;
    requires josch.persistency.interfaces;
    requires josch.persistency.factory;
}