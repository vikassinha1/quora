package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.Answer;
import com.upgrad.quora.service.entity.Question;
import com.upgrad.quora.service.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class AnswerDao {

    @Autowired
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public Answer createAnswer(Answer answer){
        entityManager.persist(answer);
        return answer;
    }

    public Answer getAnswerByUuid(String answerUuid){
        try{
            return entityManager.createNamedQuery("answerByUuid",Answer.class)
                    .setParameter("uuid",answerUuid).getSingleResult();
        } catch(NoResultException n){
            return null;
        }
    }

    @Transactional
    public void saveAnswer(Answer answer){
        entityManager.merge(answer);
    }

    @Transactional
    public Answer deleteAnswer(Answer answer) {
        entityManager.remove(answer);
        return answer;
    }

    public List<Answer> getAllAnswerToQuestion(Question question) {
        try{
            return entityManager.createNamedQuery("answerByQuestion",Answer.class)
                    .setParameter("question",question).getResultList();
        } catch(NoResultException n){
            return null;
        }
    }
}
