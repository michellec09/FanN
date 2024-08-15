package edu.tec.azuay.faan.controller.user;

import edu.tec.azuay.faan.persistence.dto.email.ResetPasswordRequest;
import edu.tec.azuay.faan.persistence.dto.secondary.SavedUser;
import edu.tec.azuay.faan.service.interfaces.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @PreAuthorize("permitAll()")
    @GetMapping("/exists-username/{username}")
    public ResponseEntity<Boolean> existsUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.existsByUsernameIgnoreCase(username));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/{username}")
    public ResponseEntity<?> getUser(@PathVariable String username) {
        return ResponseEntity.ok(userService.findOneByUsername(username));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/get/{id}")
    public ResponseEntity<?> getUserById(@PathVariable String id) {
        return ResponseEntity.ok(userService.findOneById(id));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/get-author/{username}")
    public ResponseEntity<?> getAuthor(@PathVariable String username) {
        return ResponseEntity.ok(userService.getAuthorByUsername(username));
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/exists-email/{email}")
    public ResponseEntity<Boolean> existsEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.existsByEmailIgnoreCase(email));
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/exists-phone/{phone}")
    public ResponseEntity<Boolean> existsPhone(@PathVariable String phone) {
        return ResponseEntity.ok(userService.existsByPhone(phone));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PutMapping(value = "/{username}", consumes = {"multipart/form-data"})
    public ResponseEntity<?> updateUserPhoto(@PathVariable String username, @RequestParam MultipartFile file) throws IOException {
        return ResponseEntity.ok(userService.updateUserReference(username, file));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUserData(@PathVariable String id, @RequestBody SavedUser user) {
        return ResponseEntity.ok(userService.updateUserData(id, user));
    }

    @PreAuthorize("permitAll()")
    @PutMapping("/reset-password/{userId}")
    public ResponseEntity<?> resetPassword(@PathVariable String userId, @RequestBody ResetPasswordRequest resetPasswordRequest) {
        return ResponseEntity.ok(userService.updatePassword(userId, resetPasswordRequest));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PutMapping("/profile-update-password/{userId}")
    public ResponseEntity<?> profileResetPassword(@PathVariable String userId, @RequestBody ResetPasswordRequest resetPasswordRequest) {
        return ResponseEntity.ok(userService.updatePassword(userId, resetPasswordRequest));
    }
}
