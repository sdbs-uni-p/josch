package josch.persistency.implementation.mongodb;

import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

/**
 * This class implements a pseudo iterator over Documents represented as JSON Strings. It is being
 * used for sampling and validation. However it does not use an underlying Iterable over documents
 * but instead uses MongoDB's indexing in order to increase performance: This implementation is 21
 * times as fast the previous iterable version. <br>
 * It furthermore utilizes randomization of samples in order to increase the quality of result. If
 * you iterate over n documents of the same collection, i.e. not modified, the same n documents in
 * the same order will always be retrieved. However good sampling demands random n documents being
 * retrieved.
 *
 * @author Kai Dauberschmidt
 */
public class DocumentIterator implements Iterator<String> {

  /** The MongoDB collection to iterate over. */
  private final MongoCollection<Document> collection;

  /** A list that holds the document ids. */
  private final ArrayList<Document> LIST_IDS;

  /** The the cursor index within the shuffled list. */
  private int currentId;

  /**
   * Constructs an iterator to iterate over MongoDB documents as JSON strings.
   *
   * @param collection the MongoDB collection randomly get documents from.
   */
  public DocumentIterator(MongoCollection<Document> collection, boolean randomize) {
    this.collection = collection;

    // Add all _ids to the list. All MongoDB documents do have this and invoke an index on it.
    LIST_IDS = new ArrayList<>();
    for (Document document : collection.find().projection(new Document("_id", 1))) {
      LIST_IDS.add(document);
    }

    // Create a random permutation of the list.
    if (randomize) {
      shuffle();
    }

    // Initialize the list's cursor.
    currentId = 0;
  }

  /**
   * Shuffles the indices array using the Fisher Yates shuffle algorithm, i.e. generates a random
   * permutation of the list.
   */
  private void shuffle() {

    // Random object for shuffling at random.
    Random random = new Random();

    // Swap each element one by one starting at the last element.
    for (int i = LIST_IDS.size() - 1; i > 0; i--) {

      // Pick a random index j, 0 <= j <= i.
      int j = random.nextInt(i + 1);

      // Swap current element with element at the random index.
      Document temp = LIST_IDS.get(i);
      LIST_IDS.set(i, LIST_IDS.get(j));
      LIST_IDS.set(j, temp);
    }
  }

  /**
   * Checks whether a next document exists within the iterable, i.e. the cursor has not exceeded the
   * shuffle length, so the cursor is still a valid index.
   *
   * @return {@code true} if the iterable still holds a document, {@code false} else.
   */
  @Override
  public boolean hasNext() {
    return currentId < LIST_IDS.size() - 1;
  }

  /**
   * Iterates randomly over the documents and returns them as JSON Strings. Note that all all
   * documents have to be visited but they have to be visited randomly in order to create a reliable
   * sampling set. Two iterators that iterate over the same dataset will produce the same output
   * however (in a FIFO manner). That means newer documents might not be considered at all within
   * the sampling.
   *
   * @return The MongoDB document as a JSON String.
   */
  @Override
  public String next() {

//    // some output on progress on the console: Notify the user about each 1k documents sampled.
//    if (currentId % 1000 == 0 && currentId > 0) {
//      System.out.println("sampled documents: " + currentId);
//    }

    Document next = collection.find(LIST_IDS.get(currentId)).first();
    currentId++;

    // If there still exists a document return it as JSON.
    return (next != null) ? next.toJson() : null;
  }
}
