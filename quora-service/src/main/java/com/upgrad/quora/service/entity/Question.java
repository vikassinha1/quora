package com.upgrad.quora.service.entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

/*
* CREATE TABLE IF NOT EXISTS QUESTION(
*   id SERIAL,
*   uuid VARCHAR(200) NOT NULL,
*   content VARCHAR(500) NOT NULL,
*   date TIMESTAMP NOT NULL ,
*   user_id INTEGER NOT NULL,
* PRIMARY KEY(id),
* FOREIGN KEY (user_id)
* REFERENCES USERS(id) ON DELETE CASCADE);
 * */

@Entity
@Table(name = "QUESTION")
@NamedQueries({
        @NamedQuery(name = "questionByUserID" , query = "select q from Question q where q.user = :user "),
        @NamedQuery(name = "questionByUuid" , query = "select q from Question q where q.uuid = :uuid "),
        @NamedQuery(name = "getAllQuestions" , query = "select q from Question q ")

})
public class Question {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "UUID")
    @NotNull
    private String uuid;

    @Column(name = "content")
    @NotNull
    private String content;

    @Column(name = "DATE")
    @NotNull
    private Date date;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @NotNull
    private User user;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", date=" + date +
                ", user=" + user +
                '}';
    }
}
