/** The module info for the mongodb client implementation. */
module josch.persistency.implementation.mongodb {

    // The client requires the driver
    requires org.mongodb.driver.core;
    requires org.mongodb.driver.sync.client;

    // The client requires some DTOs and datatype.
    requires josch.model;
    requires org.mongodb.bson;

    // The client requires it's interface.
    requires josch.persistency.interfaces;
    requires json;

    // And shows its public and protected stuff to the factory.
    exports josch.persistency.implementation.mongodb to josch.persistency.factory;
}
