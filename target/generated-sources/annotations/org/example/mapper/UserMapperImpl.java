package org.example.mapper;

import javax.annotation.processing.Generated;
import org.example.data.model.User;
import org.example.dto.request.SignUpRequest;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-02-14T13:08:16+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.14 (Amazon.com Inc.)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public User signUpRequestToUser(SignUpRequest signUpRequest) {
        if ( signUpRequest == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.firstName( signUpRequest.getFirstName() );
        user.lastName( signUpRequest.getLastName() );
        user.email( signUpRequest.getEmail() );
        user.password( signUpRequest.getPassword() );
        user.accountType( signUpRequest.getAccountType() );

        return user.build();
    }
}
