package com.alienCoders.moneymanger.service;


import com.alienCoders.moneymanger.dto.AuthDTO;
import com.alienCoders.moneymanger.dto.ProfileDTO;
import com.alienCoders.moneymanger.entity.ProfileEntity;
import com.alienCoders.moneymanger.repository.ProfileRepository;
import com.alienCoders.moneymanger.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service // Marks this as a Spring service component (business logic layer)
@RequiredArgsConstructor // Generates a constructor for all final fields (dependency injection)
public class ProfileService {

    @Value("${app.activation.url}")
    private String activationBaseUrl;
    // Dependency on the repository (automatically injected by Spring)
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    /**
     * Registers a new profile:
     * 1. Converts DTO → Entity
     * 2. Generates a unique activation token
     * 3. Saves the entity into the database
     * 4. Converts the saved entity back to DTO for the response
     */
    public ProfileDTO registerProfile(ProfileDTO profileDTO) {
       if(isEmailExist(profileDTO.getEmail())){
           throw new RuntimeException("Email already exist");
       }
        // Convert incoming DTO object into an Entity object
        ProfileEntity newProfile = toEntity(profileDTO);
        System.out.println("Full Name"+newProfile.getFullName());
        System.out.println("Password"+newProfile.getPassword());
        // Generate a unique activation token (used for email verification, etc.)
        newProfile.setActivationToken(UUID.randomUUID().toString());
        // Encode password
        newProfile.setPassword(passwordEncoder.encode(newProfile.getPassword()));
        // Save the entity into the database and get the persisted object back
        newProfile = profileRepository.save(newProfile);

        String activationLink = activationBaseUrl+"/api/v1.0/activate?token=" + newProfile.getActivationToken();
        String subject = "Activate your Money Manager account";
        String body = "Click on the following link to activate your account: " + activationLink;
        //Send activation url via email
        emailService.sendEmail(newProfile.getEmail(), subject, body);
        // Convert the saved entity back into DTO (to return only safe data to client)
        return toDTO(newProfile);
    }

    /**
     * Converts ProfileDTO → ProfileEntity
     * Used when saving data into the database
     */
    public ProfileEntity toEntity(ProfileDTO profileDTO) {
        return ProfileEntity.builder().id(profileDTO.getId()).fullName(profileDTO.getFullName()).email(profileDTO.getEmail()).password(profileDTO.getPassword()).profileImage(profileDTO.getProfileImage()).createdAt(profileDTO.getCreatedAt()).updatedAt(profileDTO.getUpdatedAt()).build();
    }

    /**
     * Converts ProfileEntity → ProfileDTO
     * Used when sending data back to the client (response layer).
     * Avoids exposing sensitive information (e.g., password).
     */
    public ProfileDTO toDTO(ProfileEntity profileEntity) {
        return ProfileDTO.builder().id(profileEntity.getId()).fullName(profileEntity.getFullName()).email(profileEntity.getEmail()).profileImage(profileEntity.getProfileImage()).createdAt(profileEntity.getCreatedAt()).updatedAt(profileEntity.getUpdatedAt()).build();
    }

    // Activate a profile using the activation token
    public boolean activateProfile(String activationToken) {
        // Find the profile with the given activation token
        return profileRepository.findByActivationToken(activationToken).map(profile -> {
            // If found, set profile as active
            profile.setIsActive(true);
            // Save updated profile back to DB
            profileRepository.save(profile);
            // Return true since activation was successful
            return true;
        }).orElse(false); // If not found, return false
    }

    // Check if an account is active using email
    public boolean isAccountActive(String email) {
        // Find profile by email, return its "isActive" status if present.
        // If profile not found, return false
        return profileRepository.findByEmail(email)
                .map(ProfileEntity::getIsActive)
                .orElse(false);
    }

    public boolean isEmailExist(String email){
        return profileRepository.findByEmail(email).isPresent();
    }

    public boolean isPasswordCorrect(String email,String password){
        return profileRepository.findByEmail(email)
                .map(profile->passwordEncoder.matches(password,profile.getPassword())).orElse(false);
    }
    // Get the profile of the currently logged-in user
    public ProfileEntity getCurrentProfile() {
        // Get the authentication object of the currently logged-in user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Extract the username (in this case, email) from authentication
        String email = authentication.getName();

        // Find the profile by email and return it
        // If not found, throw error
        return profileRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Profile not found with email: " + email));
    }
    // Function to get a user's public profile details
    public ProfileDTO getPublicProfile(String email){
        ProfileEntity currentUser = null;

        // If no email is passed, get the currently logged-in user's profile
        if(email == null){
            currentUser = getCurrentProfile();
        }
        else {
            // Otherwise, fetch the profile by the given email
            // If not found, throw an error
            currentUser = profileRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Profile not found with email: " + email));
            System.out.println(currentUser);
        }

        // Convert ProfileEntity to ProfileDTO and return (only safe/public fields)
        return ProfileDTO.builder()
                .id(currentUser.getId())
                .fullName(currentUser.getFullName())
                .email(currentUser.getEmail())
                .profileImage(currentUser.getProfileImage())
                .createdAt(currentUser.getCreatedAt())
                .updatedAt(currentUser.getUpdatedAt())
                .build();
    }



    // Function to authenticate a user and generate JWT token
    public Map<String, Object> authenticateAndGenerateToken(AuthDTO authDTO){
        try {

            // 1. Authenticate user using email + password
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authDTO.getEmail(),
                            authDTO.getPassword()
                    )
            );

           //Generate jwt token
            String token=jwtUtil.generateToken(authDTO.getEmail());
            // 2. If authentication succeeds, generate JWT token
            // 3. Return both the token and user's public profile as response
            return Map.of(
                    "token", token,                 // Placeholder for the actual JWT
                    "user", getPublicProfile(authDTO.getEmail()) // Get user's profile info
            );
        }
        catch (Exception e){
            // If authentication fails, throw error
            throw new RuntimeException(e);
        }
    }



}
