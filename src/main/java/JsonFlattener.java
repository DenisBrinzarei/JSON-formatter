import com.google.gson.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

/**
 * A class that takes a JSON object as input and outputs a flattened version of the JSON object,
 * with keys as the path to every terminal value in the JSON structure.
 * Assuming input has no Json array elements, and input Json is valid.
 *
 * @author  Denis.Brinzarei
 * @version 1.0
 * @since   2021-01-10
 */
public class JsonFlattener {
    // Global Gson object to serialize/deserialize raw Json.
    private static Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static String originalJson = "{\n" +
            "    \"a\": 1,\n" +
            "    \"b\": true,\n" +
            "    \"c\": {\n" +
            "        \"d\": 3,\n" +
            "        \"e\": \"test\"\n" +
            "    }\n" +
            "}";

    private static String flatJson = "{\n" +
            "  \"a\": 1,\n" +
            "  \"b\": true,\n" +
            "  \"c.d\": 3,\n" +
            "  \"c.e\": \"test\"\n" +
            "}";

    /**
     * The main function, that gets the root element of JSON, and traverses each element and subelements in a recursive fashion.
     * Along the way we are keeping track of the path to the terminal element (e.g. JsonPrimitive), when we hit JsonPrimitive, we set it as a new value for the created flat path.
     *
     * @param args - Json to be flatten
     * @return - Nothing
     * @exception Exception - On error.
     */
    public static void main(String[] args) {
        JsonFlattener jf = new JsonFlattener();
        Map<String, JsonPrimitive> flatMap = new HashMap<>();
        String result;

        try {
            jf.flattenerHelper(new String(), JsonParser.parseString(args[0]), flatMap);
            result = GSON.toJson(flatMap);
        } catch (Exception e) {
            System.out.println("Unable to flatten given JSON: " + originalJson);
            e.printStackTrace();
            return;
        }
        System.out.println(GSON.toJson(result));
    }

    /**
     * Helper to iterate through all elements of the Json.
     *
     * @param currentPath - Path that represents flat key for each Primitive
     * @param jsonElement - Current Json element that we are traversing
     * @param map - Stores new flat representation of the Json tree
     * @return - Nothing
     */
    private void flattenerHelper(String currentPath, JsonElement jsonElement, Map<String, JsonPrimitive> map) {
        if (jsonElement.isJsonPrimitive()) { // terminal element
            map.put(currentPath, jsonElement.getAsJsonPrimitive());
        } else if (jsonElement.isJsonObject()) { // nested Json object
            Iterator<Map.Entry<String, JsonElement>> iterator = jsonElement.getAsJsonObject().entrySet().iterator();
            String pathPrefix = currentPath.isEmpty() ? "" : currentPath + ".";

            while (iterator.hasNext()) {
                Map.Entry<String, JsonElement> entry = iterator.next();
                flattenerHelper(pathPrefix + entry.getKey(), entry.getValue(), map);
            }
        }
    }

    @Test
    public void JsonFlattenerTest() {
        try {
            Map<String, JsonPrimitive> flatMap = new HashMap<>();
            flattenerHelper(new String(), JsonParser.parseString(originalJson), flatMap);
            String flatJsonResult = GSON.toJson(flatMap);

            Assert.assertEquals(flatJsonResult, flatJson);
        } catch (Exception e) {
            System.out.println("Unable to flatten given JSON: " + originalJson);
            e.printStackTrace();
        }
    }
}
