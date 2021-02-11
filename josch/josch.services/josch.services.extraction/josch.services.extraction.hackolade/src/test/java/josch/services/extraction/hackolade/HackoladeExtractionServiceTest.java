package josch.services.extraction.hackolade;

import josch.model.dto.ExtractionDto;
import josch.model.enums.EDatabaseSystems;
import josch.model.enums.ESystemConstants;
import josch.test.MongoDB.MongoBaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * This class tests the public methods of the {@code HackoladeExtractionService} using the Hackolade tool.
 * This Service generates a JSON Schema and a Validator schema. However note that it cannot be
 * guaranteed that the extracted schemas stay the same, even if the collection does not change. The
 * actual schema is depending on the used version of Hackolade and how they manage to extract data.
 * <br>
 * The methods do however throw errors when something was not successful. So positive tests are
 * being implemented using {@code Assertions.assertDoesNotThrow()}.This is also implicit testing of
 * command execution.
 */
public class HackoladeExtractionServiceTest extends MongoBaseTest {

  /** Set up the database */
  @BeforeEach
  public void setupService() {
    setup();

    ExtractionDto extractionDto = settings.getExtraction();
    extractionDto.setDbInstance("localhost");
    extractionDto.setMethod("absolute");
    extractionDto.setSize(100);
    extractionDto.setContainer(DATABASE);
    extractionDto.setEntity(COLLECTION);
    settings.setExtraction(extractionDto);
  }

  /** Tests whether a json schema draft-04 gets retrieved using hackolade. */
  @Test
  public void getJsonSchema_validContainer() {
    HackoladeExtractionService service = new HackoladeExtractionService(settings);
    Assertions.assertDoesNotThrow(service::getJsonSchema);
  }

  /** Tests whether a MongoDB JSON schema gets retrieved using Hackolade. */
  @Test
  public void getValidator() {
    HackoladeExtractionService service = new HackoladeExtractionService(settings);
    Assertions.assertDoesNotThrow(service::getValidator);
  }

  /** Tests whether no schema gets retrieved when the input is flawed. */
  @Test
  public void getSchema_invalidInput() {
    ExtractionDto dto = settings.getExtraction();
    List<String> results = new ArrayList<>();

    // Flawed container.
    ExtractionDto flawed = dto;
    flawed.setEntity("Rainbows"); // Guaranteed to not exist.
    settings.setExtraction(flawed);
    HackoladeExtractionService service = new HackoladeExtractionService(settings);
    // Actual tests.
    results.add(service.getJsonSchema()); // Negative test flawed container JSON Schema.
    results.add(service.getValidator()); // Negative test flawed container validator.

    // illegal size.
    flawed = dto;
    flawed.setSize(1000000);
    settings.setExtraction(flawed);
    service = new HackoladeExtractionService(settings);
    // Actual tests.
    results.add(service.getJsonSchema()); // Negative test flawed container JSON Schema.
    results.add(service.getValidator()); // Negative test flawed container validator.

    // unknown connectName.
    flawed = dto;
    flawed.setTarget(EDatabaseSystems.getSystem("invalid"));
    settings.setExtraction(flawed);
    service = new HackoladeExtractionService(settings);

    // Actual tests.
    results.add(service.getJsonSchema()); // Negative test flawed container JSON Schema.
    results.add(service.getValidator()); // Negative test flawed container validator.

    for (String result : results) {
      Assertions.assertTrue(result.contains(ESystemConstants.ERROR.getValue()));
    }
  }
}
