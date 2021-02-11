/**
 * The module info for the client factory.
 */
module josch.persistency.factory {

    // The factory requires the DTOs
    requires josch.model;

    // The factory requires the interface and implementation.
    requires josch.persistency.interfaces;
    requires josch.persistency.implementation.mongodb;

    // The factory shows it's content to the service.
    exports josch.persistency.factory to josch.services.persistency, josch.services.validation, josch.services.extraction.jsi;
}