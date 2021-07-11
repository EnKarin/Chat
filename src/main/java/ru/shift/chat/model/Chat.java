package ru.shift.chat.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

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

    @ApiModelProperty(
            value = "Optional subscription to RSS resource",
            name = "rssLink",
            dataType = "String",
            example = "https://lenta.ru/rss/news")
    @Column
    private String rssLink;

    @OneToMany(mappedBy = "chat", fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @JsonIgnore
    private List<Connection> connections;

    @OneToMany(mappedBy = "chat", fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @JsonIgnore
    private List<Message> messages;

    public Optional<String> getRssLink() {
        return Optional.ofNullable(rssLink);
    }

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
