package org.example.security.config;

import lombok.AllArgsConstructor;
import org.example.data.model.User;
import org.example.data.repositories.UserRepository;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findUserByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email" + username));

        if(!user.isEnabled()){
            throw new DisabledException("User is disabled");
        }

        return UserPrincipal.create(user);
    }
}
