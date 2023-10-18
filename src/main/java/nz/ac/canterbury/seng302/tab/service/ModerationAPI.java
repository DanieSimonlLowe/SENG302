package nz.ac.canterbury.seng302.tab.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static nz.ac.canterbury.seng302.tab.service.APIService.getJsonNode;

public class ModerationAPI {
    /** The API key to be used for the requests */
    private final String apiKey;
    /** The key for the results in the JSON response */
    private static final String RESULTS = "results";
    /** The logger for the ModerationAPI class */
    private final Logger logger = LoggerFactory.getLogger(ModerationAPI.class);

    /**
     * Constructor for the ModerationAPI class
     * @param key The API key to be used for the requests
     */
    public ModerationAPI (String key) {
        this.apiKey = key;
    }

    /** Makes a POST request to the OpenAI Moderation API and returns the JSON it responds with.
     * @param input The text to be moderated
     * @return A JsonNode containing the JSON
     */
    public JsonNode getResult(String input) {
        String requestUrl = "https://api.openai.com/v1/moderations";
        URL url;
        try {
            url = new URL(requestUrl);

            // Set up the connection
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Authorization", "Bearer " + apiKey);
            con.setDoOutput(true);

            // Add the input to the request body
            try(OutputStream os = con.getOutputStream()) {
                byte[] body = input.getBytes(StandardCharsets.UTF_8);
                os.write(body, 0, body.length);
            }

            // Get the response
            return getJsonNode(con, RESULTS);

        } catch(Exception exception) {
            logger.error(exception.toString());
            return null;
        }
    }

}
