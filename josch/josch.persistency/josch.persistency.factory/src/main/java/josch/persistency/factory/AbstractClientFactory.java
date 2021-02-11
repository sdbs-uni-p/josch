package josch.persistency.factory;

import josch.model.dto.SettingsDto;
import josch.persistency.implementation.mongodb.MongoDbClient;
import josch.persistency.interfaces.IClient;


/**
 * The {@code AbstractClientFactory} is a creational pattern to create concrete {@code IClient} objects
 * without exposing the creation logic. It is used to create clients, that hold the concrete
 * connection and interact with the underlying database.
 *
 * @author Kai Dauberschmidt
 * @see IClient
 * @see SettingsDto
 */
public abstract class AbstractClientFactory {

  /**
   * Gets an implementation of the {@code IClient} interface, depending on the specified {@code
   * dbSystem} in the constructor.
   *
   * @return the concrete client implementation
   */
  public static IClient getClient(SettingsDto settings) {

    // Switch case for extensibility.
    //noinspection SwitchStatementWithTooFewBranches
    return switch (settings.getDbms()) {
      case MONGO -> new MongoDbClient(settings.getConnectionInfo());
    };
  }


}
