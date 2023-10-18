package nz.ac.canterbury.seng302.tab.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ClubRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("morgan.english@hotmail.com", null));
    }

    @Test
    void testGetClubTeams() throws Exception {
        long teamId = 1;

        mockMvc.perform(get("/clubs/getTeams/" + teamId))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void testGetClubTeamsNonExistentTeam() throws Exception {
        long teamId = -1L;

        MvcResult result = mockMvc.perform(get("/clubs/getTeams/" + teamId))
                .andExpect(status().is4xxClientError())
                .andReturn();

        Assertions.assertEquals("Invalid request.", result.getResponse().getContentAsString());
    }
}
