package josch.services.factory;

import josch.services.comparison.isjsonsubset.IjsComparisonService;
import josch.services.comparison.jsonsubschema.JssComparisonService;
import josch.services.extraction.hackolade.HackoladeExtractionService;
import josch.services.interfaces.AbstractComparisonService;
import josch.services.interfaces.IDatabaseService;
import josch.services.interfaces.IExtractionService;
import josch.services.interfaces.IValidationService;
import josch.services.persistency.DatabaseService;
import josch.services.validation.ValidationService;
import josch.test.MongoDB.MongoBaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * This Test class serves the purpose of testing the AbstractServiceFactory methods. While the factories'
 * method is called {@code getService()} the tests follow this naming convention: <br>
 * getService_name() <br>
 * where name is the name passed to the getService(name). This is done due to the fact that the
 * AbstractServiceFactory has synonym recognition. <br>
 *
 * @author Kai Dauberschmidt
 */
public class AbstractServiceFactoryTest extends MongoBaseTest {

  /**
   * Sets up the settings with a mongodb system, the Hackolade extraction and hackolade comparison
   * tool. The connection shall be to the localhost. Sets up the the AbstractServiceFactory as well.
   */
  @BeforeEach
  public void setupTest() {
    setup();
  }

  /** Checks the correct instance for database services */
  @Test
  public void getDatabaseService() {
    IDatabaseService service = AbstractServiceFactory.getDatabaseService(settings);
    Assertions.assertTrue(service instanceof DatabaseService); // Check the instance
  }

  /** Checks the correct instance for extraction services. */
  @Test
  public void getExtractionService() {
    IExtractionService service = AbstractServiceFactory.getExtractionService(settings);
    Assertions.assertTrue(service instanceof HackoladeExtractionService); // Check the instance
  }

  /** Checks the correct instance for comparison services */
  @Test
  public void getComparisonService() {
    AbstractComparisonService service = AbstractServiceFactory.getComparisonService(settings);
    Assertions.assertTrue(service instanceof JssComparisonService
            || service instanceof IjsComparisonService); // Check the instance.
  }

  /** Checks the correct instance for validation services */
  @Test
  public void getValidationService() {
    IValidationService service = AbstractServiceFactory.getValidationService(settings);
   Assertions.assertTrue(service instanceof ValidationService); // Check instance.
  }
}
