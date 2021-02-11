package josch.presentation.gui.controller;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import josch.model.dto.ValidationDto;
import josch.model.enums.ESystemConstants;
import josch.model.enums.EThemes;
import josch.presentation.gui.App;
import josch.presentation.gui.model.utils.EDialogueTypes;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.prefs.Preferences;

/**
 * This {@code DialogueController} class controls the creation of dialogue windows. There are
 * basically 3 types of dialogue windows of which two are also present in the respective enum.
 *
 * @author Kai Dauberschmidt
 * @see EDialogueTypes
 */
public class DialogueController extends AbstractController {

    /**
     * The overall layout
     */
    @FXML
    private GridPane layout;

    /**
     * The icon button
     */
    @FXML
    private Button icon;

    /**
     * The notification text
     */
    @FXML
    private Text notification;

    /**
     * A boolean to indicate whether processes should be executed or not.
     */
    private static boolean proceed;

    /**
     * Prohibit the instantiation of this class.
     */
    private DialogueController() {
    }

    /**
     * Builds a popup stage with a given text that informs the user about what is going on. This
     * information is emphasized by the icon to display.
     *
     * @param text The text to display the user.
     * @param icon The icon of the dialogue window that emphasizes the information.
     * @param type The type decides which elements are present.
     * @return The stage to show.
     */
    private static Stage buildStage(String text, Node icon, EDialogueTypes type) {
        return buildStage(text, null, icon, type);
    }

