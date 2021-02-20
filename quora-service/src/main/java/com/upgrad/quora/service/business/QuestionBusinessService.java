package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.Question;
import com.upgrad.quora.service.entity.User;
import com.upgrad.quora.service.entity.UserAuth;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class QuestionBusinessService {

    @Autowired
    QuestionDao questionDao;

    @Autowired
    UserDao userDao;


    public Question createQuestion(final String content, final String authorizationToken) throws AuthorizationFailedException {

        UserAuth userAuthTokenEntity = userDao.getUserAuthToken(authorizationToken);

        /*  If user is not logged IN */
        if(userAuthTokenEntity == null){
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }

        /*  User is logged out  */
        if(userAuthTokenEntity.getLogoutAt() != null){
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to post a question");
        }

        User user = userDao.getUser(userAuthTokenEntity.getUserId().getUuid());
        Question question = new Question();
        question.setContent(content);
        question.setDate(new Date());
        question.setUuid(UUID.randomUUID().toString());
        question.setUser(user);
        questionDao.createQuestion(question);
        return question;
    }

    public List<Question> getAllQuestions(final String authorizationToken) throws AuthorizationFailedException {

        UserAuth userAuthTokenEntity = userDao.getUserAuthToken(authorizationToken);

        /*  If user is not logged IN */
        if(userAuthTokenEntity == null){
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }

        /*  User is logged out  */
        if(userAuthTokenEntity.getLogoutAt() != null){
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to post a question");
        }
        return questionDao.getAllQuestions();
    }

    public Question editQuestion(String questionUuid, String content, String authorizationToken) throws AuthorizationFailedException, InvalidQuestionException {
        UserAuth userAuthTokenEntity = userDao.getUserAuthToken(authorizationToken);

        /*  If user is not logged IN */
        if(userAuthTokenEntity == null){
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }

        /*  User is logged out  */
        if(userAuthTokenEntity.getLogoutAt() != null){
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to post a question");
        }

        Question question = questionDao.getQuestionByUuidId(questionUuid);
        /*  Question is not available   */
        if(question == null){
            throw new InvalidQuestionException("QUES-001","Entered question uuid does not exist");
        }

        /*  User is not author of the question  */
        if(userAuthTokenEntity.getUserId().getId() != question.getUser().getId()){
            throw new AuthorizationFailedException("ATHR-003","Only the question owner can edit the question");
        }
        question.setContent(content);
        questionDao.saveQuestion(question);
        return questionDao.getQuestionByUuidId(questionUuid);
    }

    public Question deleteQuestion(String questionUuid, String authorizationToken) throws AuthorizationFailedException, InvalidQuestionException {
        UserAuth userAuthTokenEntity = userDao.getUserAuthToken(authorizationToken);

        /*  If user is not logged IN */
        if(userAuthTokenEntity == null){
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }

        /*  User is logged out  */
        if(userAuthTokenEntity.getLogoutAt() != null){
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to post a question");
        }

        Question question = questionDao.getQuestionByUuidId(questionUuid);
        /*  Question is not available   */
        if(question == null){
            throw new InvalidQuestionException("QUES-001","Entered question uuid does not exist");
        }

        /*  User is not author of the question  */
        if(userAuthTokenEntity.getUserId().getId() != question.getUser().getId()){
            throw new AuthorizationFailedException("ATHR-003","Only the question owner or admin can delete the question");
        }
        return questionDao.deleteQuestion(question);
    }

    public List<Question> getAlQuestionsByUser(String userUuid, String authorizationToken) throws AuthorizationFailedException, UserNotFoundException {
        UserAuth userAuthTokenEntity = userDao.getUserAuthToken(authorizationToken);

        /*  If user is not logged IN */
        if(userAuthTokenEntity == null){
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }

        /*  User is logged out  */
        if(userAuthTokenEntity.getLogoutAt() != null){
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to post a question");
        }

        /*  User not found with given UUID  */
        User user = userDao.getUser(userUuid);
        if(user == null){
            throw new UserNotFoundException("USR-001","User with entered uuid whose question details are to be seen does not exist");
        }
        return questionDao.getAllQuestionsByUser(user);
    }
}
