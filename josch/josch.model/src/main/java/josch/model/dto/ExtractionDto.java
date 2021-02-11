package josch.model.dto;

import josch.model.enums.EDatabaseSystems;
import josch.model.enums.EExtractionTools;

/**
 * This dto is used for extraction. An extraction dto must contain all the attributes that are
 * required for extraction. That is the used <em>tool</em>, <em>sampling size</em>, <em>sampling
 * method</em>, <em>instance</em>, <em>container</em> and <em>entity</em>. Container describes the
 * construct that contains the set of documents, i.e. the database and the set of documents is being
 * called entity, i.e. the collection of json documents.
 *
 * @author Kai Dauberschmidt
 */
public class ExtractionDto extends AbstractDto {

  /** The name of the used tool */
  EExtractionTools tool;
  
  /** the target dbms */
  EDatabaseSystems target;

  /** the sampling size */
  int size;

  /** the sampling method */
  String method;

  /** the name of the db instance */
  String dbInstance;

  /** The name of the container of documents, e.g. database for MongoDB. */
  String container;

  /** The name of the document entity. */
  String entity;

  /** Constructs an ExtractionDto with a given tool name. */
  public ExtractionDto(EExtractionTools tool) {
    this.tool = tool;
  }

  /** Constructs an empty ExtractionDto. */
  public ExtractionDto() {
  }

  /**
   * Gets the {@code tool}
   *
   * @return The value of {@code tool}
   */
  public EExtractionTools getTool() {
    return tool;
  }


  /**
   * Sets the {@code tool}.
   *
   * @param tool The concrete value of {@code tool}.
   */
  public void setTool(EExtractionTools tool) {
    this.tool = tool;
  }

  /**
   * Gets the {@code target}
   *
   * @return The value of {@code target}
   */
  public EDatabaseSystems getTarget() {
    return target;
  }

  /**
   * Sets the {@code target}.
   *
   * @param target The concrete value of {@code target}.
   */
  public void setTarget(EDatabaseSystems target) {
    this.target = target;
  }

  /**
   * Gets the {@code size}
   *
   * @return The value of {@code size}
   */
  public int getSize() {
    return size;
  }

  /**
   * Sets the sampling {@code size}.
   *
   * @param size The concrete sampling {@code size}.
   */
  public void setSize(int size) {
    this.size = size;
  }

  /**
   * Gets the sampling {@code method}.
   *
   * @return The value of {@code method}.
   */
  public String getMethod() {
    return method;
  }

  /**
   * Sets the sampling {@code method}.
   *
   * @param method The concrete sampling {@code method}.
   */
  public void setMethod(String method) {
    this.method = method;
  }

  /**
   * Gets the {@code dbInstance}
   *
   * @return The value of {@code dbInstance}
   */
  public String getDbInstance() {
    return dbInstance;
  }

  /**
   * Sets the {@code dbInstance}.
   *
   * @param dbInstance The concrete name of {@code dbInstance}.
   */
  public void setDbInstance(String dbInstance) {
    this.dbInstance = dbInstance;
  }

  /**
   * Gets the {@code container}.
   *
   * @return The value of {@code container}
   */
  public String getContainer() {
    return container;
  }

  /**
   * Sets the {@code container}.
   *
   * @param container The concrete name of {@code container}.
   */
  public void setContainer(String container) {
    this.container = container;
  }

  /**
   * Gets the {@code entity}.
   *
   * @return The value of {@code entity}
   */
  public String getEntity() {
    return entity;
  }

  /**
   * Sets the {@code entity}.
   *
   * @param entity The concrete name of {@code entity}.
   */
  public void setEntity(String entity) {
    this.entity = entity;
  }
}
