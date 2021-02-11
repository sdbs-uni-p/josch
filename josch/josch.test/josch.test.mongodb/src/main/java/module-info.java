@SuppressWarnings("Java9RedundantRequiresStatement")
module josch.test.mongodb {

    requires josch.test.interfaces;
    requires org.mongodb.driver.sync.client;
    requires org.mongodb.bson;
    requires josch.model;
    requires org.mongodb.driver.core;
    exports  josch.test.MongoDB;
}