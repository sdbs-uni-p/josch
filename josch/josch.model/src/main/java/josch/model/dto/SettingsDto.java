package josch.model.dto;

import josch.model.enums.EContainmentTools;
import josch.model.enums.EDatabaseSystems;
import josch.model.enums.EExtractionTools;

/**
 * The {@code SettingsDto} is a <em>data transfer object (DTO)</em> that encapsulates the
 * application's settings in order to pass them through the application's architectural layers.
 *
 * @author Kai Dauberschmidt
 */
public class SettingsDto extends AbstractDto {

  /** The database management system's name, e.g. MongoDB */
  private EDatabaseSystems dbms;

  /** The extraction tool to use. */
  private ExtractionDto extraction;

  /** The comparison tool to use. */
  private EContainmentTools comparison;
  
  /** The path to the IJS comparison tool. */
  private String ijsPath;
  
  /** The path to the JSS comparison tool. */
  private String jssPath; 

  /** The wrapped connection information */
  private ConnectionInfoDto connectionInfo;

  /** The wrapped collection information */
  private CollectionDto collection;

  /**
   * Constructs a SettingsDto object with its three attributes.
   *
   * @param system The data source system's name.
   * @param extraction The extraction tool.
   * @param comparison The comparison tool
   */
  public SettingsDto(EDatabaseSystems system, EExtractionTools extraction, EContainmentTools comparison) {
    this.dbms = system;
    this.extraction = new ExtractionDto(extraction);
    this.extraction.setTarget(system);
    this.comparison = comparison;
  }

  /** Constructs an empty SettingsDto object. */
  public SettingsDto() {}

  /**
   * Gets the database system's name
   *
   * @return The value of database system's name
   */
  public EDatabaseSystems getDbms() {
    return dbms;
  }

  /**
   * Sets the database system's name.
   *
   * @param dbms The concrete database system's name.
   */
  public void setDbms(EDatabaseSystems dbms) {
    this.dbms = dbms;
  }

  /**
   * Gets the {@code extraction} parameters encapsulated in a dto.
   *
   * @return The encapsulated values for {@code extraction}
   */
  public ExtractionDto getExtraction() {
    return extraction;
  }

  /**
   * Sets the {@code extraction} parameters.
   *
   * @param extraction The concrete encapsulated values for {@code extraction}.
   */
  public void setExtraction(ExtractionDto extraction) {
    this.extraction = extraction;
  }

  /**
   * Gets the {@code comparison}
   *
   * @return The value of {@code comparison}
   */
  public EContainmentTools getComparison() {
    return comparison;
  }

  /**
   * Sets the {@code comparison}.
   *
   * @param comparison The concrete value of {@code comparison}.
   */
  public void setComparison(EContainmentTools comparison) {
    this.comparison = comparison;
  }

  /**
   * Gets the {@code ijsPath} as a String. 
   *
   * @return The value of {@code ijsPath} as a String. 
   */
  public String getIjsPath() {
    return ijsPath;
  }

  /**
   * Sets the {@code ijsPath} as a String. .
   *
   * @param ijsPath The concrete value of {@code ijsPath} as a String. .
   */
  public void setIjsPath(String ijsPath) {
    this.ijsPath = ijsPath;
  }

  /**
   * Gets the {@code jssPath} as a String. 
   *
   * @return The value of {@code jssPath} as a String. 
   */
  public String getJssPath() {
    return jssPath;
  }

  /**
   * Sets the {@code jssPath} as a String. .
   *
   * @param jssPath The concrete value of {@code jssPath} as a String. .
   */
  public void setJssPath(String jssPath) {
    this.jssPath = jssPath;
  }
  
  /**
   * Gets the {@code connectionInfo}
   *
   * @return The value of {@code connectionInfo}
   */
  public ConnectionInfoDto getConnectionInfo() {
    return connectionInfo;
  }

  /**
   * Sets the {@code connectionInfo}.
   *
   * @param connectionInfo The concrete value of {@code connectionInfo}.
   */
  public void setConnectionInfo(ConnectionInfoDto connectionInfo) {
    this.connectionInfo = connectionInfo;
  }

  /**
   * Gets the {@code collection}
   *
   * @return The value of {@code collection}
   */
  public CollectionDto getCollection() {
    return collection;
  }

  /**
   * Sets the {@code collection}.
   *
   * @param collection The concrete value of {@code collection}.
   */
  public void setCollection(CollectionDto collection) {
    this.collection = collection;
  }
}
