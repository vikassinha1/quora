package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.QuestionDetailsResponse;
import com.upgrad.quora.api.model.QuestionResponse;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.entity.Question;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/")
public class QuestionController {
    @Autowired
    QuestionBusinessService questionBusinessService;

    @RequestMapping(method = RequestMethod.POST, path = "/question/create", consumes= MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(@RequestHeader("authorization") final String authorization,
                                                           @RequestParam("content") final String content)
            throws AuthorizationFailedException {

        Question question = questionBusinessService.createQuestion(content,authorization);
        QuestionResponse questionResponse = new QuestionResponse().id(question.getUuid()).status("QUESTION CREATED");
        return new ResponseEntity<>(questionResponse,HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/question/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse > > getAllQuestions(@RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException {

        List<Question> questionList = questionBusinessService.getAllQuestions(authorization);
        List<QuestionDetailsResponse> questionDetailsResponseList = new ArrayList<QuestionDetailsResponse>();
        for (Question question: questionList) {
            questionDetailsResponseList.add(new QuestionDetailsResponse().content(question.getContent()).id(question.getUuid()));
        }
        return new ResponseEntity<List<QuestionDetailsResponse>>(questionDetailsResponseList,HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/question/edit/{questionId}", consumes= MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> editQuestion(@RequestHeader("authorization") final String authorization,
                                                           @PathVariable("questionId") final String questionUuid,
                                                         @RequestParam("content") final String content)
            throws AuthorizationFailedException, InvalidQuestionException {

        Question question = questionBusinessService.editQuestion(questionUuid,content,authorization);
        QuestionResponse questionResponse = new QuestionResponse().id(question.getUuid()).status("QUESTION EDITED");
        return new ResponseEntity<>(questionResponse,HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/question/delete/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> deleteQuestion(@RequestHeader("authorization") final String authorization,
                                                         @PathVariable("questionId") final String questionUuid)
            throws AuthorizationFailedException, InvalidQuestionException {

        Question question = questionBusinessService.deleteQuestion(questionUuid,authorization);
        QuestionResponse questionResponse = new QuestionResponse().id(question.getUuid()).status("QUESTION DELETED");
        return new ResponseEntity<>(questionResponse,HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/question/all/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse > >  getAllQuestionsByUser(@RequestHeader("authorization") final String authorization,
                                                           @PathVariable("userId") final String userUuid)
            throws AuthorizationFailedException, UserNotFoundException {

        List<Question> questionList = questionBusinessService.getAlQuestionsByUser(userUuid,authorization);

        List<QuestionDetailsResponse> questionDetailsResponseList = new ArrayList<QuestionDetailsResponse>();
        for (Question question: questionList) {
            questionDetailsResponseList.add(new QuestionDetailsResponse().content(question.getContent()).id(question.getUuid()));
        }
        return new ResponseEntity<List<QuestionDetailsResponse>>(questionDetailsResponseList,HttpStatus.OK);
    }
}
