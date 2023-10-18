package nz.ac.canterbury.seng302.tab.controller;

import io.cucumber.cienvironment.internal.com.eclipsesource.json.Json;
import io.cucumber.cienvironment.internal.com.eclipsesource.json.JsonObject;
import jakarta.annotation.Resource;
import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.stats.GameScore;
import nz.ac.canterbury.seng302.tab.repository.ActivityRepository;
import nz.ac.canterbury.seng302.tab.service.ActivityStatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ActivityRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Resource
    ActivityStatService activityStatService;

    @Resource
    ActivityRepository activityRepository;


    @Autowired
    private WebApplicationContext wac;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("morgan.english@hotmail.com", null));
    }

    @Test
    void testGetAllPlayersInTeam() throws Exception {
        long teamId = 1L;
        mockMvc.perform(get("/activities/getPlayers/"+teamId))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.length()").value(4));
    }

    @Test
    void testGetAllPlayersInNonExistentTeam() throws Exception {
        long teamId = -1L;
        mockMvc.perform(get("/activities/getPlayers/"+teamId))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testAddSubstitution() throws Exception {
        long teamId = 1L;

        JsonObject body = Json.object()
                .add("activityId", "1")
                .add("minute", "10")
                .add("playerOn", "1")
                .add("playerOff", "2")
                .add("team", Long.toString(teamId));

        mockMvc.perform(post("/activities/addSubstitution")
                .contentType("application/json")
                .content(body.toString()))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void testAddSubstitutionNonExistentTeam() throws Exception {
        long teamId = -1L;

        JsonObject body = Json.object()
                .add("activityId", "1")
                .add("minute", "10")
                .add("playerOn", "17")
                .add("playerOff", "18")
                .add("team", Long.toString(teamId));

        mockMvc.perform(post("/activities/addSubstitution")
                        .contentType("application/json")
                        .content(body.toString()))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testAddScore() throws Exception {
        long teamId = 1L;

        JsonObject body = Json.object()
                .add("activityId", "1")
                .add("minute", "10")
                .add("player", "17")
                .add("points", "18")
                .add("team", Long.toString(teamId));

        mockMvc.perform(post("/activities/addScore")
                        .contentType("application/json")
                        .content(body.toString()))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void validInput_addFact() throws Exception {
        JsonObject body = Json.object()
                .add("actId", "1")
                .add("description", "test")
                .add("time", "10");

        mockMvc.perform(post("/activities/addFact")
                        .contentType("application/json")
                        .content(body.toString()))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void invalidInput_addFact() throws Exception {
        JsonObject body = Json.object()
                .add("actId", "1")
                .add("description", "a")
                .add("time", "10:00");

        mockMvc.perform(post("/activities/addFact")
                        .contentType("application/json")
                        .content(body.toString()))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void getFacts() throws Exception {
        mockMvc.perform(get("/activities/getFacts/1"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getFacts_noId() throws Exception {
        mockMvc.perform(get("/activities/getFacts/"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void deleteFact() throws Exception {
        mockMvc.perform(delete("/activities/deleteFact/1"))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void deleteFact_noId() throws Exception {
        mockMvc.perform(delete("/activities/deleteFact/"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testAddScoreNonExistentTeam() throws Exception {
        long teamId = -1L;

        JsonObject body = Json.object()
                .add("activityId", "1")
                .add("minute", "10")
                .add("player", "17")
                .add("points", "18")
                .add("team", Long.toString(teamId));

        mockMvc.perform(post("/activities/addScore")
                        .contentType("application/json")
                        .content(body.toString()))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testGetScores() throws Exception {
        long activityId = 1L;

        mockMvc.perform(get("/activities/getScores/"+activityId))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void saveGameScores_valid() throws Exception {

        JsonObject body = Json.object()
                .add("score1", "1")
                .add("score2", "10");

        mockMvc.perform(post("/activities/saveGameScore/1")
                        .contentType("application/json")
                        .content(body.toString()))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void saveGameScores_noActivity() throws Exception {

        JsonObject body = Json.object()
                .add("score1", "1")
                .add("score2", "10");

        mockMvc.perform(post("/activities/saveGameScore/100")
                        .contentType("application/json")
                        .content(body.toString()))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void saveGameScores_hasGameScore() throws Exception {

        JsonObject body = Json.object()
                .add("score1", "1-p")
                .add("score2", "10");

        mockMvc.perform(post("/activities/saveGameScore/1")
                        .contentType("application/json")
                        .content(body.toString()))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void getGameScores_noGameScore() throws Exception {

        mockMvc.perform(get("/activities/getGameScore/1"))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void getGameScores_hasGameScore() throws Exception {
        Activity activity = activityRepository.findById(1L).get();

        activityStatService.saveGameScore(new GameScore(activity,"1","2"));

        mockMvc.perform(get("/activities/getGameScore/1"))
                .andExpect(status().is2xxSuccessful());
    }



    @Test
    void addFact_afterEnd() throws Exception {
        JsonObject body = Json.object()
                .add("actId", "1")
                .add("description", "test")
                .add("time", "604800");

        mockMvc.perform(post("/activities/addFact")
                        .contentType("application/json")
                        .content(body.toString()))
                .andExpect(status().is4xxClientError());
    }
}
