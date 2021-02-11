package josch.presentation.gui.model.utils;

/**
 * This enum decides the type of a dialogue window. The type of a dialogue defines the elements
 * present.
 *
 * @author Kai Dauberschmidt
 */
public enum EDialogueTypes {

    /** Control Flow dialogues that ask the user for confirmation to continue. */
    CONFIRMATION,

    /** Information dialogues that inform the user about results or errors. */
    INFORMATION,

    /** Process dialogues are used to show a progress indicator */
    PROCESS,

    VALIDATION
}
