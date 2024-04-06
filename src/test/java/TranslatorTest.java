import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Stream;


public class TranslatorTest {

    @ParameterizedTest
    @MethodSource("translationTestData")
    public void testTranslationAPI(String text, String sourceLang, String targetLang, String expectedTranslation) {
        try {
            String apiUrl = "http://translationapi.assistantslab.com/translate";

            // Create a URL object with the API endpoint
            URL url = new URL(apiUrl);

            // Open a connection to the URL
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set the request method to POST
            connection.setRequestMethod("POST");

            // Enable input/output streams
            connection.setDoOutput(true);
            connection.setDoInput(true);

            // Set the content type to JSON
            connection.setRequestProperty("Content-Type", "application/json");

            // Create JSON payload
            String jsonInputString = "{\"text\":\"" + text + "\",\"source_lang\":\"" + sourceLang
                    + "\",\"target_lang\":\"" + targetLang + "\"}";

            // Write the JSON payload to the connection's output stream
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Get the response from the server
            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            }

            // Parse JSON
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(response.toString(), JsonObject.class);

            // Extract translation value
            JsonElement translationElement = jsonObject.get("translation");
            String translation = "";
            if (translationElement != null) {
                if (translationElement.isJsonArray()) {
                    JsonArray translationArray = translationElement.getAsJsonArray();
                    if (!translationArray.isEmpty()) {
                        translation = translationArray.get(0).getAsString();
                    }
                } else if (translationElement.isJsonPrimitive()) {
                    translation = translationElement.getAsString();
                }
            }

            Assertions.assertEquals(expectedTranslation, translation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Stream<Object[]> translationTestData() {
        return Stream.of(
                new Object[]{"Hoe gaat het?", "NL", "EN", "How are we doing it?"},
                new Object[]{"How are you?", "EN", "NL", "Hoe gaat het?"}
        );
    }

}
