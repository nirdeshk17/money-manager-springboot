package com.alienCoders.moneymanger.controller;

import com.alienCoders.moneymanger.dto.AuthDTO;
import com.alienCoders.moneymanger.dto.ProfileDTO;
import com.alienCoders.moneymanger.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor

public class ProfileController {
    private final ProfileService profileService;

    @PostMapping("/register")
    public ResponseEntity<?> registerProfile(@RequestBody ProfileDTO profileDTO){
        System.out.println(profileDTO);
        if(profileService.isEmailExist(profileDTO.getEmail())){
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message","Email already exist"));
       }
        ProfileDTO registerProfile=profileService.registerProfile(profileDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(registerProfile);
    }

    @GetMapping("/activate")
    public ResponseEntity<String> activateProfile(@RequestBody String token){
   boolean isActivated=profileService.activateProfile(token);
   if(isActivated){
       return ResponseEntity.ok("Profile activated successfully");
   }
   else{
       return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Activation token not found or alreadt used");
   }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String,Object>> login(@RequestBody AuthDTO authDTO){
        try {
            if(!profileService.isEmailExist(authDTO.getEmail())){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "message","Email not found"
                ));
            }
            if(!profileService.isPasswordCorrect(authDTO.getEmail(),authDTO.getPassword())){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        Map.of("message","Incorrect password")
                );
            }
            if(!profileService.isAccountActive(authDTO.getEmail())){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                        "message","Account is not active. Please activate your account first"
                ));
            }
            Map<String, Object> response=profileService.authenticateAndGenerateToken(authDTO);
            return ResponseEntity.ok(response);
//            profileService
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/test")
    public String test(){
        return "Testing";
    }

    @GetMapping("/profile")
    public ResponseEntity<ProfileDTO> getPublicProfile(){
        ProfileDTO profileDTO=profileService.getPublicProfile(null);
        return ResponseEntity.ok(profileDTO);
    }
}
