package josch.test.interfaces;

import java.util.ArrayList;
import java.util.List;

/**
 * Josch is an application that is database-driven. Hence the tests require a certain state of
 * the database. This {@code AbstractBaseTest} class defines the methods that the implementations
 * should have in order to guarantee that the database is in the correct state for the tests
 * among the various modules Josch has. While it could be considered an interface for the various
 * NoSQL db implementations, it defines the basic collections, documents and so on in a single
 * place.
 *
 * TODO: ensure that the database in that combination is not already used elsewhere.
 *
 * @author Kai Dauberschmidt
 */
public abstract class AbstractBaseTest {

    /** The name of the database to test. */
    protected static final String DATABASE = "Josch";

    /** The name of the collection to test. */
    protected static final String COLLECTION = "students";

    /** The documents of the collection as a String list. */
    protected static List<String> documents;

    /** A valid JSON Schema draft-04 for the collection. */
    protected static final String JSON_SCHEMA = String.join("\n", new String[] {
            "{" +
                    "  \"$schema\": \"http://json-schema.org/draft-04/schema#\"," +
                    "  \"type\": \"object\"," +
                    "  \"title\": \"students\"," +
                    "  \"additionalProperties\": true," +
                    "  \"properties\": {" +
                    "    \"year\": { \"type\": \"number\" }," +
                    "    \"class\": { \"type\": [\"number\", \"string\"] }," +
                    "    \"language\": { \"type\": \"string\" }," +
                    "    \"name\": {" +
                    "      \"anyOf\": [" +
                    "        { \"type\": \"string\" }," +
                    "        {" +
                    "          \"type\": \"object\"," +
                    "          \"additionalProperties\": false," +
                    "          \"properties\": {" +
                    "            \"first\": { \"type\": \"string\" }," +
                    "            \"nick\": { \"type\": \"string\" }" +
                    "          }" +
                    "        }" +
                    "      ]" +
                    "    }" +
                    "  }," +
                    "  \"required\": [\"year\", \"class\" ]" +
                    "}"
    });


    /**
     * The 26 documents of the collection. These have the following distribution:
     * <ul>
     *     <li>class: 13 integers (50%) 13 strings (50%)</li>
     *     <li>language: 8 documents have this attribute (~30%) </li>
     *     <li>name: 4 (~ 15%) have a nested name with a first and a nickname given. </li>
     * </ul>
     *
     * Note that this is slightly longer longer than max line to keep it visually readable.
     * */
    protected void addDocuments() {
        documents = new ArrayList<>();
        documents.add("{ \"name\": {\"first\": \"Anton\", \"nick\": \"Toni\" }, \"year\": 2015, \"class\": 1 }");
        documents.add("{ \"name\": \"Ben\", \"year\": 2014, \"class\": 1 }");
        documents.add("{ \"name\": {\"first\": \"Caroline\", \"nick\": \"Caro\" }, \"year\": 2015, \"class\": 1 }");
        documents.add("{ \"name\": \"Doris\", \"year\": 2014, \"class\": 1 }");
        documents.add("{ \"name\": \"Emir\", \"year\": 2015, \"class\": 1, \"language\": \"Arabic\" }");
        documents.add("{ \"name\": {\"first\": \"Franziska\", \"nick\": \"Franzi\" }, \"year\": 2014, \"class\": 1 }");
        documents.add("{ \"name\": \"Gregor\", \"year\": 2015, \"class\": 1, \"language\": \"Russian\" }");
        documents.add("{ \"name\": \"Henry\", \"year\": 2014, \"class\": 1, \"language\": \"French\" }");
        documents.add("{ \"name\": \"Iris\", \"year\": 2015, \"class\": 1 }");
        documents.add("{ \"name\": \"Jonas\", \"year\": 2014, \"class\": 1 }");
        documents.add("{ \"name\": \"Kai\", \"year\": 2015, \"class\": 1 }");
        documents.add("{ \"name\": \"Lukas\", \"year\": 2014, \"class\": 1 },");
        documents.add("{ \"name\": {\"first\": \"Maximilian\", \"nick\": \"Max\" }, \"year\": 2015, \"class\": 1 }");
        documents.add("{ \"name\": \"Nadine\", \"year\": 2017, \"class\": \"Preschool\" }");
        documents.add("{ \"name\": \"Otto\", \"year\": 2016, \"class\": \"Preschool\" }");
        documents.add("{ \"name\": \"Paul\", \"year\": 2015, \"class\": \"Preschool\" }");
        documents.add("{ \"name\": \"Quentin\", \"year\": 2016, \"class\": \"Preschool\", \"language\": \"English\" }");
        documents.add("{ \"name\": \"Richard\", \"year\": 2017, \"class\": \"Preschool\", \"language\": \"English\" }");
        documents.add("{ \"name\": \"Sarah\", \"year\": 2016, \"class\": \"Preschool\" }");
        documents.add("{ \"name\": \"Theodor\", \"year\": 2015,\"class\": \"Preschool\" }");
        documents.add("{ \"name\": \"Ulrich\", \"year\": 2016, \"class\": \"Preschool\" }");
        documents.add("{ \"name\": \"Vera\", \"year\": 2017, \"class\": \"Preschool\" }");
        documents.add("{ \"name\": \"Wendy\", \"year\": 2016, \"class\": \"Preschool\", \"language\": \"English\" }");
        documents.add("{ \"name\": \"Xantippe\", \"year\": 2015, \"class\": \"Preschool\" }");
        documents.add("{ \"name\": \"Yvonne\", \"year\": 2016, \"class\": \"Preschool\", \"language\": \"Czech\" }");
        documents.add("{ \"name\": \"Zoe\", \"year\": 2017, \"class\": \"Preschool\", \"language\": \"French\" }");
    }

    /**
     * Sets up the database to the correct state, i.e. it ensures that the {@code database},
     * {@code collection} and {@code documents} exist within the database. It furthermore has
     * to ensure that only these {@code documents} exists within that {@code collection}.
     */
    public abstract void setup();
}
