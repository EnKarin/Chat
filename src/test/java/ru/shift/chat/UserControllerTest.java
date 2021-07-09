package ru.shift.chat;

import com.google.gson.Gson;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;
import ru.shift.chat.model.User;
import ru.shift.chat.service.DatabaseServiceImpl;

import static org.hamcrest.Matchers.*;

@ContextConfiguration(classes = {ChatApplication.class})
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    DatabaseServiceImpl databaseService;

    private final Gson gson = new Gson();

    @Test
    public void getAllTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/users")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void getUser() throws Exception {
        User user = new User();
        user.setFirstName("A");
        user.setLastName("K");
        user = databaseService.addUser(user);
        mockMvc.perform(MockMvcRequestBuilders.get("/user/" + user.getUserId()).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("firstName", is(equalTo(user.getFirstName()))))
                .andExpect(MockMvcResultMatchers.jsonPath("lastName", is(equalTo(user.getLastName()))));
    }

    @Test
    public void getNonExistUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/user/0").accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void saveUser() throws Exception {
        User user = new User();
        user.setFirstName("A");
        user.setLastName("K");
        mockMvc.perform(MockMvcRequestBuilders.post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(user))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("userId", is(not(empty()))))
                .andExpect(MockMvcResultMatchers.jsonPath("firstName", is(equalTo("A"))))
                .andExpect(MockMvcResultMatchers.jsonPath("lastName", is(equalTo("K"))));
    }

    @Test
    public void saveNotValidUser() throws Exception{
        User user = new User();
        user.setFirstName("");
        user.setLastName("K");
        mockMvc.perform(MockMvcRequestBuilders.post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(user))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }
}
