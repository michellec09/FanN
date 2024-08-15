package edu.tec.azuay.faan.service.implementation;

import edu.tec.azuay.faan.exceptions.ObjectNotFoundException;
import edu.tec.azuay.faan.exceptions.RecoveryTokenAlreadyExistsException;
import edu.tec.azuay.faan.persistence.dto.email.MailRequest;
import edu.tec.azuay.faan.persistence.dto.email.MailResponse;
import edu.tec.azuay.faan.persistence.entity.ResetToken;
import edu.tec.azuay.faan.persistence.entity.User;
import edu.tec.azuay.faan.service.interfaces.IEmailService;
import edu.tec.azuay.faan.service.interfaces.IRecoveryTokenService;
import edu.tec.azuay.faan.service.interfaces.IUserService;
import edu.tec.azuay.faan.service.secondary.ResetPasswordService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailServiceImp implements IEmailService {

    private final JavaMailSender javaMailSender;

    private final ITemplateEngine templateEngine;

    private final IRecoveryTokenService recoveryTokenService;

    private final ResetPasswordService tokenPasswordRecovery;

    private final IUserService userService;

    @Override
    public MailResponse sendEmail(MailRequest mailRequest, Map<String, Object> model) {

        MailResponse mailResponse = new MailResponse();
        MimeMessage message = javaMailSender.createMimeMessage();
        ResetToken resetToken = generateResetToken(mailRequest.getTo());

        try{
            model.put("token", resetToken.getToken());

            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED,
                    StandardCharsets.UTF_8.name());

            getHTMLAndSend(mailRequest, model, mailResponse, message, helper);

        } catch(MessagingException | IOException e) {
            mailResponse.setMessage("Error trying to send the email: " + e.getMessage());
            mailResponse.setStatus(Boolean.FALSE);
        }

        return mailResponse;
    }

    @Override
    public void validateRequest(User user) {
        List<ResetToken> resetTokens = recoveryTokenService.findByUserId(user.getId());

        resetTokens.stream().filter(ResetToken::getIsActive)
                .filter(token -> token.getExpirationDate().compareTo(new Date()) > 0)
                .findAny()
                .ifPresent(token -> {
                    throw new RecoveryTokenAlreadyExistsException("The user already has an active token");
                });
    }

    @Override
    public Map<String, Object> validateToken(String token) {
        ResetToken resetToken = recoveryTokenService.findByToken(token)
                .orElseThrow(() -> new ObjectNotFoundException("Token " + token + " not found"));

        Map<String, Object> response = new HashMap<>();

        if (!resetToken.getIsActive()) {
            response.put("active", Boolean.FALSE);
            response.put("message", "Token is inactive");
            return response;
        }

        if (resetToken.getExpirationDate().compareTo(new Date()) < 0) {
            deactivateTokenAndSave(resetToken);
            response.put("active", Boolean.FALSE);
            response.put("message", "Token has expired");
            return response;
        }

        deactivateTokenAndSave(resetToken);

        response.put("active", Boolean.TRUE);
        response.put("message", "Token is valid");
        return response;
    }

    private void deactivateTokenAndSave(ResetToken token) {
        token.setIsActive(Boolean.FALSE);
        recoveryTokenService.save(token);
    }

    private ResetToken generateResetToken(String email) {
        String tokenValue = tokenPasswordRecovery.generateToken();
        ResetToken recoveryToken = new ResetToken();
        User user = userService.findByUsernameOrEmail(email, email).orElseThrow(() -> new ObjectNotFoundException("User not found"));
        Date expirationDate = tokenPasswordRecovery.calculateExpirationDate();

        if (user != null && !user.getUsername().isEmpty()) {
            recoveryToken.setUser(user);
        }

        recoveryToken.setToken(tokenValue);
        recoveryToken.setExpirationDate(expirationDate);

        return recoveryTokenService.save(recoveryToken);
    }

    private void getHTMLAndSend(MailRequest mailRequest, Map<String, Object> model, MailResponse mailResponse,
                         MimeMessage mimeMessage, MimeMessageHelper helper) throws  IOException, MessagingException {

        Context context = new Context();
        context.setVariables(model);

        String html = templateEngine.process(getTemplate(), context);

        helper.setTo(mailRequest.getTo());
        helper.setText(html, true);
        helper.setSubject(mailRequest.getSubject());
        helper.setFrom(mailRequest.getFrom());
        javaMailSender.send(mimeMessage);

        mailResponse.setMessage("Email send to: " + mailRequest.getTo());
        mailResponse.setStatus(Boolean.TRUE);
    }

    private static String getTemplate() {
        return "/email_template";
    }
}
