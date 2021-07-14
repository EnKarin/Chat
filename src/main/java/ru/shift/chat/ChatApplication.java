package ru.shift.chat;

import com.rometools.rome.io.FeedException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ChatApplication {

	public static void main(String[] args) throws FeedException {
		SpringApplication.run(ChatApplication.class, args);
	}
}
