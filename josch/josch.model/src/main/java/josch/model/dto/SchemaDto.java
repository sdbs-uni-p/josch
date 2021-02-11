package josch.model.dto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * This {@code SchemaDto} encapsulates the attributes of a schema. A schema has a {@code
 * collection}, a {@code json} String representation and possibly a date.
 *
 * @author Kai Dauberschmidt
 */
public class SchemaDto extends AbstractDto {

  /**
   * The collection of the schema.
   */
  private String collection;

  /**
   * The JSON String representation.
   */
  private String json;

  /**
   * The note of the schema.
   */
  private String note;

  /**
   * Constructs a schema DTO with a given collection, JSON representation and date.
   */
  public SchemaDto(String collection, String json, LocalDate date, String note) {
    this.collection = collection;
    this.json = json;
    this.date = date;
    this.note = note;
  }

  /**
   * Gets the {@code note}
   *
   * @return The value of {@code note}
   */
  public String getNote() {
    return note;
  }

  /**
   * The optional date of the Schema.
   */
  private LocalDate date;

  /**
   * Constructs a schema DTO with a given collection and JSON representation.
   */
  public SchemaDto(String collection, String json) {
    this.collection = collection;
    this.json = json;
  }

  /**
   * Sets the {@code note}.
   *
   * @param note The concrete value of {@code note}.
   */
  public void setNote(String note) {
    this.note = note;
  }

  /**
   * Constructs an empty schema DTO.
   */
  public SchemaDto() {
  }

  /**
   * Gets the {@code collection} of the schema.
   *
   * @return The name of the {@code collection}.
   */
  public String getCollection() {
    return collection;
  }

  /**
   * Sets the schema's {@code collection}.
   *
   * @param collection The name of the schema's {@code collection}.
   */
  public void setCollection(String collection) {
    this.collection = collection;
  }

  /**
   * Gets the {@code json} string representation of the schema.
   *
   * @return The {@code json} string representation of the schema.
   */
  public String getJson() {
    return json;
  }

  /**
   * Sets the {@code json} string representation of the schema.
   *
   * @param json The {@code json} string representation of the schema. .
   */
  public void setJson(String json) {
    this.json = json;
  }

  /**
   * Gets the optional storage {@code date}.
   *
   * @return The optional storage date {@code date}.
   */
  public LocalDate getDate() {
    return date;
  }

  /**
   * Sets the storage {@code date}.
   *
   * @param date The concrete {@code date}.
   */
  public void setDate(LocalDate date) {
    this.date = date;
  }

  /**
   * Returns a string representation of the schema. The schema is represented by the collection and
   * the date, i.e. <em>Collection (Date)</em>.
   */
  @Override
  public String toString() {
    return date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + " - " + note;
  }
}
