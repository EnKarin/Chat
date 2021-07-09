package ru.shift.chat;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.shift.chat.model.User;
import ru.shift.chat.repository.ChatRepository;
import ru.shift.chat.repository.ConnectionRepository;
import ru.shift.chat.repository.MessageRepository;
import ru.shift.chat.repository.UserRepository;
import ru.shift.chat.service.DatabaseServiceImpl;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class ChatApplicationTests {
	@InjectMocks
	DatabaseServiceImpl databaseService;

	@Mock
	UserRepository userRepository;

	@Mock
	ChatRepository chatRepository;

	@Mock
	ConnectionRepository connectionRepository;

	@Mock
	MessageRepository messageRepository;

	@Test
	public void updateUser(){
		User finder = new User();
		finder.setUserId(1);
		finder.setFirstName("A");
		finder.setLastName("K");

		User user = new User();
		user.setFirstName("B");
		user.setLastName("C");

		User userWithId = new User();
		userWithId.setUserId(1);
		userWithId.setFirstName("B");
		userWithId.setLastName("C");

		Mockito.when(userRepository.findById(1)).thenReturn(Optional.of(finder));
		Mockito.when(userRepository.save(user)).thenReturn(userWithId);

		Assert.assertSame(userWithId, databaseService.updateUser(1, user));
	}

	@Test
	public void updateUserWithException(){
		User finder = new User();
		finder.setUserId(1);
		finder.setFirstName("A");
		finder.setLastName("K");

		User user = new User();
		user.setFirstName("B");
		user.setLastName("C");

		User userWithId = new User();
		userWithId.setUserId(1);
		userWithId.setFirstName("B");
		userWithId.setLastName("C");

		Mockito.when(userRepository.findById(1)).thenReturn(Optional.empty());
		Mockito.when(userRepository.save(user)).thenReturn(userWithId);

		Assert.assertThrows(NoSuchElementException.class, () -> databaseService.updateUser(1, user));
	}

	@Test
	public void enterChatWithExceptionIfUserDoNotContain(){
		Mockito.when(connectionRepository.findByUserIdAndChatId(1, 1)).thenReturn(List.of());
		Mockito.when(userRepository.findById(1)).thenReturn(Optional.empty());

		Assert.assertThrows(NoSuchElementException.class, () -> databaseService.enterChat(1, 1));
	}

	@Test
	public void enterChatWithExceptionIfChatDoNotContain(){
		Mockito.when(connectionRepository.findByUserIdAndChatId(1, 1)).thenReturn(List.of());
		Mockito.when(chatRepository.findById(1)).thenReturn(Optional.empty());

		Assert.assertThrows(NoSuchElementException.class, () -> databaseService.enterChat(1, 1));
	}

	@Test
	public void getAllMessageInCurrentChatDoNotExistChat(){
		Mockito.when(chatRepository.findById(2)).thenReturn(Optional.empty());
		Assert.assertThrows(NoSuchElementException.class, () -> databaseService.getAllMessageInCurrentChat(2, 0));
	}
}
