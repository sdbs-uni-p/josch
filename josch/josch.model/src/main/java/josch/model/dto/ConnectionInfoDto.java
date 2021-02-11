package josch.model.dto;

/**
 * The {@code ConnectionInfoDto} class encapsulates the attributes from a connection. It is designed by
 * a <i>data transfer object (dto)</i> pattern to pass multiple data in a single object throughout
 * the architectural layers.
 *
 * @author Kai Dauberschmidt
 * @version 1.0
 */
public class ConnectionInfoDto extends AbstractDto {

  /** The host of the database server */
  private String host;

  /** The port of the database server */
  private String port;

  /** The user of the database server */
  private String user;

  /** The password of the database server */
  private String password;

  /** The actual database */
  private String database;

  /** The connectionString of the database server */
  private String url;

  /** The timeout in milliseconds (ms) */
  long timeout = 300;

  /**
   * Constructs a ConnectionInfoDto with a given url
   *
   * @param url The Connection information passed as an url.
   */
  public ConnectionInfoDto(String url) {
    this.url = url;
  }

  /** Constructs a ConnectionInfoDto and sets its params to empty strings */
  public ConnectionInfoDto() {
    this.host = "";
    this.url = "";
    this.port = "";
    this.user = "";
    this.password = "";
    this.database = "";
  }

  /** Constructs a ConnectionInfoDto with a host and port. */
  public ConnectionInfoDto(String host, String port) {
    this.host = host;
    this.port = port;
    this.url = "";
    this.user = "";
    this.password = "";
    this.database = "";
  }

  /**
   * Gets the {@code timeout}
   *
   * @return The value of {@code timeout}
   */
  public long getTimeout() {
    return timeout;
  }

  /**
   * Sets the {@code timeout}.
   *
   * @param timeout The concrete value of {@code timeout}.
   */
  public void setTimeout(long timeout) {
    if (timeout >= 0) {
      this.timeout = timeout;
    } else {
      throw new IllegalArgumentException("timeout must be >= 0");
    }
  }

  /**
   * Gets the {@code host}
   *
   * @return The value of {@code host}
   */
  public String getHost() {
    return host;
  }

  /**
   * Sets the {@code host}.
   *
   * @param host The concrete value of {@code host}.
   */
  public void setHost(String host) {
    this.host = host;
  }

  /**
   * Gets the {@code port}
   *
   * @return The value of {@code port}
   */
  public String getPort() {
    return port;
  }

  /**
   * Sets the {@code port}.
   *
   * @param port The concrete value of {@code port}.
   */
  public void setPort(String port) {
    this.port = port;
  }

  /**
   * Sets the {@code port} to the string port used in the connectionString.
   *
   * @param port The concrete value of {@code port}.
   */
  public void setPort(int port) {
    this.port = String.valueOf(port);
  }

  /**
   * Gets the {@code user}
   *
   * @return The value of {@code user}
   */
  public String getUser() {
    return user;
  }

  /**
   * Sets the {@code user}.
   *
   * @param user The concrete value of {@code user}.
   */
  public void setUser(String user) {
    this.user = user;
  }

  /**
   * Gets the {@code password}
   *
   * @return The value of {@code password}
   */
  public String getPassword() {
    return password;
  }

  /**
   * Sets the {@code password}.
   *
   * @param password The concrete value of {@code password}.
   */
  public void setPassword(String password) {
    this.password = password;
  }

  /**
   * Gets the {@code database}
   *
   * @return The value of {@code database}
   */
  public String getDatabase() {
    return database;
  }

  /**
   * Sets the {@code database}.
   *
   * @param database The concrete value of {@code database}.
   */
  public void setDatabase(String database) {
    if (url.isEmpty()) {
      this.database = database;

      // update the url
    } else {
      String options = "";

      // extract options: url = url + options
      if (url.contains("?")) {
        int index = url.indexOf('?');
        options += url.substring(index); // "?...(&...)"
        url = url.substring(0, index); // "p://host:port(/)"
      }

      int sIndex = url.lastIndexOf('/');
      int rIndex = sIndex - 1;

      if (sIndex == url.length() - 1) { // case 1: slash at the end: p://host:port/
        url += database;
      } else if (url.charAt(sIndex) == url.charAt(rIndex)) { // case 2: protocol slash p://host:port
        url += "/" + database;
      } else { // case 3: existing db slash: p://host:port/db
        url = url.substring(0, sIndex+1); // trim to inclusive /
        url += database;
      }

      url += options;
    }
  }

  /**
   * Gets the {@code url}
   *
   * @return The value of {@code url}
   */
  public String getUrl() {
    return url;
  }

  /**
   * Sets the {@code url}.
   *
   * @param url The concrete value of {@code url}.
   */
  public void setUrl(String url) {
    this.url = url;
  }
}
