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

    @Autowired
    Gson gson;

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
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.jsonPath("code", is(equalTo("INCORRECT_ID"))));
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
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.jsonPath("code", is(equalTo("INCOMPLETE_INPUT"))));
    }

    @Test
    public void updateUser() throws Exception{
        User user = new User();
        user.setFirstName("C");
        user.setLastName("B");
        mockMvc.perform(MockMvcRequestBuilders.put("/user/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(user))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("userId", is(equalTo(1))))
                .andExpect(MockMvcResultMatchers.jsonPath("firstName", is(equalTo("C"))))
                .andExpect(MockMvcResultMatchers.jsonPath("lastName", is(equalTo("B"))));
    }

    @Test
    public void updateNotValidUser() throws Exception{
        User user = new User();
        user.setFirstName("C");
        user.setLastName(" ");
        mockMvc.perform(MockMvcRequestBuilders.put("/user/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(user))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.jsonPath("code", is(equalTo("INCOMPLETE_INPUT"))));
    }

    @Test
    public void updateNotExistId() throws Exception{
        User user = new User();
        user.setFirstName("C");
        user.setLastName("C");
        mockMvc.perform(MockMvcRequestBuilders.put("/user/0")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(user))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.jsonPath("code", is(equalTo("INCORRECT_ID"))));
    }
}