    /**
     * Builds a popup stage with a given text that informs the user about what is going on. This
     * information is emphasized by the icon to display.
     *
     * @param text The text to display the user.
     * @param dto  A ValidationDto if existent.
     * @param icon The icon of the dialogue window that emphasizes the information.
     * @param type The type decides which elements are present.
     * @return The stage to show.
     */
    private static Stage buildStage(String text, ValidationDto dto, Node icon, EDialogueTypes type) {

        // Setup the stage.
        Stage stage = new DialogueStage();
        stage.initStyle(StageStyle.UNDECORATED);
        stage.initModality(Modality.APPLICATION_MODAL);

        // Decide whether a show all is required.
        boolean hasShowButton = dto != null && dto.getAmountInvalidDocuments() > 0;

        // Create an instance of this controller.
        DialogueController controller = new DialogueController();

        // Load the view.
        FXMLLoader loader = new FXMLLoader(App.class.getResource("view/DialogueView.fxml"));
        loader.setController(controller);

        try {
            // Set the scene.
            Scene scene = new Scene(loader.load(), 600, 200);
            scene.getStylesheets().add(App.class.getResource("view/css/main.css").toExternalForm());
            scene.getStylesheets().add(App.class.getResource("view/css/icons.css").toExternalForm());
            Preferences prefs = OptionsController.getPreferences();
            EThemes theme = EThemes.getTheme(prefs.get("theme", "default"));
            scene.getStylesheets().add(App.class.getResource(theme.getStylesheet()).toExternalForm());
            stage.setScene(scene);

            // Set the basic content.
            if (dto == null) {
                controller.notification.setText(text);
            } else {
                controller.notification.setText(dto.getNotification());
            }
            controller.layout.add(icon, 1, 1);

            // Define the ok button
            Button btnOk = new Button("Ok");
            btnOk.setOnAction(
                    e -> {
                        proceed = true;
                        stage.close();
                    });

            // Define the cancel button.
            Button btnCancel = new Button("Cancel");
            btnCancel.setOnAction(
                    e -> {
                        proceed = false;
                        stage.close();
                    });

            // Define the show button.
            Button btnShow = new Button("Show all");
            if (hasShowButton) {
                btnShow.setOnAction(e -> controller.showInvalid(dto));
            }

            // decide which buttons are present and in which order they are presented.
            switch (type) {
                case INFORMATION -> {
                    controller.layout.add(btnOk, 3, 2);
                    GridPane.setHalignment(btnOk, HPos.RIGHT);
                }
                case CONFIRMATION -> {
                    controller.layout.add(btnOk, 2, 2);
                    GridPane.setHalignment(btnOk, HPos.LEFT);
                    controller.layout.add(btnCancel, 3, 2);
                    GridPane.setHalignment(btnCancel, HPos.RIGHT);
                }
                case VALIDATION -> {
                    controller.layout.add(btnOk, 2, 2);
                    GridPane.setHalignment(btnOk, HPos.LEFT);
                    if (hasShowButton) {
                        controller.layout.add(btnShow, 3, 2);
                        GridPane.setHalignment(btnShow, HPos.RIGHT);
                    }
                }
                default -> {
                    GridPane.setRowSpan(icon, 2);
                    GridPane.setRowSpan(controller.notification, 2);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stage;
    }

    /**
     * This method is used to notify users with a popup and a message. The popup differs whether the
     * message is an error message (as indicated by containing the system constant for error messages)
     * or an information message. <br>
     * In case of an error the popup has to be closed by a button, whereas informational popups close
     * after 5 seconds automatically.
     *
     * @param message The message for the user.
     */
    public static void inform(String message) {
        boolean isError = message.contains(ESystemConstants.ERROR.getValue());

        // Define the symbol: get different svg paths for error or information.
        String shape = (isError) ? "i-cross" : "i-info";
        Button icon = new Button();
        icon.getStyleClass().addAll("ico-inform", shape);

        // Build the stage.
        Stage stgNotify = DialogueController.buildStage(message, icon, EDialogueTypes.INFORMATION);

        stgNotify.show();
    }

    /**
     * This method is used to notify users with a popup. It has an additional button to show all
     * invalid documents.
     * *
     *
     * @param dto The encapsulated information to show.
     */
    public static void inform(ValidationDto dto) {
        boolean isError = dto.getNotification().contains(ESystemConstants.ERROR.getValue());

        // Define the symbol: get different svg paths for error or information.
        String shape = (isError) ? "i-cross" : "i-info";
        Button icon = new Button();
        icon.getStyleClass().addAll("ico-inform", shape);

        // Build the stage.
        Stage stgNotify = DialogueController.buildStage(null, dto, icon, EDialogueTypes.VALIDATION);

        stgNotify.show();
    }

    /**
     * This method is used for the execution of a background process {@code backgroundProcess} while
     * showing a wait dialogue. At first the dialogue is being defined and {@code backgroundProcess}
     * is being executed from within another process. <br>
     * This is preferred over execution in a new {@code Thread} and waiting for it to finish. The
     * reason is that if the thread fails the whole application has to be killed. The task however
     * just closes the window on failure.
     *
     * @param backgroundProcess The method that implements the background process.
     */
    public static void process(Callable<String> backgroundProcess, Callable<ValidationDto> ValidationProcess) {

        // Define content of the progress popup.
        ProgressIndicator icon = new ProgressIndicator();
        String message = "Processing. Please wait.";

        Stage stgPopupWait = buildStage(message, icon, EDialogueTypes.PROCESS);

        /* Step 2: Define the task for the background process */
        @SuppressWarnings("rawtypes")
        Task task;
        if (backgroundProcess != null) {
           task =
                    new Task<String>() {

                        // Close the dialogue, no matter what happened.
                        {
                            setOnFailed(
                                    a -> {
                                        // An error occurred: Print and inform
                                        Throwable e = getException();
                                        e.printStackTrace();
                                        stgPopupWait.close();
                                        inform(ESystemConstants.ERROR.getValue() + e.getMessage());
                                    });
                            setOnSucceeded(
                                    a -> {
                                        stgPopupWait.close();
                                        String result = getValue();
                                        if (result != null) {
                                            inform(result);
                                        }
                                    });
                            setOnCancelled(
                                    a -> {
                                        stgPopupWait.close();
                                        inform(ESystemConstants.ERROR.getValue() + "Executed task was cancelled.");
                                    });
                        }

                        // execute the background process and return null (because no return is required).
                        @Override
                        protected String call() throws Exception {
                            return backgroundProcess.call();
                        }
                    };
        } else {
            task =
                    new Task<ValidationDto>() {

                        // Close the dialogue, no matter what happened.
                        {
                            setOnFailed(
                                    a -> {
                                        // An error occurred: Print and inform
                                        Throwable e = getException();
                                        e.printStackTrace();
                                        stgPopupWait.close();
                                        inform(ESystemConstants.ERROR.getValue() + e.getMessage());
                                    });
                            setOnSucceeded(
                                    a -> {
                                        stgPopupWait.close();
                                        ValidationDto result = getValue();
                                        if (result != null) {
                                            inform(result);
                                        }
                                    });
                            setOnCancelled(
                                    a -> {
                                        stgPopupWait.close();
                                        inform(ESystemConstants.ERROR.getValue() + "Executed task was cancelled.");
                                    });
                        }

                        // execute the background process and return null (because no return is required).
                        @Override
                        protected ValidationDto call() throws Exception {
                            return ValidationProcess.call();
                        }
                    };
        }


        // bind the progress spinning wheel to the task.
        icon.progressProperty().bind(task.progressProperty());

        // execute all.
        Thread taskThread = new Thread(task);
        taskThread.start();
        stgPopupWait.showAndWait();
    }

    /**
     * This method is used to execute a process that requires confirmation from the user. It opens a
     * dialogue window that asks for confirmation in the shape of the {@code message} and executes the
     * {@code process} if the user hits 'OK'.
     *
     * @param message The message that indicates what to confirm.
     * @param process The process to execute on confirmation.
     */
    public static void confirmProcess(String message, Callable<String> process, Callable<ValidationDto> validationProcess) {
        // init proceed.
        proceed = false;

        // Build the icon.
        Button icon = new Button();
        icon.getStyleClass().addAll("ico-inform", "i-warn");

        // Build and show the stage.
        Stage confirmDialogue = buildStage(message, icon, EDialogueTypes.CONFIRMATION);
        confirmDialogue.showAndWait();

        // Execute the command on confirmation.
        if (proceed) {
            if (process != null) {
                process(process, null);
            } else {
                process(null, validationProcess);
            }
        }
    }

    /**
     * Shows all invalid documents in a popup window. The invalid documents are being taken
     * from a DTO.
     */
     void showInvalid(ValidationDto dto) {
        List<String> documents = dto.getInvalidDocuments();
        StringBuilder sb = new StringBuilder();
        documents.forEach(doc -> sb.append(pretty(doc)).append("\n"));
        showDocuments(sb.toString(), "Show all invalid documents of this collection.");
    }

    /**
     * This inner class is used to automatically toggle the veil on stage creation and closing.
     */
    private static class DialogueStage extends Stage {

        /**
         * Constructs a Dialogue stage and toggles the main view.
         */
        public DialogueStage() {
            super();
            toggleVeil();
        }

        @Override
        public void close() {
            toggleVeil();
            super.close();
        }

        /**
         * Toggles the veil on the main view.
         */
        private void toggleVeil() {
            MainController.getInstance().toggleVeil();
        }
    }
}
