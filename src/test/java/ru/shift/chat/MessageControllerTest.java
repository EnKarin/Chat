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
        User user = new User();
        user.setLastName("Us");
        user.setLastName("Us");
        user = databaseService.addUser(user);

        mockMvc.perform(MockMvcRequestBuilders.get("/messages")
                .accept(MediaType.APPLICATION_JSON)
                .param("userId", Integer.toString(user.getUserId())))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void getMessageInNotExistUser() throws Exception{
        Chat chat = new Chat();
        chat.setName("Test chat");
        chat = databaseService.addChat(chat);

        mockMvc.perform(MockMvcRequestBuilders.get("/messages")
                .accept(MediaType.APPLICATION_JSON)
                .param("userId", "0")
                .param("chatId", Integer.toString(chat.getChatId())))
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

        MessageDTO messageForDelete = new MessageDTO();
        messageForDelete.setChatId(chat.getChatId());
        messageForDelete.setText("Odd message");
        messageForDelete.setLifetimeSec(1);
        messageForDelete.setUserId(user.getUserId());
        messageForDelete.setSendTime(LocalDateTime.now().minusSeconds(1).toString());

        MessageDTO neededMessage = new MessageDTO();
        neededMessage.setLifetimeSec(5);
        neededMessage.setChatId(chat.getChatId());
        neededMessage.setText("Needed message");
        neededMessage.setUserId(user.getUserId());
        neededMessage.setSendTime(LocalDateTime.now().minusSeconds(1).toString());

        databaseService.addMessage(messageForDelete);
        databaseService.addMessage(neededMessage);

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
        Chat chat = new Chat();
        chat.setName("canCheckedMessage chat");
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
        map.put("text", "canCheckedMessage");

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
                        is("canCheckedMessage")));

        mockMvc.perform(MockMvcRequestBuilders.get("/messages/unread")
                .param("chatId", Integer.toString(chat.getChatId()))
                .param("userId", Integer.toString(first.getUserId()))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(0)));
    }

    @Test
    public void uncheckedMessageWithMultiUser() throws Exception {
        Chat chat = new Chat();
        chat.setName("uncheckedMessageWithMultiUser chat");
        chat = databaseService.addChat(chat);

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

        databaseService.enterChat(owner.getUserId(), chat.getChatId());
        databaseService.enterChat(first.getUserId(), chat.getChatId());
        databaseService.enterChat(second.getUserId(), chat.getChatId());

        Map<String, String> map = new TreeMap<>();
        map.put("chatId", Integer.toString(chat.getChatId()));
        map.put("userId", Integer.toString(owner.getUserId()));
        map.put("text", "uncheckedMessageWithMultiUser");

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
                        is("uncheckedMessageWithMultiUser")));

        mockMvc.perform(MockMvcRequestBuilders.get("/messages/unread")
                .param("chatId", Integer.toString(chat.getChatId()))
                .param("userId", Integer.toString(second.getUserId()))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].text",
                        is("uncheckedMessageWithMultiUser")));
    }

    @Test
    public void uncheckedMessageWithDelayTime() throws Exception{
        Chat chat = new Chat();
        chat.setName("uncheckedMessageWithDelayTime chat");
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
        map.put("text", "uncheckedMessageWithDelayTime");
        map.put("delaySec", "10");

        mockMvc.perform(MockMvcRequestBuilders.post("/message")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(map)).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/messages/unread")
                .param("chatId", Integer.toString(chat.getChatId()))
                .param("userId", Integer.toString(first.getUserId()))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(0)));
    }

    @Test
    public void uncheckedMessageForItOwner() throws Exception{
        Chat chat = new Chat();
        chat.setName("uncheckedMessageForItOwner chat");
        chat = databaseService.addChat(chat);

        User owner = new User();
        owner.setFirstName("Owner");
        owner.setLastName("O");
        owner = databaseService.addUser(owner);

        databaseService.enterChat(owner.getUserId(), chat.getChatId());

        Map<String, String> map = new TreeMap<>();
        map.put("chatId", Integer.toString(chat.getChatId()));
        map.put("userId", Integer.toString(owner.getUserId()));
        map.put("text", "uncheckedMessageForItOwner");

        mockMvc.perform(MockMvcRequestBuilders.post("/message")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(map)).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/messages/unread")
                .param("chatId", Integer.toString(chat.getChatId()))
                .param("userId", Integer.toString(owner.getUserId()))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(0)));
    }

    @Test
    public void uncheckedMessageWithLifeTime() throws Exception{
        Chat chat = new Chat();
        chat.setName("Test chat");
        chat = databaseService.addChat(chat);

        User user = new User();
        user.setLastName("A");
        user.setLastName("B");
        user = databaseService.addUser(user);

        User owner = new User();
        owner.setFirstName("Owner");
        owner.setLastName("O");
        owner = databaseService.addUser(owner);

        databaseService.enterChat(user.getUserId(), chat.getChatId());
        databaseService.enterChat(owner.getUserId(), chat.getChatId());

        MessageDTO messageForDelete = new MessageDTO();
        messageForDelete.setChatId(chat.getChatId());
        messageForDelete.setText("Odd message");
        messageForDelete.setLifetimeSec(1);
        messageForDelete.setUserId(owner.getUserId());
        messageForDelete.setSendTime(LocalDateTime.now().minusSeconds(1).toString());

        MessageDTO neededMessage = new MessageDTO();
        neededMessage.setLifetimeSec(5);
        neededMessage.setChatId(chat.getChatId());
        neededMessage.setText("Needed message");
        neededMessage.setUserId(owner.getUserId());
        neededMessage.setSendTime(LocalDateTime.now().minusSeconds(1).toString());

        databaseService.addMessage(messageForDelete);
        databaseService.addMessage(neededMessage);

        mockMvc.perform(MockMvcRequestBuilders.get("/messages/unread")
                .param("chatId", Integer.toString(chat.getChatId()))
                .param("userId", Integer.toString(user.getUserId()))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].text", is(neededMessage.getText())));
    }

    @Test
    public void getAllMessageAndGetUnread() throws Exception {
        Chat chat = new Chat();
        chat.setName("getAllMessageAndGetUnread chat");
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
        map.put("text", "getAllMessageAndGetUnread");

        mockMvc.perform(MockMvcRequestBuilders.post("/message")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(map)).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/messages")
                .param("chatId", Integer.toString(chat.getChatId()))
                .param("userId", Integer.toString(first.getUserId()))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].text",
                        is("getAllMessageAndGetUnread")));

        mockMvc.perform(MockMvcRequestBuilders.get("/messages/unread")
                .param("chatId", Integer.toString(chat.getChatId()))
                .param("userId", Integer.toString(first.getUserId()))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(0)));
    }

    @Test
    public void superuserAddMessage() throws Exception {
        Chat chat = new Chat();
        chat.setName("superuserAddMessage chat");
        chat = databaseService.addChat(chat);

        User first = new User();
        first.setLastName("First");
        first.setLastName("F");
        first = databaseService.addUser(first);

        databaseService.enterChat(first.getUserId(), chat.getChatId());

        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setSendTime(LocalDateTime.now().toString());
        messageDTO.setChatId(chat.getChatId());
        messageDTO.setText("superuserAddMessage");
        messageDTO.setLifetimeSec(-1);
        messageDTO.setUserId(-1);

        databaseService.addMessage(messageDTO);

        mockMvc.perform(MockMvcRequestBuilders.get("/messages")
                .param("chatId", Integer.toString(chat.getChatId()))
                .param("userId", Integer.toString(first.getUserId()))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].text",
                        is("superuserAddMessage")));
    }
}
