package josch.persistency.factory;

import josch.model.dto.ConnectionInfoDto;
import josch.model.dto.SettingsDto;
import josch.model.enums.EContainmentTools;
import josch.model.enums.EDatabaseSystems;
import josch.model.enums.EExtractionTools;
import josch.persistency.implementation.mongodb.MongoDbClient;
import josch.persistency.interfaces.IClient;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * This class tests the functionality of the AbstractClientFactory. The AbstractClientFactory returns a concrete
 * MongoDbClient object depending on the settings input.
 *
 * @author Kai Dauberschmidt
 */
public class AbstractClientFactoryTest {

  /** The settings as a dto. */
  private SettingsDto settings;

  /** Helper method to setup dynamic factories */
  @SuppressWarnings("unused")
  private void setup(String system, String url) {
    settings = new SettingsDto(EDatabaseSystems.MONGO, EExtractionTools.HACK, EContainmentTools.JSS);
    settings.setConnectionInfo(new ConnectionInfoDto(url));
  }

  /** Tests whether a MongoDB MongoDbClient implementation is returned on MongoDB dbSystem input. */
  @Test
  public void getClient_MongoDB() {
    setup("mongodb", "mongodb://localhost:27017/");

    IClient client = AbstractClientFactory.getClient(settings);
    assertTrue(client instanceof MongoDbClient); // instance of .../MongoDB.MongoDbClient
    client.close();
  }

  /**
   * Tests whether an exception is thrown when MongoDbClient implementation is demanded that hasn't been
   * implemented.
   */
  @Test
  public void getClient_PostgreSQL() {
  setup("postgresql", "postgresql://localhost");
    assertThrows(
        IllegalArgumentException.class, () -> AbstractClientFactory.getClient(settings));
  }
}
