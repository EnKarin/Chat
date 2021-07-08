package ru.shift.chat.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "chat")
public class Chat {

    @ApiModelProperty(
            value = "Chat ID in the database. Not specified at creation",
            name = "chatId",
            dataType = "int",
            example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int chatId;

    @ApiModelProperty(
            value = "Chat name",
            name = "name",
            dataType = "String",
            example = "Private chat")
    @Column
    private String name;

    @OneToMany(mappedBy = "chat", fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @JsonIgnore
    private List<Connection> connections;

    @OneToMany(mappedBy = "chat", fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @JsonIgnore
    private List<Message> messages;

    public List<Message> getMessages() {
        return messages;
    }

    public int getChatId() {
        return chatId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Connection> getConnections() {
        return connections;
    }
}
