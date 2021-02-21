package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.AnswerDeleteResponse;
import com.upgrad.quora.api.model.AnswerDetailsResponse;
import com.upgrad.quora.api.model.AnswerResponse;
import com.upgrad.quora.service.business.AnswerBusinessService;
import com.upgrad.quora.service.entity.Answer;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/")
public class AnswerController {

    @Autowired
    AnswerBusinessService answerBusinessService;

    @RequestMapping(method = RequestMethod.POST, path = "/question/{questionId}/answer/create", consumes= MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> createAnswer(@RequestHeader("authorization") final String authorization,
                                                           @PathVariable("questionId") final String questionUuid,
                                                           @RequestParam("answer") final String content)
            throws AuthorizationFailedException, InvalidQuestionException {
        Answer answer = answerBusinessService.createAnswer(content,questionUuid,authorization);
        AnswerResponse answerResponse = new AnswerResponse().id(answer.getUuid()).status("ANSWER CREATED");
        return new ResponseEntity<>(answerResponse,HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/answer/edit/{answerId}", consumes= MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> editAnswer(@RequestHeader("authorization") final String authorization,
                                                         @PathVariable("answerId") final String answerUuid,
                                                         @RequestParam("content") final String content)
            throws AuthorizationFailedException, AnswerNotFoundException {

        Answer answer = answerBusinessService.editAnswerContent(content,answerUuid,authorization);
        AnswerResponse answerResponse = new AnswerResponse().id(answer.getUuid()).status("ANSWER EDITED");
        return new ResponseEntity<>(answerResponse,HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/answer/delete/{answerId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerDeleteResponse> deleteAnswer(@RequestHeader("authorization") final String authorization,
                                                             @PathVariable("answerId") final String answerUuid)
            throws AuthorizationFailedException, AnswerNotFoundException {

        Answer answer = answerBusinessService.deleteAnswer(answerUuid,authorization);
        AnswerDeleteResponse answerDeleteResponse = new AnswerDeleteResponse().id(answer.getUuid()).status("ANSWER DELETED");
        return new ResponseEntity<>(answerDeleteResponse,HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/answer/all/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<AnswerDetailsResponse >>  getAllAnswersToQuestion(@RequestHeader("authorization") final String authorization,
                                                                                 @PathVariable("questionId") final String questionUuid)
            throws AuthorizationFailedException, InvalidQuestionException {

        List<Answer> answerList = answerBusinessService.getAllAnswersToQuestion(questionUuid,authorization);

        List<AnswerDetailsResponse> answerDetailsResponses = new ArrayList<AnswerDetailsResponse>();
        for (Answer answer: answerList) {
            answerDetailsResponses.add(new AnswerDetailsResponse().answerContent(answer.getAns()).id(answer.getUuid()));
        }
        return new ResponseEntity<List<AnswerDetailsResponse>>(answerDetailsResponses,HttpStatus.OK);
    }

}
