package josch.model.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * This {@code ValidationDto} encapsulates the validation variables. It holds the information about
 * the amount of invalid documents as well as a list of invalid documents. Note that while
 * the amount could be fetched by the {@link List#size()}, the list could exceed the memory limit
 * and thus these are separated.
 *
 * @author Kai Dauberschmidt
 */
public class ValidationDto {

    /** The amount of invalid Documents */
    private long amountInvalidDocuments;

    /** A list of invalid documents */
    private final List<String> INVALID_DOCUMENTS;

    /** The notification message for validation */
    String notification;

    /** Constructs the dto with a new list */
    public ValidationDto() {
        this.INVALID_DOCUMENTS = new ArrayList<>();
        this.amountInvalidDocuments = 0;
    }

    /**
     * Gets the {@code amountInvalidDocuments}
     *
     * @return The value of {@code amountInvalidDocuments}
     */
    public long getAmountInvalidDocuments() {
        return amountInvalidDocuments;
    }

    /**
     * Sets the {@code amountInvalidDocuments}.
     *
     * @param amountInvalidDocuments The concrete value of {@code amountInvalidDocuments}.
     */
    public void setAmountInvalidDocuments(long amountInvalidDocuments) {
        this.amountInvalidDocuments = amountInvalidDocuments;
    }

    /**
     * Gets the {@code notification}
     *
     * @return The value of {@code notification}
     */
    public String getNotification() {
        return notification;
    }

    /**
     * Sets the {@code notification}.
     *
     * @param notification The concrete value of {@code notification}.
     */
    public void setNotification(String notification) {
        this.notification = notification;
    }

    /**
     * Adds a document to the invalid documents list if there is enough memory available.
     *
     * @param doc The document to add.
     */
    public void addInvalid(String doc) {

        /* Memory related settings. */
        Runtime jvm = Runtime.getRuntime();

        // Maximum available memory to the jvm.
        long maxMemory = jvm.maxMemory();

        // Reserve 10%
        long reservedMemory = (long) (maxMemory * 0.1);

        // The amount of memory that is being considered allocated.
        long allocatedMemory = jvm.totalMemory() - jvm.freeMemory() + reservedMemory;

        // The amount of memory that is being considered free.
        long freeMemory = maxMemory - allocatedMemory;

        // a boolean to determine whether memory is still left.
        boolean hasMemoryLeft = freeMemory > 0 | jvm.totalMemory() + reservedMemory < maxMemory;

        /* actual method: If there is still free memory add, else notify the user via console. */
        if (hasMemoryLeft) {
            INVALID_DOCUMENTS.add(doc);
        } else {
            System.out.println("Error! Running out of memory. Not adding invalid document.");
        }
    }

    /** Gets the invalid documents list */
    public List<String> getInvalidDocuments() {
        return INVALID_DOCUMENTS;
    }
}
