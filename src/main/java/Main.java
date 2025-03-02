import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
  public static void main(String[] args) throws Exception {
    for (String arg : args) {
      System.out.println(arg);
    }
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

    LinkedHashMap<String, Map.Entry<Integer, ArrayList<String>>> map = new LinkedHashMap<>();
    try (BufferedReader reader =
        new BufferedReader(new FileReader(args[0], Charset.defaultCharset()))) {
      String line;
      while ((line = reader.readLine()) != null) {
        if (line.matches(args[1]) && !ignorePattern.matcher(line).find()) {
          Matcher matcher = aggregatePattern.matcher(line);
          if (matcher.find()) {
            String key = matcher.group(1);
            ArrayList<String> list;
            Map.Entry<Integer, ArrayList<String>> entry;
            if (map.containsKey(key)) {
              list = map.get(key).getValue();
              entry = new AbstractMap.SimpleImmutableEntry<>(map.get(key).getKey() + 1, list);
            } else {
              list = new ArrayList<>();
              entry = new AbstractMap.SimpleImmutableEntry<>(1, list);
            }
            list.add(line);
            map.put(key, entry);
          } else {
            throw new IllegalStateException();
          }
        }
      }
    }

    ArrayList<Map.Entry<String, Map.Entry<Integer, ArrayList<String>>>> list =
        new ArrayList<>(map.entrySet());
    // Sort by count in descending order, if counts are equal, sort by input order (earlier first).
    list.sort((o1, o2) -> o2.getValue().getKey().compareTo(o1.getValue().getKey()));
    for (Map.Entry<String, Map.Entry<Integer, ArrayList<String>>> entry : list) {
      System.out.printf(
          "%04d - %s - %s%n",
          entry.getValue().getKey(), entry.getKey(), entry.getValue().getValue().get(0));
    }
  }
}
