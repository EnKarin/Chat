package ru.shift.chat.model;

import javax.persistence.*;

@Entity
public class Unchecked {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column
    private int chatId;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne
    @JoinColumn(name = "messageId")
    private Message message;

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public int getChatId() {
        return chatId;
    }

    public User getUser() {
        return user;
    }

    public Message getMessage() {
        return message;
    }
}
