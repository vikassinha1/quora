package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.QuestionResponse;
import com.upgrad.quora.api.model.SignupUserResponse;
import com.upgrad.quora.service.business.UserBusinessService;
import com.upgrad.quora.service.entity.Question;
import com.upgrad.quora.service.entity.User;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class UserController {

    @Autowired
    UserBusinessService userBusinessService;

    @RequestMapping(method = RequestMethod.POST, path = "/user/signup", consumes= MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupUserResponse> signup(@RequestParam("firstName") final String firstName,
                                                     @RequestParam("lastName") final String lastName,
                                                     @RequestParam("userName") final String userName,
                                                     @RequestParam("emailAddress") final String emailAddress,
                                                     @RequestParam("password") final String password,
                                                     @RequestParam("country") final String country,
                                                     @RequestParam("aboutMe") final String aboutMe,
                                                     @RequestParam("dob") final String dob,
                                                     @RequestParam("contactNumber") final String contactNumber)
            throws SignUpRestrictedException {

        User newUser = new User();
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setUserName(userName);
        newUser.setEmail(emailAddress);
        newUser.setPassword(password);
        newUser.setCountry(country);
        newUser.setAboutMe(aboutMe);
        newUser.setDob(dob);
        newUser.setContactNumber(contactNumber);

        User registeredUser = userBusinessService.signup(newUser);
        SignupUserResponse signupUserResponse = new SignupUserResponse().id(registeredUser.getUuid()).status("USER SUCCESSFULLY REGISTERED");
        return new ResponseEntity<>(signupUserResponse,HttpStatus.OK);
    }
}
