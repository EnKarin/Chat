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
import ru.shift.chat.model.Chat;
import ru.shift.chat.model.Message;
import ru.shift.chat.model.User;
import ru.shift.chat.service.DatabaseServiceImpl;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.TreeMap;

import static org.hamcrest.Matchers.*;

@ContextConfiguration(classes = {ChatApplication.class})
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@AutoConfigureMockMvc
public class MessageControllerTest {
    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    DatabaseServiceImpl databaseService;

    @Autowired
    Gson gson;

    @Test
    public void getMessageInNotExistChat() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get("/messages").accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void getMessageInNotExistUser() throws Exception{
        Chat chat = new Chat();
        chat.setName("Test chat");
        chat = databaseService.addChat(chat);

        mockMvc.perform(MockMvcRequestBuilders.get("/messages")
                .accept(MediaType.APPLICATION_JSON)
                .param("chatId", Integer.toString(chat.getChatId()))
                .param("userId", "0"))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void getMessageWithLifetime() throws Exception{
        Chat chat = new Chat();
        chat.setName("Test chat");
        chat = databaseService.addChat(chat);

        User user = new User();
        user.setLastName("A");
        user.setLastName("B");
        user = databaseService.addUser(user);

        databaseService.enterChat(user.getUserId(), chat.getChatId());

        Message messageForDelete = new Message();
        messageForDelete.setChat(chat);
        messageForDelete.setText("Odd message");
        messageForDelete.setSendTime(LocalDateTime.now().minusSeconds(1).toString());
        messageForDelete.setLifetimeSec(1);
        messageForDelete.setUserId(user.getUserId());

        Message neededMessage = new Message();
        neededMessage.setSendTime(LocalDateTime.now().toString());
        neededMessage.setLifetimeSec(5);
        neededMessage.setChat(chat);
        neededMessage.setText("Needed message");
        neededMessage.setUserId(user.getUserId());

        databaseService.addMessage(messageForDelete, chat.getChatId());
        databaseService.addMessage(neededMessage, chat.getChatId());

        mockMvc.perform(MockMvcRequestBuilders.get("/messages")
                .param("chatId", Integer.toString(chat.getChatId()))
                .param("userId", Integer.toString(user.getUserId()))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].text", is(neededMessage.getText())));
    }

    @Test
    public void saveMessage() throws Exception{
        Chat chat = new Chat();
        chat.setName("Test chat");
        chat = databaseService.addChat(chat);

        User user = new User();
        user.setLastName("A");
        user.setLastName("B");
        user = databaseService.addUser(user);

        databaseService.enterChat(user.getUserId(), chat.getChatId());

        Map<String, String> map = new TreeMap<>();
        map.put("chatId", Integer.toString(chat.getChatId()));
        map.put("userId", Integer.toString(user.getUserId()));
        map.put("text", "Text");
        map.put("sendTime", LocalDateTime.now().toString());
        map.put("lifetimeSec", "2");

        mockMvc.perform(MockMvcRequestBuilders.post("/message")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(map))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("text", is("Text")));

        Thread.sleep(2000);
        Assert.assertTrue(databaseService.getAllMessage(chat.getChatId(), user.getUserId()).stream()
                .noneMatch(message -> message.getText().equals("Text")));
    }

