package josch.presentation.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import josch.model.dto.ConnectionInfoDto;
import josch.model.dto.ExtractionDto;
import josch.model.dto.SettingsDto;
import josch.model.dto.ValidationDto;
import josch.model.enums.EContainmentTools;
import josch.model.enums.EDatabaseSystems;
import josch.model.enums.EExtractionTools;
import josch.services.factory.AbstractServiceFactory;
import josch.services.interfaces.AbstractComparisonService;
import josch.services.interfaces.IDatabaseService;
import josch.services.interfaces.IExtractionService;
import josch.services.interfaces.IValidationService;
import org.json.simple.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class provides a sample on how to use the API of the Josch application. By running the
 * experiment as specified in the Bachelor Thesis. In order to run this on your computer you need
 * to check the {@link ApiController#setup(SettingsDto, int)}. You can optionally alternate the
 * iterations and sample size at will by modifying {@link ApiController#main(String[])}.
 *
 * @author Kai Dauberschmidt
 */
public class ApiController {

  /** The global extraction service */
  static IExtractionService extractionService;

  /** The global validation service */
  static IValidationService validationService;

  /** The global database service */
  static IDatabaseService databaseService;

  /** IJS containment Service */
  static AbstractComparisonService ijsService;

  /** JSS containment Service */
  static AbstractComparisonService jssService;


  /** Runs the experiment, adjust at will. */
  public static void main(String[] args) {

    // Params that can be adjusted.
    int iterations = 100;
    int[] sampleSizes = {10000};

    for (int sampleSize : sampleSizes) {
//      run(iterations, sampleSize, EExtractionTools.JSI, false);
      run(iterations, sampleSize, EExtractionTools.HACK, false);
}
  }

  /**
   * Executes the experiment.
   *
   * @param iterations The amount of iterations.
   * @param sampleSize The sample size.
   * @param extraction The used extraction tool.
   * @param skip A boolean to indicate whether to skip containment comparison (required only once!)
   */
  private static void run(int iterations, int sampleSize, EExtractionTools extraction, boolean skip) {

    // Set up the tools.
    SettingsDto settings = new SettingsDto(EDatabaseSystems.MONGO, extraction, null);
    double totalDocs = setup(settings, sampleSize);

    // The time difference 'delta time' for each step, i.e. the duration.
    double dt;

    // The lists to store the iteration data, aligned together.
    List<Double> extractionTimes = new ArrayList<>();
    List<String> schemas = new ArrayList<>();

    List<Double> validationTimes = new ArrayList<>();
    List<Long> invalidDocuments = new ArrayList<>();

    List<Double> ijsTimes = new ArrayList<>();
    List<String> ijsResults = new ArrayList<>();

    List<Double> jssTimes = new ArrayList<>();
    List<String> jssResults = new ArrayList<>();

    // Strings used.
    String result, schema;

    // Loop through iterations.
    for (int i = 0; i < iterations; i++) {

      // Notify about progress.
      if (i % 10 == 0 && i > 0) {
        System.out.println("Iteration " + i + " of total " + iterations + ".");
      }

      // Extract the JSON Schema.
      System.out.println("extracting JSON Schema");
      StopWatch.start();
      schema = extractionService.getJsonSchema();
      dt = StopWatch.stop();
      schemas.add(schema);
      extractionTimes.add(dt);


      // Validate the JSON Schema.
      System.out.println("validating all documents");
      StopWatch.start();
      ValidationDto dto = validationService.validate(schema);
      dt = StopWatch.stop();
      validationTimes.add(dt);
      invalidDocuments.add(dto.getAmountInvalidDocuments());

      // Measure containment as reflexive containment check if not skipped.
      if (!skip) {
        // JSS
        System.out.println("check containment");
        StopWatch.start();
        result = jssService.contains(schema, schema);
        dt = StopWatch.stop();
        jssTimes.add(dt);
        jssResults.add(result);

        // IJS
        System.out.println("check containment ijs");
        StopWatch.start();
        result = ijsService.contains(schema, schema);
        dt = StopWatch.stop();
        ijsTimes.add(dt);
        ijsResults.add(result);
      }
    }

    // Write the result.
    String fileName = "F:/" + extraction.getName() + "_" + sampleSize + ".json";
    JSONObject conclusion = new JSONObject();
    

    // Nest validation.
    JSONObject schemaValidation = new JSONObject();
    JSONObject validationTime = getTimes(validationTimes);
    
    // Nest results. 
    JSONObject validationResults = new JSONObject(); 
    validationResults.put("min", Collections.min(invalidDocuments));
    validationResults.put("max", Collections.max(invalidDocuments));
    validationResults.put("avg", averageLong(invalidDocuments));
    
    // Add. 
    schemaValidation.put("time", validationTime);
    schemaValidation.put("results", validationResults); 
    conclusion.put("validation", schemaValidation);

    // Nest extraction times.
    JSONObject schemaExtraction = new JSONObject();
    JSONObject extractionTime = getTimes(extractionTimes);
    schemaExtraction.put("time", extractionTime);
    schemaExtraction.put("schemas", schemas);
    conclusion.put("extraction", schemaExtraction);

    // if existent nest containment.
    if (!skip) {
      // Nest IJS
      JSONObject ijs = getContainment(ijsTimes, ijsResults);

      // Nest JSS
      JSONObject jss = getContainment(jssTimes, jssResults);

      // Add.
      JSONObject containment = new JSONObject();
      containment.put("IJS", ijs);
      containment.put("JSS", jss);
      conclusion.put("containment", containment);
    }
    conclusion.put("tool", settings.getExtraction().getTool().getName());
    conclusion.put("iterations", iterations);
    conclusion.put("sampleSize", sampleSize);
    

    // Pretty things up.
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    String prettyJson = gson.toJson(JsonParser.parseString(conclusion.toJSONString()));

    // Write the JSON Object to file.
    System.out.println("writing file");
    try (FileWriter file = new FileWriter(fileName)) {
      file.write(prettyJson);
      file.flush();
    } catch (IOException e) {
      // Print the console so that the calculation is not lost entirely.
      System.out.println(conclusion.toJSONString());
    }
  }

  /** Wraps containment times and results in a JSON Object and returns it. */
  private static JSONObject getContainment(List<Double> times, List<String> results) {
    JSONObject containment = new JSONObject();
    containment.put("time", getTimes(times));
    containment.put("results", calculateContainmentResults(results));
    return containment;
  }

  /** Nests the minimum, maximum and average times in a JSON Object and returns it. */
  private static JSONObject getTimes(List<Double> times) {
    JSONObject time = new JSONObject();
    time.put("min", Collections.min(times));
    time.put("max", Collections.max(times));
    time.put("avg", average(times));
    return time;
  }


  /** calculates the statistics for containment results and returns them as JSON Object. */
  private static JSONObject calculateContainmentResults(List<String> results) {
    JSONObject jsonObject = new JSONObject();
    int eqv = 0;
    int neqv = 0;
    int sub = 0;
    int sup = 0;
    int err = 0;

    // Check the results and sort.
    for (String result : results) {
      switch (result) {
        case "equivalent" -> eqv++;
        case "incomparable" -> neqv++;
        case "subset" -> sub++;
        case "superset" -> sup++;
        default -> err++;
      }
    }

    jsonObject.put("equivalent", eqv);
    jsonObject.put("incomparable", neqv);
    jsonObject.put("subset", sub);
    jsonObject.put("superset", sup);
    jsonObject.put("errors", err);
    return jsonObject;
  }

  /** Returns the average of a list of doubles.  */
  private static double average(List<Double> doubleList) {
    double result = 0;
    for (double entry : doubleList) {
      result += entry;
    }
    return (result) / doubleList.size();
  }

  /** Returns the average of a list of longs. */
  private static double averageLong(List<Long> doubleList) {
    double result = 0;
    for (long entry : doubleList) {
      result += (double) entry;
    }
    return (result) / doubleList.size();
  }

  /** Set up the concrete settings for a given SettingsDto. */
  private static double setup(SettingsDto settings, int sampleSize) {

    // Split the Settings into two separate in order to get both containment services.
    SettingsDto jss = new SettingsDto(settings.getDbms(), settings.getExtraction().getTool(), EContainmentTools.JSS);
    SettingsDto ijs = new SettingsDto(settings.getDbms(), settings.getExtraction().getTool(), EContainmentTools.IJS_SUBSET);

    // Path to comparison tools.
    ijs.setIjsPath("F:\\Uni Passau\\Josch\\tools\\IsJsonSchemaSubset");
    jss.setJssPath("F:\\Uni Passau\\Josch\\tools\\JsonSubSchema");

    // Create connection to localhost db thesis.
    ConnectionInfoDto localhost = new ConnectionInfoDto("mongodb://localhost:27017/thesis");
    ijs.setConnectionInfo(localhost);
    jss.setConnectionInfo(localhost);

    // Get the collection.
    databaseService = AbstractServiceFactory.getDatabaseService(ijs);
    ijs.setCollection(databaseService.getCollection("Verein"));
    jss.setCollection(databaseService.getCollection("Verein"));

    // Set the extraction settings.
    ExtractionDto extractionParams = settings.getExtraction();
    extractionParams.setSize(sampleSize);
    extractionParams.setMethod("absolute");
    extractionParams.setTarget(EDatabaseSystems.MONGO);
    extractionParams.setDbInstance("localhost");
    extractionParams.setContainer("thesis");
    extractionParams.setEntity("Verein");
    ijs.setExtraction(extractionParams);
    jss.setExtraction(extractionParams);

    // Set the services accordingly. Note that only containment differs.
    extractionService = AbstractServiceFactory.getExtractionService(ijs);
    validationService = AbstractServiceFactory.getValidationService(ijs);
    ijsService = AbstractServiceFactory.getComparisonService(ijs);
    jssService = AbstractServiceFactory.getComparisonService(jss);

    return ijs.getCollection().getCount();
  }

  /** Inner class that measures delta time. */
  private static class StopWatch {

    /** The start time as a double. */
    private static double start;

    /**
     * Start the counter. Note that while the inner method returns a long it is internally casted
     * to a double. We do not need to cast when averaging if using double as start.
     */
    public static void start() {
      start = System.currentTimeMillis();
    }

    /** Returns the delta time between start and now. */
    public static double stop() {
      return System.currentTimeMillis() - start;
    }
  }
}
