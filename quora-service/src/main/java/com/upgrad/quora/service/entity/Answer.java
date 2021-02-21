package com.upgrad.quora.service.entity;

/*
* CREATE TABLE IF NOT EXISTS ANSWER(
*   id SERIAL,
*   uuid VARCHAR(200) NOT NULL,
*   ans VARCHAR(255) NOT NULL,
*   date TIMESTAMP NOT NULL ,
*   user_id INTEGER NOT NULL,
*   question_id INTEGER NOT NULL ,
*   PRIMARY KEY(id),
*   FOREIGN KEY (user_id)
*   REFERENCES USERS(id) ON DELETE CASCADE,
*   FOREIGN KEY (question_id) REFERENCES QUESTION(id) ON DELETE CASCADE);

 * */

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "ANSWER")
@NamedQueries({
        @NamedQuery(name = "answerByUuid" , query = "select a from Answer a where a.uuid = :uuid "),
        @NamedQuery(name = "answerByQuestion" , query = "select a from Answer a where a.question = :question ")

})
public class Answer {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "UUID")
    @NotNull
    private String uuid;

    @Column(name = "ans")
    @NotNull
    private String ans;

    @Column(name = "DATE")
    @NotNull
    private Date date;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    @NotNull
    private User user;

    @ManyToOne
    @JoinColumn(name = "QUESTION_ID")
    @NotNull
    private Question question;

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

    public String getAns() {
        return ans;
    }

    public void setAns(String ans) {
        this.ans = ans;
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

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    @Override
    public String toString() {
        return "Answer{" +
                "id=" + id +
                ", uuid='" + uuid + '\'' +
                ", ans='" + ans + '\'' +
                ", date=" + date +
                ", user=" + user +
                ", question=" + question +
                '}';
    }
}