    @Test
    public void saveMessageInNotExistChat() throws Exception{
        User user = new User();
        user.setLastName("A");
        user.setLastName("B");
        user = databaseService.addUser(user);

        Map<String, String> map = new TreeMap<>();
        map.put("chatId", "1000");
        map.put("userId", Integer.toString(user.getUserId()));
        map.put("text", "Text");
        map.put("sendTime", LocalDateTime.now().toString());

        mockMvc.perform(MockMvcRequestBuilders.post("/message")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(map))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void saveMessageInNotExistConnection() throws Exception{
        Chat chat = new Chat();
        chat.setName("Test chat");
        chat = databaseService.addChat(chat);

        User user = new User();
        user.setLastName("A");
        user.setLastName("B");
        user = databaseService.addUser(user);

        Map<String, String> map = new TreeMap<>();
        map.put("chatId", Integer.toString(chat.getChatId()));
        map.put("userId", Integer.toString(user.getUserId()));
        map.put("text", "Text");
        map.put("sendTime", LocalDateTime.now().toString());

        mockMvc.perform(MockMvcRequestBuilders.post("/message")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(map))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void uncheckedMessageInPrivateChat() throws Exception{
        Chat chat = new Chat();
        chat.setName("uncheckedMessageInPrivateChat chat");
        chat = databaseService.addChat(chat);

        User first = new User();
        first.setLastName("First");
        first.setLastName("F");
        first = databaseService.addUser(first);

        User owner = new User();
        owner.setFirstName("Owner");
        owner.setLastName("O");
        owner = databaseService.addUser(owner);

        databaseService.enterChat(owner.getUserId(), chat.getChatId());
        databaseService.enterChat(first.getUserId(), chat.getChatId());

        Map<String, String> map = new TreeMap<>();
        map.put("chatId", Integer.toString(chat.getChatId()));
        map.put("userId", Integer.toString(owner.getUserId()));
        map.put("text", "uncheckedMessageInPrivateChat");
        map.put("sendTime", LocalDateTime.now().toString());

        mockMvc.perform(MockMvcRequestBuilders.post("/message")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(map)).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/messages/unread")
                .param("chatId", Integer.toString(chat.getChatId()))
                .param("userId", Integer.toString(first.getUserId()))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].text",
                        is("uncheckedMessageInPrivateChat")));
    }

    @Test
    public void canCheckedMessage() throws Exception{
        User first = new User();
        first.setLastName("First");
        first.setLastName("F");
        first = databaseService.addUser(first);

        User owner = new User();
        owner.setFirstName("Owner");
        owner.setLastName("O");
        owner = databaseService.addUser(owner);

        Map<String, String> map = new TreeMap<>();
        map.put("chatId", "0");
        map.put("userId", Integer.toString(owner.getUserId()));
        map.put("text", "canCheckedMessage");
        map.put("sendTime", LocalDateTime.now().toString());

        mockMvc.perform(MockMvcRequestBuilders.post("/message")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(map)).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/messages/unread")
                .param("chatId", "0")
                .param("userId", Integer.toString(first.getUserId()))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].text",
                        is("canCheckedMessage")));

        mockMvc.perform(MockMvcRequestBuilders.get("/messages/unread")
                .param("chatId", "0")
                .param("userId", Integer.toString(first.getUserId()))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(0)));
    }

    @Test
    public void uncheckedMessageWithMultiUser() throws Exception{
        User first = new User();
        first.setLastName("First");
        first.setLastName("F");
        first = databaseService.addUser(first);

        User owner = new User();
        owner.setFirstName("Owner");
        owner.setLastName("O");
        owner = databaseService.addUser(owner);

        User second = new User();
        second.setLastName("First");
        second.setLastName("F");
        second = databaseService.addUser(second);

        Map<String, String> map = new TreeMap<>();
        map.put("chatId", "0");
        map.put("userId", Integer.toString(owner.getUserId()));
        map.put("text", "uncheckedMessageWithMultiUser");
        map.put("sendTime", LocalDateTime.now().toString());

        mockMvc.perform(MockMvcRequestBuilders.post("/message")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(map)).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/messages/unread")
                .param("chatId", "0")
                .param("userId", Integer.toString(first.getUserId()))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].text",
                        is("uncheckedMessageWithMultiUser")));

        mockMvc.perform(MockMvcRequestBuilders.get("/messages/unread")
                .param("chatId", "0")
                .param("userId", Integer.toString(second.getUserId()))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].text",
                        is("uncheckedMessageWithMultiUser")));
    }
}
