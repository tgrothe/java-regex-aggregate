import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
  /**
   * Main method to aggregate and count the occurrences of a regex pattern in a text file.
   *
   * @param args input text file, aggregate regex, and optional ignore regex.
   * @throws Exception if the input file is not found or cannot be read.
   */
  public static void main(String[] args) throws Exception {
    if (args.length < 2) {
      System.out.println(
          "Usage: java -jar <jar file> <input text file> <aggregate regex> (<ignore regex>)");
      System.out.println(
          "Example: java -jar <jar file> \"input.txt\" \".*? (\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}) .*\"");
      System.out.println(" to aggregate IP addresses from the input file.");
      throw new IllegalArgumentException("Invalid number of arguments");
    }
    Pattern aggregatePattern = Pattern.compile(args[1]);
    Pattern ignorePattern = args.length > 2 ? Pattern.compile(args[2]) : Pattern.compile("(?!x)x");

    LinkedHashMap<String, Map.Entry<ArrayList<String>, Integer>> map = new LinkedHashMap<>();
    try (BufferedReader reader =
        new BufferedReader(new FileReader(args[0], Charset.defaultCharset()))) {
      String line;
      while ((line = reader.readLine()) != null) {
        Matcher aggregateMatcher = aggregatePattern.matcher(line);
        if (aggregateMatcher.find() && !ignorePattern.matcher(line).find()) {
          String key = aggregateMatcher.group(1);
          map.computeIfAbsent(key, k -> new AbstractMap.SimpleEntry<>(new ArrayList<>(), 0));
          Map.Entry<ArrayList<String>, Integer> entry = map.get(key);
          entry.getKey().add(line);
          entry.setValue(entry.getValue() + 1);
        }
      }
    }

    ArrayList<Map.Entry<String, Map.Entry<ArrayList<String>, Integer>>> list =
        new ArrayList<>(map.entrySet());
    // Sort by count in descending order, if counts are equal, sort by input order (earlier first).
    list.sort((o1, o2) -> o2.getValue().getValue().compareTo(o1.getValue().getValue()));
    for (Map.Entry<String, Map.Entry<ArrayList<String>, Integer>> entry : list) {
      System.out.printf(
          "%04d - %s - %s%n",
          entry.getValue().getValue(), entry.getKey(), entry.getValue().getKey().get(0));
    }
  }
}
