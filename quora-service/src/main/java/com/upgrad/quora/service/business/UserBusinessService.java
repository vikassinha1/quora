package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.User;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserBusinessService {

    @Autowired
    UserDao userDao;
    /*
    * Method will check if userName exist, if yes throw exception.
    * */
    public User signup(User newUser) throws SignUpRestrictedException {
        User existing = userDao.getUserByUserName(newUser.getUserName());
        if(existing == null){
            existing = userDao.getUserByEmail(newUser.getEmail());
            if(existing == null){
                PasswordCryptographyProvider encPass = new PasswordCryptographyProvider();
                String encPassword[] = encPass.encrypt(newUser.getPassword());
                newUser.setSalt(encPassword[0]);
                newUser.setPassword(encPassword[1]);
                newUser.setUuid(UUID.randomUUID().toString());
                newUser.setRole("nonadmin");
                userDao.createUser(newUser);
            } else {
                throw new SignUpRestrictedException("SRG-002","This user has already been registered, try with any other emailId");
            }
        } else {
            throw new SignUpRestrictedException("SRG-001","Try any other Username, this Username has already been taken");
        }
        return newUser;
    }
}
