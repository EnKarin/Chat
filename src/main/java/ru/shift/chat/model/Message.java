package ru.shift.chat.model;

import javax.persistence.*;

@Entity
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int messageId;

    @Column
    private int userId;

    @Column
    private String text;

    @Column
    private String sendTime;

    @ManyToOne()
    @JoinColumn(name = "chatId")
    private Chat chat;

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSendTime() {
        return sendTime.replace('T', ' ');
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }
}
