package com.alienCoders.moneymanger.service;

import com.alienCoders.moneymanger.entity.ProfileEntity;
import com.alienCoders.moneymanger.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Collections;

@Service // Marks this class as a Spring service
@RequiredArgsConstructor // Automatically creates constructor for final fields
public class AppUserDetailsService implements UserDetailsService {

    private final ProfileRepository profileRepository; // Repository to fetch user data from DB

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Find user by email in database
        ProfileEntity existingProfile = profileRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Profile not found with email: " + email)
                ); // Throw exception if user not found

        // Create a UserDetails object for Spring Security
        return User.builder()
                .username(existingProfile.getEmail()) // Set email as username
                .password(existingProfile.getPassword()) // Set password (hashed)
                .authorities(Collections.emptyList()) // No roles/permissions for now
                .build(); // Build and return the UserDetails object
    }
}
