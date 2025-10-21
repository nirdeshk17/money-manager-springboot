package com.alienCoders.moneymanger.controller;

import com.alienCoders.moneymanger.dto.AuthDTO;
import com.alienCoders.moneymanger.dto.ProfileDTO;
import com.alienCoders.moneymanger.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor

public class ProfileController {
    private final ProfileService profileService;

    @PostMapping("/register")
    public ResponseEntity<?> registerProfile(ProfileDTO profileDTO){

        if(profileService.isEmailExist(profileDTO.getEmail())){
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message","Email already exist"));
       }
        ProfileDTO registerProfile=profileService.registerProfile(profileDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(registerProfile);
    }

    @GetMapping("/activate")
    public ResponseEntity<String> activateProfile(@RequestParam String token){
   boolean isActivated=profileService.activateProfile(token);
   if(isActivated){
       return ResponseEntity.ok("Profile activated successfully");
   }
   else{
       return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Activation token not found or alreadt used");
   }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String,Object>> login(@RequestParam String email,@RequestParam String password){
        try {
            AuthDTO authDTO=new AuthDTO(email,password,null);
            if(!profileService.isEmailExist(authDTO.getEmail())){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "Message","Account not found"
                ));
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
}
