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
import ru.shift.chat.model.Chat;
import ru.shift.chat.model.Message;
import ru.shift.chat.model.User;
import ru.shift.chat.service.DatabaseServiceImpl;

import java.time.LocalDateTime;

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
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].text", is(neededMessage.getText())));
    }
}
