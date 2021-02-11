package josch.presentation.gui.controller;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;
import josch.model.dto.CollectionDto;
import josch.model.dto.ExtractionDto;
import josch.model.enums.EViews;
import josch.services.factory.AbstractServiceFactory;
import josch.services.interfaces.IDatabaseService;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/** This class is the controller for the collectionOverview view. */
public class CollectionOverviewController extends AbstractDatabaseController {

  /** This is the caption of the collection overview */
  @FXML private Text cptCollections;

  /** This is the grid of the collection overview */
  @FXML private TableView<CollectionDto> grdCollections;

  /** The box that contains the table */
  @FXML private VBox boxBase;

  /** The anchor of the application */
  @FXML private AnchorPane anchor;

  @FXML private Button btnBack;

  /** {@inheritDoc} */
  @Override
  public void initialize(URL url, ResourceBundle rb) {
    // Disable selection
    grdCollections.setSelectionModel(null);

    // Create column collection Name (String)
    TableColumn<CollectionDto, String> colName = new TableColumn<>("Collection");

    // Create column Document Count (Long)
    TableColumn<CollectionDto, Long> colCount = new TableColumn<>("Documents");

    // Create column Validator (String)
    TableColumn<CollectionDto, String> colVal = new TableColumn<>("Validator");

    // Add the columns to the table.
    grdCollections.getColumns().add(colName);
    grdCollections.getColumns().add(colCount);
    grdCollections.getColumns().add(colVal);

    // Adjust column width
    ReadOnlyDoubleProperty gridWidth = grdCollections.widthProperty();
    colName.prefWidthProperty().bind(gridWidth.divide(4));
    colCount.prefWidthProperty().bind(gridWidth.divide(4));
    colVal.prefWidthProperty().bind(gridWidth.divide(4));

    // Alignment in columns
    colCount.getStyleClass().add("align-center-right");
    colVal.getStyleClass().add("align-center");

    // Get the DTOs.
    IDatabaseService service = AbstractServiceFactory.getDatabaseService(getSettings());
    try {
    List<CollectionDto> collectionDtos = service.getAllCollections();

    // Add data to show.
    ObservableList<CollectionDto> collectionObservableList =
            FXCollections.observableList(collectionDtos);

    // Associate data with columns.
    colName.setCellValueFactory(new PropertyValueFactory<>("name"));
    colCount.setCellValueFactory(new PropertyValueFactory<>("count"));
    colVal.setCellValueFactory(
            cellData -> {
              boolean hasValidator = cellData.getValue().hasValidator();
              String validationActive = (hasValidator) ? "has validator" : "";
              return new ReadOnlyStringWrapper(validationActive);
            });

    // Add the data into the table.
    grdCollections.setItems(collectionObservableList);

    // Add the buttons
    addDetailsButtons();
    } catch (IllegalStateException e) {
      handle(e);
    }
  }

  /**
   * Adds the "show" buttons for the details buttons.
   */
  private void addDetailsButtons() {
    // Create column
    TableColumn<CollectionDto, Void> colDetails = new TableColumn<>("Show");

    // Create a cell factory
    Callback<TableColumn<CollectionDto, Void>, TableCell<CollectionDto, Void>> cellFactory =
        new Callback<>() {

          @Override
          public TableCell<CollectionDto, Void> call(TableColumn<CollectionDto, Void> param) {

            // Create a concrete cell
            return new TableCell<>() {

              // The cell contains the button.
              private final Button btnShow = new Button();

              { // The button's method and style.
                btnShow.getStyleClass().add("i-next");
                btnShow.getStyleClass().add("ico-sm");

                btnShow.setOnAction(
                    (ActionEvent event) -> {
                      // Set the collection to pass
                      CollectionDto collectionDto = getTableView().getItems().get(getIndex());
                      getSettings().setCollection(collectionDto);

                      // Set the extraction details.
                      ExtractionDto extractionDto = getSettings().getExtraction();
                      extractionDto.setEntity(getSettings().getCollection().getName());

                      MainController.getInstance().loadComponent(EViews.COLLECTION);
                    });
              }

              // show the actual button
              @Override
              public void updateItem(Void item, boolean isEmpty) {
                super.updateItem(item, isEmpty);
                if (isEmpty) {
                  setGraphic(null);
                } else {
                  setGraphic(btnShow);
                }
              }
            };
          }
        };

    // Set the Cell factory and add the button to the table.
    colDetails.setCellFactory(cellFactory);
    grdCollections.getColumns().add(colDetails);

    // Adjust column width
    colDetails.prefWidthProperty().bind(grdCollections.widthProperty().divide(4));

    // center align this column.
    colDetails.getStyleClass().add("align-center");
  }
}
