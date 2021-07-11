package ru.shift.chat;

import com.google.gson.Gson;
import com.rometools.rome.io.FeedException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import ru.shift.chat.service.FeedConsumer;

@SpringBootApplication
public class ChatApplication {

	public static void main(String[] args) throws FeedException {
		SpringApplication.run(ChatApplication.class, args);
	}

	@Bean
	Gson gson(){
		return new Gson();
	}
}
