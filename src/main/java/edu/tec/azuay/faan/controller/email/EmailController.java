package edu.tec.azuay.faan.controller.email;

import edu.tec.azuay.faan.exceptions.ObjectNotFoundException;
import edu.tec.azuay.faan.persistence.dto.email.MailRequest;
import edu.tec.azuay.faan.persistence.dto.email.MailResponse;
import edu.tec.azuay.faan.persistence.entity.User;
import edu.tec.azuay.faan.service.interfaces.IEmailService;
import edu.tec.azuay.faan.service.interfaces.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
public class EmailController {

    private final IEmailService emailService;

    private final IUserService userService;

    @Value("${spring.mail.username}")
    private String from;

    @PreAuthorize("permitAll()")
    @PostMapping("/send-email-reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody MailRequest mailRequest) {
        User user = userService.findByEmailIgnoreCase(mailRequest.getTo()).orElseThrow(
                () -> new ObjectNotFoundException("User with email: " + mailRequest.getFrom() + " not found")
        );
        return createResponse(user, mailRequest);
    }

    @PreAuthorize("permitAll()")
    @PostMapping("/validate-token/{token}")
    public ResponseEntity<?> validateToken(@PathVariable String token) {

        return ResponseEntity.ok(emailService.validateToken(token));
    }

    private String getCompleteName(User user) {
        return user.getName() + " " + user.getLastname();
    }

    private ResponseEntity<?> createResponse(User user, MailRequest mailRequest) {
        emailService.validateRequest(user);

        mailRequest.setFrom(from);
        mailRequest.setSubject("Reset Password");

        Map<String, Object> model = new HashMap<>();

        model.put("username", getCompleteName(user));

        Map<String, Object> model1 = new HashMap<>();
        model1.put("userId", user.getId());


        MailResponse mailResponse = emailService.sendEmail(mailRequest, model);

        if (mailResponse.getMessage().contains("Error trying to send the email")){
            return ResponseEntity.internalServerError().body(mailResponse);
        }

        return ResponseEntity.ok().body(model1);
    }
}
