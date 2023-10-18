package nz.ac.canterbury.seng302.tab.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class APIService {

    /**
     * Private constructor to hide the implicit public one
     */
    private APIService() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Gets the JSON from the response of a HttpURLConnection
     * @param con The HttpURLConnection
     * @param results The key for the results in the JSON response
     * @return A JsonNode containing the JSON
     * @throws IOException If there is an error reading the response
     */
    public static JsonNode getJsonNode(HttpURLConnection con, String results) throws IOException {
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        StringBuilder content = new StringBuilder();
        String inputLine;
        while (null != (inputLine = in.readLine())) {
            content.append(inputLine);
        }
        in.close();

        ObjectMapper mapper = new ObjectMapper();
        String decodedResponse = URLDecoder.decode(content.toString(), StandardCharsets.UTF_8);
        JsonNode jsonResponse = mapper.readTree(decodedResponse);

        return jsonResponse.get(results);
    }
}
