package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.Answer;
import com.upgrad.quora.service.entity.Question;
import com.upgrad.quora.service.entity.User;
import com.upgrad.quora.service.entity.UserAuth;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class AnswerBusinessService {

    @Autowired
    QuestionDao questionDao;

    @Autowired
    UserDao userDao;

    @Autowired
    AnswerDao answerDao;

    public Answer createAnswer(String content, String questionUuid, String authorizationToken) throws AuthorizationFailedException, InvalidQuestionException {

        Question question = questionDao.getQuestionByUuidId(questionUuid);
        if(question == null){
            throw new InvalidQuestionException("QUES-001","The question entered is invalid");
        }

        UserAuth userAuthTokenEntity = userDao.getUserAuthToken(authorizationToken);

            /*  If user is not logged IN */
        if(userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }

        /*  User is logged out  */
        if(userAuthTokenEntity.getLogoutAt() != null){
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to post a question");
        }

        User user = userDao.getUser(userAuthTokenEntity.getUserId().getUuid());
        Answer answer = new Answer();
        answer.setAns(content);
        answer.setDate(new Date());
        answer.setQuestion(question);
        answer.setUser(user);
        answer.setUuid(UUID.randomUUID().toString());
        answerDao.createAnswer(answer);
        return answer;
    }

    public Answer editAnswerContent(String content, String answerUuid, String authorizationToken) throws AuthorizationFailedException, AnswerNotFoundException {

        UserAuth userAuthTokenEntity = userDao.getUserAuthToken(authorizationToken);

        /*  If user is not logged IN */
        if(userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }

        /*  User is logged out  */
        if(userAuthTokenEntity.getLogoutAt() != null){
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to post a question");
        }

        Answer answer = answerDao.getAnswerByUuid(answerUuid);
        if(answer == null){
            throw new AnswerNotFoundException("ANS-001","Entered answer uuid does not exist");
        }

        if(answer.getUser().getId() != userAuthTokenEntity.getUserId().getId()) {
            throw new AuthorizationFailedException("ATHR-003","Only the answer owner can edit the answer");
        }
        answer.setAns(content);
        answerDao.saveAnswer(answer);
        return answerDao.getAnswerByUuid(answerUuid);
    }

    public Answer deleteAnswer(String answerUuid, String authorizationToken) throws AuthorizationFailedException, AnswerNotFoundException {

        UserAuth userAuthTokenEntity = userDao.getUserAuthToken(authorizationToken);

        /*  If user is not logged IN */
        if(userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }

        /*  User is logged out  */
        if(userAuthTokenEntity.getLogoutAt() != null){
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to post a question");
        }

        /*  Only the answer owner or admin can delete the answer    */

        Answer answer = answerDao.getAnswerByUuid(answerUuid);
        if(answer == null){
            throw new AnswerNotFoundException("ANS-001","Entered answer uuid does not exist");
        }

        if((answer.getUser().getId() == userAuthTokenEntity.getUserId().getId()) || (userAuthTokenEntity.getUserId().getRole().compareTo("nonadmin") != 0 ) ) {
            answerDao.saveAnswer(answer);
            return answerDao.getAnswerByUuid(answerUuid);
        }
        throw new AuthorizationFailedException("ATHR-003","Only the answer owner or admin can delete the answer");
    }

    public List<Answer> getAllAnswersToQuestion(String questionUuid, String authorizationToken) throws AuthorizationFailedException, InvalidQuestionException {
        UserAuth userAuthTokenEntity = userDao.getUserAuthToken(authorizationToken);

        /*  If user is not logged IN */
        if(userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }

        /*  User is logged out  */
        if(userAuthTokenEntity.getLogoutAt() != null){
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to post a question");
        }

        Question question = questionDao.getQuestionByUuidId(questionUuid);

        if(question == null){
            throw new InvalidQuestionException("QUES-001","The question with entered uuid whose details are to be seen does not exist");
        }
        return answerDao.getAllAnswerToQuestion(question);
    }
}
