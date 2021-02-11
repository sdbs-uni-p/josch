package josch.services.comparison.jsonsubschema;

import josch.model.dto.SettingsDto;
import josch.model.enums.EContainmentRelations;
import josch.model.enums.EContainmentTools;
import josch.model.enums.EDatabaseSystems;
import josch.model.enums.EExtractionTools;
import josch.test.interfaces.AbstractBaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * This class is used to test the comparison service using the jss tool. It tests whether the script
 * is being called correctly and also whether the result of reflection matches. Note that the script
 * itself is being tested from within its module.
 *
 * @author Kai Dauberschmidt
 */
public class JssComparisonServiceTest extends AbstractBaseTest {

  /** The settings */
  private SettingsDto settings;

  /** setup the path to the system. Note this has to be adjusted. */
  @BeforeEach
  @Override
  public void setup() {
    settings = new SettingsDto(EDatabaseSystems.MONGO, EExtractionTools.HACK, EContainmentTools.IJS_SUBSET);
    settings.setIjsPath("F:/Uni Passau/josch/tools/IsJsonSchemaSubset");
    settings.setJssPath("F:/Uni Passau/josch/tools/JsonSubSchema");
  }

  /**
   * Checks for equivalence on reflexive Schema and correct call of script.
   */
  @Test
  public void contains_ReflexiveS1() {
    JssComparisonService service = new JssComparisonService(settings);
    String result = service.contains(JSON_SCHEMA, JSON_SCHEMA);

    Assertions.assertEquals(EContainmentRelations.EQV.toString(), result);
  }
}
