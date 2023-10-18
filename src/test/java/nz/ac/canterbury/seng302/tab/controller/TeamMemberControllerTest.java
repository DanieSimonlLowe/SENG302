package nz.ac.canterbury.seng302.tab.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "morgan.english@hotmail.com")
 class TeamMemberControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private TeamMemberController teamMemberController;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    void notManager_checkManager_returnsFalse() {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("morgan.english@hotmail.com", null));
        Assertions.assertFalse(teamMemberController.checkIsManager(1L));
    }

    @Test
    void Manager_checkManager_returnsTrue() {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("tom.barthelmeh@hotmail.com", null));
        Assertions.assertTrue(teamMemberController.checkIsManager(1L));
    }

    @Test
    void Manager_checkManager_nonExistentTeam_returnsFalse() {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("tom.barthelmeh@hotmail.com", null));
        Assertions.assertFalse(teamMemberController.checkIsManager(-1L));
    }

    @Test
    void checkIsInTeam_nonExistentTeam_returnsFalse() {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("tom.barthelmeh@hotmail.com", null));
        Assertions.assertFalse(teamMemberController.checkIsInTeam(-1L));
    }

    @Test
    void post_setTeamRoles_nonExistentTeam_homeRedirection() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("tom.barthelmeh@hotmail.com", null));
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("id", "-1");
        requestParams.add("managerIds", "1");
        requestParams.add("coachIds", "1");
        requestParams.add("memberIds", "1");

        MvcResult mvcResult1 = mockMvc.perform(post("/teamMembers")
                        .params(requestParams))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        String redirectedUrl = mvcResult1.getResponse().getRedirectedUrl();
        Assertions.assertEquals("/", redirectedUrl);
    }

    @Test
    void get_myTeams_asMorgan_containsClubs() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("morgan.english@hotmail.com", null));

        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        MvcResult result = mockMvc.perform(get("/myTeams")
                        .params(requestParams))
                        .andExpect(status().isOk())
                        .andExpect(view().name("myTeams"))
                        .andExpect(model().attributeExists("teamClubs"))
                        .andReturn();

        // Assert that the number of clubs for each team is right (correct number of teams and is getting clubs)
        Object teamClubsObj = result.getModelAndView().getModel().get("teamClubs");
        List<?> teamClubs = new ArrayList<>((Collection<?>)teamClubsObj);
        Assertions.assertTrue(teamClubs.size() > 0);
    }
}