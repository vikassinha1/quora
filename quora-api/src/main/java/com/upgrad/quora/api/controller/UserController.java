package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.SigninResponse;
import com.upgrad.quora.api.model.SignoutResponse;
import com.upgrad.quora.api.model.SignupUserRequest;
import com.upgrad.quora.api.model.SignupUserResponse;
import com.upgrad.quora.service.business.UserBusinessService;
import com.upgrad.quora.service.entity.UserAuth;
import com.upgrad.quora.service.entity.User;
import com.upgrad.quora.service.business.UserAdminBusinessService;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserBusinessService userBusinessService;

  /*
   * Sign in method called for the endpoint "/user/signin"
   * */

    @RequestMapping(method = RequestMethod.POST, path = "signin", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SigninResponse> signin(@RequestHeader("authorization") final String authorization) throws AuthenticationFailedException {
        byte[] decoded = Base64.getDecoder().decode(authorization);
        String decodedText = new String(decoded);
        String[] decodedArray = decodedText.split(":");

        if(decodedArray.length < 2){
            throw new AuthenticationFailedException("ATH-001","This username does not exist");
        }

        UserAuth authEntity = userBusinessService.signin(decodedArray[0], decodedArray[1]);

        SigninResponse signinResponse = new SigninResponse();

        signinResponse.setId(authEntity.getUserId().getUuid());
        signinResponse.setMessage("SIGNED IN SUCCESSFULLY");

        HttpHeaders headers = new HttpHeaders();

        headers.add("access_token", authEntity.getAccessToken());


        return new ResponseEntity<SigninResponse>(signinResponse, headers, HttpStatus.OK );
    }


  /*
  * Sign up method called for the endpoint "/user/signup"
  * */

    @RequestMapping(method = RequestMethod.POST, path = "signup", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupUserResponse> signup(final SignupUserRequest signupUserRequest) throws SignUpRestrictedException {


        final User User = new User();

        User.setUuid(UUID.randomUUID().toString());
        User.setFirstName(signupUserRequest.getFirstName());
        User.setLastName(signupUserRequest.getLastName());
        User.setUserName(signupUserRequest.getUserName());
        User.setEmail(signupUserRequest.getEmailAddress());
        User.setPassword(signupUserRequest.getPassword());
        User.setCountry(signupUserRequest.getCountry());
        User.setAboutMe(signupUserRequest.getAboutMe());
        User.setDob(signupUserRequest.getDob());
        User.setRole("nonadmin");
        User.setContactNumber(signupUserRequest.getContactNumber());

        final User createdUser = userBusinessService.signup(User);

        SignupUserResponse userResponse = new SignupUserResponse().id(createdUser.getUuid()).status("USER SUCCESSFULLY REGISTERED");

        return new ResponseEntity<SignupUserResponse>(userResponse, HttpStatus.CREATED);

    }



  /*
   * Sign out method called for the endpoint "/user/signout"
   * */

    @RequestMapping(method = RequestMethod.POST, path = "signout")
    public ResponseEntity<SignoutResponse> signout(@RequestHeader("authorization") final String accessToken) throws SignOutRestrictedException {

        String signedOutUser = userBusinessService.signout(accessToken);
        SignoutResponse signoutResponse = new SignoutResponse().id(signedOutUser).message("SIGNED OUT SUCCESSFULLY");

        return new ResponseEntity<SignoutResponse>(signoutResponse, HttpStatus.OK);
    }
}
