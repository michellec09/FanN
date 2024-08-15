package edu.tec.azuay.faan.controller.auth;

import edu.tec.azuay.faan.persistence.dto.auth.AuthenticationRequest;
import edu.tec.azuay.faan.persistence.dto.auth.AuthenticationResponse;
import edu.tec.azuay.faan.persistence.dto.primary.RegisteredUser;
import edu.tec.azuay.faan.persistence.dto.primary.SaveUser;
import edu.tec.azuay.faan.persistence.dto.secondary.TokenRefreshRequest;
import edu.tec.azuay.faan.persistence.entity.User;
import edu.tec.azuay.faan.service.auth.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping ("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    @PreAuthorize("permitAll()")
    @GetMapping("/validate")
    public ResponseEntity<Boolean> validate(@RequestParam String jwt){
        boolean isTokenValid = authenticationService.validate(jwt);

        return ResponseEntity.ok(isTokenValid);
    }

    @PreAuthorize("permitAll()")
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest requestAuthentication){
        AuthenticationResponse rsp = authenticationService.login(requestAuthentication);

        return ResponseEntity.ok(rsp);
    }

    @PreAuthorize("permitAll()")
    @PostMapping(value = "/register", consumes = {"multipart/form-data"})
    public ResponseEntity<RegisteredUser> register(@RequestPart("newUser") SaveUser newUser, @RequestPart("image") MultipartFile file) throws IOException {
        RegisteredUser user = authenticationService.registerUser(newUser, file);

        return ResponseEntity.ok(user);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/profile")
    public ResponseEntity<User> findMyProfile(){
        User user = authenticationService.findLoggedInUser();

        return ResponseEntity.ok(user);
    }

    @PreAuthorize("permitAll()")
    @PostMapping("/refresh-token")
    public ResponseEntity<AuthenticationResponse> refreshToken(@RequestBody TokenRefreshRequest refreshToken){
        AuthenticationResponse rsp = authenticationService.refreshToken(refreshToken);

        return ResponseEntity.ok(rsp);
    }

}
