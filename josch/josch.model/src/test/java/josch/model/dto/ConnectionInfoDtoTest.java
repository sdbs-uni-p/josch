package josch.model.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/** This tests special setter and getter methods */
public class ConnectionInfoDtoTest {

  /** Some url cases */
  final String[] URL_CASES = {
    "mongodb://localhost:27017/", "mongodb://localhost:27017", "mongodb://localhost:27017/database"
  };

  /** The dto for the tests (aside from setDatabase) */
  final ConnectionInfoDto DTO = new ConnectionInfoDto(URL_CASES[0]);

  /** Calls the setDatabaseTest on the three possible cases */
  @Test
  public void setDatabase_cases() {
    for (String url : URL_CASES) {
      setDatabaseTest(url);
    }
  }

  /** Tests whether an exception is thrown on illegal timeout times */
  @Test
  public void setTimeout_invalid() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> DTO.setTimeout(-1));
  }

  /** Tests for valid timeout setting */
  @Test
  public void setTimeout_valid() {
    long timeout = 4500;
    DTO.setTimeout(timeout);
    Assertions.assertEquals(timeout, DTO.getTimeout());
  }

  /** Tests if an int port gets converted to str. */
  @Test
  public void setPort_convert() {
    int port = 23;
    DTO.setPort(port);
    Assertions.assertEquals(String.valueOf(port), DTO.getPort());
  }

  /** Tests whether the new db is saved within the url */
  private void setDatabaseTest(String url) {
    // create the dto from the url
    ConnectionInfoDto dto = new ConnectionInfoDto(url);

    // set the database
    dto.setDatabase("testDb");

    // build the expected result:
    String expected = "mongodb://localhost:27017/" + "testDb";

    // Check whether the url is saved within the url
    Assertions.assertEquals(expected, dto.getUrl());
  }
}
