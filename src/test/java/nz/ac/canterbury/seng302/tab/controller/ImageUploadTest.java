package nz.ac.canterbury.seng302.tab.controller;

import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
@AutoConfigureMockMvc
@WithAnonymousUser
public class ImageUploadTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TeamService teamService;

    @Autowired
    private WebApplicationContext wac;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void uploadImageTest() throws Exception {
        // Create a mock MultipartFile
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.png",
                "image/png",
                "test image content".getBytes()
        );

        // Mock the service response
        Team team = new Team();
        team.setId(1L);
        Mockito.when(teamService.getTeam(1L)).thenReturn(Optional.of(team));

        // Perform the POST request
        mockMvc.perform(MockMvcRequestBuilders.multipart("/teamProfile")
                .file(file)
                .param("id", "1"));


        // Verify that the team's profile picture was updated
        ArgumentCaptor<Team> teamCaptor = ArgumentCaptor.forClass(Team.class);
        Mockito.verify(teamService, Mockito.times(1)).addProfilePic(teamCaptor.capture());
        Team capturedTeam = teamCaptor.getValue();

        assertEquals(Path.of("profilePics/teamProfile-1.png"), Path.of(capturedTeam.getProfilePicName()));
    }
}
