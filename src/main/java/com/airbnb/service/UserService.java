package com.airbnb.service;

import com.airbnb.entity.PropertyUser;
import com.airbnb.payload.LoginDto;
import com.airbnb.payload.PropertyUserDto;
import com.airbnb.repository.PropertyUserRepository;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private PropertyUserRepository userRepository;
    private JWTService jwtService;




    public UserService(PropertyUserRepository userRepository, JWTService jwtService) {

        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    public PropertyUser addUser(PropertyUserDto  propertyUserDto){
        PropertyUser user= new PropertyUser();
        user.setFirstName(propertyUserDto.getFirstName());
        user.setLastName(propertyUserDto.getLastName());
        user.setEmail(propertyUserDto.getEmail());
        user.setUserName(propertyUserDto.getUserName());
        user.setUserRole("ROLE_USER");
        user.setPassword(BCrypt.hashpw(propertyUserDto.getPassword(),BCrypt.gensalt(10)));
        PropertyUser savedUser= userRepository.save(user);
        return savedUser;
    }


    public String verifyLogin(LoginDto loginDto) {
        Optional<PropertyUser> OpUser = userRepository.findByUserName(loginDto.getUserName());
        if(OpUser.isPresent()){
            PropertyUser propertyUser =OpUser.get();
           if(BCrypt.checkpw(loginDto.getPassword(),propertyUser.getPassword())){
               return jwtService.generateToken(propertyUser);
           }
        }
        return null;
    }
}
