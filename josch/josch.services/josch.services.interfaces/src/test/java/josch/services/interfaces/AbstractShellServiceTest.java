package josch.services.interfaces;

import josch.model.enums.ESystemConstants;
import josch.test.interfaces.AbstractBaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This test class is used to test the non-execution methods of the {@code AbstractShellService}.
 * The execution is being tested within the implementations of this class. These are positive tests
 * only because negative tests would kind of mirror the positive tests with
 * {@code Assertions.assertThrows()}. These methods do have an ordering, i.e. a control flow that
 * mimics the actual control flow within the application. If the ordering is not being held onto
 * the tests will fail because e.g. you cannot delete a file that is not present.
 *
 * @author Kai Dauberschmidt
 */
public class AbstractShellServiceTest extends AbstractBaseTest {

    /** Sets up the test */
    @Override
    @SuppressWarnings("unused")
    public void setup() {
        addDocuments();
    }

    /** The path to the josch directory within the home. */
    private final String PATH = ESystemConstants.PATH.getValue();

    /** The path to the schema file. */
    private final Path SCHEMA_PATH = Paths.get(PATH).resolve("test").resolve("schema.json");

    /** Tests whether the creation of a test directory does not fail. */
    @Test
    @Order(1)
    public void createDirectories_test() {
        Assertions.assertDoesNotThrow(() -> AbstractShellService.createDirectories(SCHEMA_PATH, false));
    }

    /** Tests whether the JSON_SCHEMA can be stored to a json file. */
    @Test
    @Order(2)
    public void storeFile_JSON_SCHEMA() {
        String expected = ESystemConstants.SUCCESS.getValue();
        String actual = AbstractShellService.storeFile(JSON_SCHEMA, SCHEMA_PATH);
        Assertions.assertEquals(expected, actual);
    }

    /** Tests whether the stored JSON_SCHEMA can be loaded from the file. */
    @Test
    @Order(3)
    public void loadJsonString_JSON_SCHEMA() {
        String schema = AbstractShellService.loadJsonString(SCHEMA_PATH);
        Assertions.assertEquals(JSON_SCHEMA.replace(" ", ""), schema);
    }

    /** Tests the deletion of the JSON_SCHEMA file. */
    @Test
    @Order(4)
    public void delete_JSON_SCHEMA() {
        Assertions.assertDoesNotThrow(() -> AbstractShellService.delete(SCHEMA_PATH));
    }
}
