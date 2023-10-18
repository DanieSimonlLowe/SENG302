package nz.ac.canterbury.seng302.tab.service;

import com.fasterxml.jackson.databind.JsonNode;
import nz.ac.canterbury.seng302.tab.controller.LocationAPIController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.HttpURLConnection;
import java.net.URL;

import static nz.ac.canterbury.seng302.tab.service.APIService.getJsonNode;

public class LocationAPI {


    private final String apiKey;

    private final String results = "results";

    public LocationAPI(String key) {
        this.apiKey = key;
    }

    /** The logger */
    final Logger logger = LoggerFactory.getLogger(LocationAPIController.class);

    public JsonNode getResult(String input) {
        String inputLocation = input.replaceAll(" ", "%20");
        String requestUrl = "https://www.mapquestapi.com/search/v3/prediction?limit=5&collection=poi,airport,address,adminArea&undefined=undefined&q=" + inputLocation + "&key=" + apiKey;
        URL url;
        try {
            url = new URL(requestUrl);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("GET");
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);

            return getJsonNode(con, results);

        } catch(Exception exception) {
            logger.error(exception.toString());
            return null;
        }
    }

}
