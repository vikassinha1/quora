package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.User;
import com.upgrad.quora.service.entity.UserAuth;
import com.upgrad.quora.service.entity.UserAuth;
import com.upgrad.quora.service.entity.User;
import com.upgrad.quora.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.UUID;

@Service
public class UserBusinessService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;

    public User getUserById(final String userUuid) throws UserNotFoundException {
        User User = userDao.getUser(userUuid);

        if(User == null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid does not exist");
        }

        return User;
    }



    public UserAuth getUserByToken(final String accessToken) throws AuthorizationFailedException {
        UserAuth userAuthByToken = userDao.getUserAuthToken(accessToken);

        if(userAuthByToken == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        if(userAuthByToken.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get user details");
        }

        return userAuthByToken;
    }


    public User getUserProfile(final String userUuid, final String accessToken) throws AuthorizationFailedException, UserNotFoundException {

        getUserByToken(accessToken);
        User userById = getUserById(userUuid);

        return userById;
    }


    @Transactional
    public User signup(User User) throws SignUpRestrictedException {

        if(userDao.isEmailExists(User.getUserName())) {
            throw new SignUpRestrictedException("SGR-001", "Try any other Username, this Username has already been taken");
        }

        if(userDao.isEmailExists(User.getEmail())) {
            throw new SignUpRestrictedException("SGR-002", "This user has already been registered, try with any other emailId");
        }

        String[] encryptedText = cryptographyProvider.encrypt(User.getPassword());
        User.setSalt(encryptedText[0]);
        User.setPassword(encryptedText[1]);

        User signUpUser = userDao.createUser(User);

        return signUpUser;
    }

    @Transactional
    public UserAuth signin(final String username, final String password) throws AuthenticationFailedException {
        User User = userDao.getUserByUsername(username);

        if(User == null) {
            throw new AuthenticationFailedException("ATH-001", "This username does not exist");
        }

        final String encryptedPassword = cryptographyProvider.encrypt(password, User.getSalt());
        if(encryptedPassword.equals(User.getPassword())) {
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
            UserAuth UserAuth = new UserAuth();

            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime expiresAt = now.plusHours(8);

            UserAuth.setUuid(UUID.randomUUID().toString());
            UserAuth.setUserId(User);
            UserAuth.setAccessToken(jwtTokenProvider.generateToken(User.getUuid(), now, expiresAt));
            UserAuth.setExpiresAt(expiresAt);
            UserAuth.setLoginAt(now);

            userDao.createAuthToken(UserAuth);

            return UserAuth;
        } else {
            throw new AuthenticationFailedException("ATH-002", "Password failed");
        }

    }

    @Transactional
    public String signout(final String accessToken) throws SignOutRestrictedException {
        ZonedDateTime currentTime = ZonedDateTime.now();
        UserAuth UserAuth = userDao.getUserAuthToken(accessToken);

        if(UserAuth == null) {
            throw new SignOutRestrictedException("SGR-001", "User is not Signed in");
        }

        userDao.updateUserLogoutByToken(accessToken, currentTime);

        return UserAuth.getUserId().getUuid();
    }


    public boolean isUserSignedIn(UserAuth userAuthTokenEntity) {
        boolean isUserSignedIn = false;
        if (userAuthTokenEntity != null && userAuthTokenEntity.getLoginAt() != null && userAuthTokenEntity.getExpiresAt() != null) {
            if ((userAuthTokenEntity.getLogoutAt() == null)) {
                isUserSignedIn = true;
            }
        }
        return isUserSignedIn;
    }


    public boolean isUserAdmin(User user) {
        boolean isUserAdmin = false;
        if (user != null && "admin".equals(user.getRole())) {
            isUserAdmin = true;
        }
        return isUserAdmin;
    }
}
