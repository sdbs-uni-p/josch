/**
 * This is the module info for the gui.model, i.e. the extra javafx classes, validators and such.
 */
module josch.presentation.gui.model {

  // the module must interact with javafx
  requires javafx.controls;

  // javafx gains knowledge about the module's members
  opens josch.presentation.gui.model.fields to
      javafx.fxml;

  // allow them to be used in the view and controller.
  exports josch.presentation.gui.model.fields to
      josch.presentation.gui.controller;
  exports josch.presentation.gui.model.validator to
      josch.presentation.gui.controller;
  exports josch.presentation.gui.model.utils;
}
