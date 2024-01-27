
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
    public void testTranslationApi(String text, String sourceLang, String targetLang, String expectedTranslation) {
        try {
            String apiUrl = "http://159.69.60.114:8000/translate";

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

            // Close the connection
            connection.disconnect();

            // Assert the expected translation result
            Assertions.assertEquals(expectedTranslation, response.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Stream<Object[]> translationTestData() {
        return Stream.of(
                new Object[]{"Hoe gaat het vandaag?", "nld", "eng", "{\"translation\":\"How's it going today?\"}"},
                new Object[]{"Cómo te va", "spa", "eng", "{\"translation\":\"How's it going?\"}"},
                new Object[]{"Çkemi si jeni ju", "alb", "eng", "{\"translation\":\"How's it going?\"}"}
                //new Object[]{"Bonjour", "fra", "eng", "{\"translation\":\"Hello\"}"}
                // Add more test cases as needed
                // new Object[]{"...", "...", "...", "..."},
        );
    }

}
