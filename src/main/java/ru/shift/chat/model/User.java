package ru.shift.chat.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "user")
public class User {

    @ApiModelProperty(
            value = "User ID in the database. Not specified at creation",
            name = "userId",
            dataType = "int",
            example = "1")
    @Id
    @GeneratedValue( strategy = GenerationType.AUTO)
    private int userId;

    @ApiModelProperty(
            value = "First name of the user",
            name = "firstName",
            dataType = "String",
            example = "Alex")
    @Column
    private String firstName;

    @ApiModelProperty(
            value = "Last name of the user",
            name = "lastName",
            dataType = "String",
            example = "Meheev")
    @Column
    private String lastName;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @JsonIgnore
    private List<Connection> connections;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @JsonIgnore
    private List<Unchecked> unchecked;

    public int getUserId() {
        return userId;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public List<Connection> getConnections() {
        return connections;
    }
}
