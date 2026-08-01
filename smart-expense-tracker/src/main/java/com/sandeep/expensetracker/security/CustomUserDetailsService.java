package com.sandeep.expensetracker.security;

import com.sandeep.expensetracker.entity.User;
import com.sandeep.expensetracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/** Loads user-specific data for Spring Security during authentication. */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email != null ? email.toLowerCase().trim() : null)
                .orElseThrow(() -> new UsernameNotFoundException("No user found with email: " + email));
        return new CustomUserDetails(user);
    }
}
