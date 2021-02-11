package josch.presentation.gui.controller;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.util.Callback;
import josch.model.dto.ConnectionInfoDto;
import josch.model.dto.DatabaseDto;
import josch.model.enums.EViews;
import josch.services.factory.AbstractServiceFactory;
import josch.services.interfaces.IDatabaseService;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/** This class is used as the controller for the databaseOverview view. */
public class DatabaseOverviewController extends AbstractDatabaseController {

  /** This is the caption of the database overview */
  @FXML
  private Text cptDatabases;

  /** THis is the grid of the database overview */
  @FXML private TableView<DatabaseDto> grdDatabases;

  /** {@inheritDoc} */
  @Override
  public void initialize(URL url, ResourceBundle rb) {
    // Disable selection
    grdDatabases.setSelectionModel(null);

    // Create column Database Name (String)
    TableColumn<DatabaseDto, String> colName = new TableColumn<>("Database");

    // Create column Collection Count (int)
    TableColumn<DatabaseDto, Integer> colCount = new TableColumn<>("Collections");

    // Add the columns to the table.
    grdDatabases.getColumns().add(colName);
    grdDatabases.getColumns().add(colCount);

    // Adjust column width
    ReadOnlyDoubleProperty gridWidth = grdDatabases.widthProperty();
    colName.prefWidthProperty().bind(gridWidth.divide(2));
    colCount.prefWidthProperty().bind(gridWidth.divide(4));

    // Align count center
    colCount.setStyle("-fx-alignment: CENTER-RIGHT; ");

    // Get the database DTOs.
    IDatabaseService service = AbstractServiceFactory.getDatabaseService(getSettings());
    try {
      List<DatabaseDto> databaseDtos = service.getAllDatabases();

      // Add data to show.
      ObservableList<DatabaseDto> databaseObservableList =
          FXCollections.observableList(databaseDtos);

      // Associate data with columns.
      colName.setCellValueFactory(new PropertyValueFactory<>("name"));
      colCount.setCellValueFactory(new PropertyValueFactory<>("collectionCount"));

      // Add the data into the table.
      grdDatabases.setItems(databaseObservableList);

      // Add the buttons
      addButtons();
    } catch (IllegalStateException e) {
      handle(e);
    }
  }

  /** Adds the "select" button to the corresponding Column. */
  private void addButtons() {

    // Create column select
    TableColumn<DatabaseDto, Void> slcBtn = new TableColumn<>("Show");

    // Create a cell factory
    Callback<TableColumn<DatabaseDto, Void>, TableCell<DatabaseDto, Void>> cellFactory =
        new Callback<>() {

          @Override
          public TableCell<DatabaseDto, Void> call(TableColumn<DatabaseDto, Void> param) {

            // Create a concrete cell
            return new TableCell<>() {

              // The cell contains the button.
              private final Button btnSelect = new Button();

              // The button's method and style.
              {
                btnSelect.getStyleClass().add("i-next");
                btnSelect.getStyleClass().add("ico-sm");

                btnSelect.setOnAction(
                    (ActionEvent event) -> {
                      // Get the database name
                      String db = getTableView().getItems().get(getIndex()).getName();

                      // Update the getSettings()
                      getSettings().getExtraction().setContainer(db);
                      ConnectionInfoDto cInfo = getSettings().getConnectionInfo();
                      cInfo.setDatabase(db);
                      getSettings().setConnectionInfo(cInfo);

                      // Set the root to this db.
                      MainController.getInstance().loadComponent(EViews.COLLECTION_LIST);
                    });
              }

              // show the actual button
              @Override
              public void updateItem(Void item, boolean isEmpty) {
                super.updateItem(item, isEmpty);
                if (isEmpty) {
                  setGraphic(null);
                } else {
                  setGraphic(btnSelect);
                }
              }
            };
          }
        };

    // Set the Cell factory and add the button to the table.
    slcBtn.setCellFactory(cellFactory);
    grdDatabases.getColumns().add(slcBtn);

    // Adjust column width
    slcBtn.prefWidthProperty().bind(grdDatabases.widthProperty().divide(4));

    // center align this column.
    slcBtn.setStyle("-fx-alignment: CENTER; ");
  }
}
