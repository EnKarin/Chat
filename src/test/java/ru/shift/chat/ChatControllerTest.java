package ru.shift.chat;

import com.google.gson.Gson;
import org.junit.Assert;
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
import ru.shift.chat.DTO.MessageDTO;
import ru.shift.chat.exception.ConnectionNotFoundException;
import ru.shift.chat.model.Chat;
import ru.shift.chat.model.User;
import ru.shift.chat.service.DatabaseServiceImpl;
import ru.shift.chat.service.FeedConsumer;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@ContextConfiguration(classes = {ChatApplication.class})
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@AutoConfigureMockMvc
public class ChatControllerTest {
    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    DatabaseServiceImpl databaseService;

    @Autowired
    Gson gson;

    @Autowired
    FeedConsumer feedConsumer;

    @Test
    public void saveChat() throws Exception {
        Chat chat = new Chat();
        chat.setName("Test chat");
        mockMvc.perform(MockMvcRequestBuilders.post("/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(chat))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("name", is("Test chat")));
    }

    @Test
    public void twoEnterAndOneLiveChat() throws Exception{
        User user = new User();
        user.setLastName("L");
        user.setFirstName("F");
        user = databaseService.addUser(user);

        Chat chat = new Chat();
        chat.setName("Test chat");
        chat = databaseService.addChat(chat);

        Map<String, String> map = new HashMap<>();
        map.put("userId", Integer.toString(user.getUserId()));
        map.put("chatId", Integer.toString(chat.getChatId()));

        mockMvc.perform(MockMvcRequestBuilders.post("/chat/enter")
        .contentType(MediaType.APPLICATION_JSON).content(gson.toJson(map)).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.post("/chat/enter")
        .contentType(MediaType.APPLICATION_JSON).content(gson.toJson(map)).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.post("/chat/leave")
        .contentType(MediaType.APPLICATION_JSON).content(gson.toJson(map)).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        MessageDTO message = new MessageDTO();
        message.setText("Test message");
        message.setChatId(chat.getChatId());
        message.setUserId(user.getUserId());
        message.setSendTime(LocalDateTime.now().toString());
        message.setLifetimeSec(-1);

        Assert.assertThrows(ConnectionNotFoundException.class, () -> databaseService.addMessage(message));
    }

    @Test
    public void rssCreateMessage() throws Exception {
        Chat chat = new Chat();
        chat.setRssLink("https://lenta.ru/rss/news");
        chat.setName("Test chat");
        chat = databaseService.addChat(chat);

        User user = new User();
        user.setFirstName("User");
        user.setLastName("U");
        user = databaseService.addUser(user);

        databaseService.enterChat(user.getUserId(), chat.getChatId());

        mockMvc.perform(MockMvcRequestBuilders.get("/messages")
                .param("chatId", Integer.toString(chat.getChatId()))
                .param("userId", Integer.toString(user.getUserId()))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)));
    }
}
