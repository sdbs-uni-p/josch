package josch.services.extraction.jsi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.saasquatch.jsonschemainferrer.JsonSchemaInferrer;
import com.saasquatch.jsonschemainferrer.RequiredPolicies;
import com.saasquatch.jsonschemainferrer.SpecVersion;
import josch.model.dto.SettingsDto;
import josch.model.enums.EDatabaseSystems;
import josch.model.enums.ESystemConstants;
import josch.persistency.factory.AbstractClientFactory;
import josch.persistency.interfaces.IClient;
import josch.services.interfaces.IDatabaseService;
import josch.services.interfaces.IExtractionService;
import josch.services.persistency.DatabaseService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This {@code JsiExtractionService} class implements JSON Schema extraction using the JSON Schema
 * Inferrer (JSI). It is being recommended by the official JSON Schema Website <a
 * href="https://json-schema.org/implementations.html#from-data">json-schema.org</a>.
 *
 * @author Kai Dauberschmidt
 */
public class JsiExtractionService implements IExtractionService {

  /** The system's settings. */
  private final SettingsDto SETTINGS;

  /** Constructs a new ExtractionService with given settings */
  public JsiExtractionService(SettingsDto settings) {
    this.SETTINGS = settings;
  }

  /** {@inheritDoc} */
  @Override
  public String getJsonSchema() {
    ObjectMapper mapper = new ObjectMapper();
    IClient client = AbstractClientFactory.getClient(SETTINGS);

    // Get the amount of documents.
    String method = SETTINGS.getExtraction().getMethod();
    int size = SETTINGS.getExtraction().getSize();
    if (method.equals("relative")) {
      double percent = (double) size / 100;
      long amountDocuments = SETTINGS.getCollection().getCount();
      size = (int) (amountDocuments * percent);
    }

    // Create an Inferrer.
    JsonSchemaInferrer inferrer =
        JsonSchemaInferrer.newBuilder()
            .setSpecVersion(SpecVersion.DRAFT_04)
            .setRequiredPolicy(RequiredPolicies.nonNullCommonFields())
            .build();


    // Store the documents in a list of JsonNodes.
    List<JsonNode> samples = new ArrayList<>();
    Iterator<String> it = client.getDocumentIterator(SETTINGS.getCollection().getName(), true);
    String doc;
    JsonNode current;

    /* Tracks the memory because large collections might easily exceed the memory limits. */
    Runtime jvm = Runtime.getRuntime();
    long allocatedMemory;
    long freeMemory;
    boolean hasMemoryLeft;

    // The reserve half the maximum available memory of the JVM.
    long maxMemory = jvm.maxMemory();
    long reservedMemory = (long) (maxMemory * 0.5);
    System.out.println(
        "Reserving for processing: " + (reservedMemory / Math.pow(1024, 3)) + " GB");

    // Add the documents to the sample if they still fit into the memory.
    int i = 0;
    do {
      // Check the memory state.
      allocatedMemory = jvm.totalMemory() - jvm.freeMemory() + reservedMemory;
      freeMemory = maxMemory - allocatedMemory;
      hasMemoryLeft = freeMemory > 0 | jvm.totalMemory() + reservedMemory < maxMemory;

      // Add the documents to the sample list.
      try {
        doc = it.next();
          current = mapper.readTree(doc);
        samples.add(current);
        i++;
      } catch (JsonProcessingException e) {
        e.printStackTrace();
        return  ESystemConstants.ERROR.getValue() + e.getMessage();
      } catch (IllegalArgumentException e) {
        e.printStackTrace();
      }
    } while (it.hasNext() && i <= size && hasMemoryLeft);


    if (!hasMemoryLeft) {
      System.out.println("System reaching memory limits:");
      System.out.println("Total Memory (GB): " + (jvm.totalMemory() / Math.pow(1024,3)));
      System.out.println("Free Memory (MB): " + (jvm.freeMemory() / Math.pow(1024, 2)));
      System.out.println("JSI couldn't sample all documents because the memory has reached its " +
              "limits. it sampled " + i + " documents instead.");
    }

    // Process the list and return the result.
    try {
      JsonNode schema = inferrer.inferForSamples(samples);
      // MongoDB specifics: remove oid property.
      if (SETTINGS.getDbms().equals(EDatabaseSystems.MONGO)) {
        current = schema.get("properties");
        current = current.get("_id"); // All documents in mongo have this.
        if (current.isObject()) {
          ((ObjectNode) current).remove("properties");
          ((ObjectNode) current).remove("required");
        }
      }
      return schema.toString();
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
      return ESystemConstants.ERROR.getValue() + e.getMessage();
    }
  }

  @Override
  public String getValidator() {
    String schema = getJsonSchema();
    if (schema.contains(ESystemConstants.ERROR.getValue())) {
      return schema;
    }
    IDatabaseService service = new DatabaseService(SETTINGS);
    return service.generateValidator(schema);
  }
}
