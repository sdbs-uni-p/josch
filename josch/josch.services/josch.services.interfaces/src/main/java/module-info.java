module josch.services.interfaces {

    // System constants
    requires josch.model;

    // Externals for abstract classes.
    requires com.google.gson;
    requires json;
    requires com.google.common;

    // Show the interfaces
    exports josch.services.interfaces;
}