package ru.shift.chat.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;

@Entity
public class Message {

    @ApiModelProperty(
            value = "Message ID in the database. Not specified at creation",
            name = "messageId",
            dataType = "int",
            example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int messageId;

    @ApiModelProperty(
            value = "User ID in the database",
            name = "userId",
            dataType = "int",
            example = "6")
    @Column
    private int userId;

    @ApiModelProperty(
            value = "Message text",
            name = "text",
            dataType = "String",
            example = "Hello!")
    @Column
    private String text;

    @Column
    private String sendTime;

    @ManyToOne()
    @JoinColumn(name = "chatId")
    @JsonIgnore
    private Chat chat;

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public Chat getChat() {
        return chat;
    }

    public int getMessageId() {
        return messageId;
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
