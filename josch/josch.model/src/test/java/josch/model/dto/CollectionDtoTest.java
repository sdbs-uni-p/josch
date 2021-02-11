package josch.model.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * This class is used for testing the validationOptions as the other attributes are simple basic
 * datatype with getters and setters.
 *
 * @author Kai Dauberschmidt
 */
public class CollectionDtoTest {

  /** Checks whether the flow of setValidationAction -> getValidationAction works correctly. */
  @Test
  public void setAndGet_validationAction() {
    // The value to set and get.
    String action = "warn";

    // Set and Get.
    CollectionDto collectionDto = new CollectionDto();
    collectionDto.setValidationAction(action);
    String result = collectionDto.getValidationAction();

    Assertions.assertNotNull(result);
    Assertions.assertEquals(action, result);
  }

  /** Checks whether the flow of getValidationAction -> setValidationAction works correctly. */
  @Test
  public void getAndSet_validationAction() {
    // The value to set and get.
    String action = "warn";

    // Set and Get.
    CollectionDto collectionDto = new CollectionDto();
    String result = collectionDto.getValidationAction();
    collectionDto.setValidationAction(action);

    Assertions.assertNull(result);
  }

  /** Checks whether the flow of setValidationLevel -> getValidationLevel works correctly. */
  @Test
  public void setAndGet_validationLevel() {
    // The value to set and get.
    String level = "level";

    // Set and Get.
    CollectionDto collectionDto = new CollectionDto();
    collectionDto.setValidationLevel(level);
    String result = collectionDto.getValidationLevel();

    Assertions.assertNotNull(result);
    Assertions.assertEquals(level, result);
  }

  /** Checks whether the flow of getValidationLevel -> setValidationLevel works correctly. */
  @Test
  public void getAndSet_validationLevel() {
    // The value to set and get.
    String level = "level";

    // Set and Get.
    CollectionDto collectionDto = new CollectionDto();
    String result = collectionDto.getValidationLevel();
    collectionDto.setValidationLevel(level);

    Assertions.assertNull(result);
  }

  /** Checks whether the flow of setValidator -> getValidator works correctly. */
  @Test
  public void setAndGet_validator() {
    // The value to set and get.
    String validator = "validator";

    // Set and Get.
    CollectionDto collectionDto = new CollectionDto();
    collectionDto.setValidator(validator);
    String result = collectionDto.getValidator();

    Assertions.assertNotNull(result);
    Assertions.assertEquals(validator, result);
  }

  /** Checks whether the flow of getValidator -> setValidator works correctly. */
  @Test
  public void getAndSet_validator() {
    // The value to set and get.
    String validator = "validator";

    // Set and Get.
    CollectionDto collectionDto = new CollectionDto();
    String result = collectionDto.getValidator();
    collectionDto.setValidator(validator);

    Assertions.assertNull(result);
  }
}
