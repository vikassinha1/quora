package com.upgrad.quora.service.dao;

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
public class QuestionDao {

    @Autowired
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public Question createQuestion(Question question){
        entityManager.persist(question);
        return question;
    }

    public List<Question> getAllQuestions(){
        try {
            return entityManager.createNamedQuery("getAllQuestions", Question.class).getResultList();
        } catch(NoResultException n){
            return null;
        }
    }

    public Question getQuestionByUuidId(String questionUuid){
        try{
            return entityManager.createNamedQuery("questionByUuid",Question.class)
                    .setParameter("uuid",questionUuid).getSingleResult();
        } catch(NoResultException n){
            return null;
        }
    }

    @Transactional
    public void saveQuestion(Question question){
        entityManager.merge(question);
    }

    @Transactional
    public Question deleteQuestion(Question question) {
        entityManager.remove(question);
        return question;
    }

    public List<Question> getAllQuestionsByUser(User user) {
        try{
            return entityManager.createNamedQuery("questionByUserID",Question.class)
                    .setParameter("user",user).getResultList();
        } catch(NoResultException n){
            return null;
        }
    }
}
