package com.mealchak.mealchakserverapplication.oauth2;

import com.mealchak.mealchakserverapplication.model.User;
import com.mealchak.mealchakserverapplication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    //이름은 loadByUsername이지만 현재 카카오방식에서는 username이 중복될수있는 상태이기에 로직을 email로 돌려두었음
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email, User.class)
                .orElseThrow(() -> new UsernameNotFoundException("Can't find " + email));

        return new UserDetailsImpl(user);
    }
}
