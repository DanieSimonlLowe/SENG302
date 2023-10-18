package nz.ac.canterbury.seng302.tab.controller;

import com.fasterxml.jackson.databind.JsonNode;
import nz.ac.canterbury.seng302.tab.service.LocationAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * This is a rest controller handling requests for the location API key, note the @link{RestController} annotation which defines this.
 * This controller defines endpoints as functions with specific HTTP mappings
 */
@RestController
@RequestMapping("/api")
public class LocationAPIController {

    /** The logger */
    final Logger logger = LoggerFactory.getLogger(LocationAPIController.class);

    /** Api key */
    @Value("${mapquest.api.key}")
    private String apiKey;

    /**
     * Returns the API key for the location API
     * @return the API key as a string
     */
    @GetMapping("/get-key")
    public String getKey() {
        logger.info("GET /api/get-key");
        return apiKey;
    }

    /**
     * Makes a request to <a href="https://www.mapquestapi.com/search/v3/">...</a> and returns
     * the JSON it responds with.
     *
     * @return A ResponseEntity containing the JSON
     */
    @GetMapping("/location-request")
    public JsonNode requestLocations(@RequestParam("searchValue") String value) {

        logger.info("GET /location-request");
        LocationAPI locationAPI = new LocationAPI(apiKey);
        return locationAPI.getResult(value);

    }
}
