package nz.ac.canterbury.seng302.tab.controller;

import nz.ac.canterbury.seng302.tab.entity.*;
import nz.ac.canterbury.seng302.tab.service.ActivityService;
import nz.ac.canterbury.seng302.tab.service.TeamMemberService;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "morgan.english@hotmail.com")
class TeamProfileTempTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @MockBean
    private TeamService teamService;

    @Mock
    private UserService userService;

    @MockBean
    private ActivityService activityService;

    @MockBean
    private TeamMemberService teamMemberService;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    void get_teamProfileTemp_nonExistentTeam_homeRedirection() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("tom.barthelmeh@hotmail.com", null));
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("id", "-1");
        requestParams.add("managerIds", "1");
        requestParams.add("coachIds", "1");
        requestParams.add("isVisibleMembers", "true");
        requestParams.add("isVisibleFormations", "false");
        requestParams.add("memberIds", "1");
        requestParams.add("userId", "1");
        requestParams.add("newRole", "Manager");


        MvcResult mvcResult1 = mockMvc.perform(get("/teamProfileTemp")
                        .params(requestParams))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        String redirectedUrl = mvcResult1.getResponse().getRedirectedUrl();
        Assertions.assertEquals("/", redirectedUrl);
    }
}