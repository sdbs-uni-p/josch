/** This is the module info for the gui.controller. */
module josch.presentation.gui.controller {
  // The controller can use the DTOs.
  requires josch.model;

  // The controller must access the model.

  // the controller interacts with the service interfaces.
  requires josch.services.interfaces;

  // The controller interacts with the service factory.
  requires josch.services.factory;

  // The controller interacts with the JavaFX framework
  requires javafx.fxml;
  requires javafx.graphics;
  requires javafx.controls;

  // Document interaction.
  requires com.google.gson;
  requires josch.presentation.gui.model;
  requires json;
  requires org.everit.json.schema;
  requires org.fxmisc.richtext;
  requires reactfx;
  requires flowless;
  requires java.prefs;

  // allows to use their public methods elsewhere. TODO reflect necessity
  exports josch.presentation.gui;

  // javafx should learn about all members in that package.
  opens josch.presentation.gui to
          javafx.fxml;
  opens josch.presentation.gui.controller to
          javafx.fxml;
}
