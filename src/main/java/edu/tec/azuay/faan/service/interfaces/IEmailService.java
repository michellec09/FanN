package edu.tec.azuay.faan.service.interfaces;


import edu.tec.azuay.faan.persistence.dto.email.MailRequest;
import edu.tec.azuay.faan.persistence.dto.email.MailResponse;
import edu.tec.azuay.faan.persistence.entity.User;

import java.util.Map;

public interface IEmailService {
    MailResponse sendEmail(MailRequest mailRequest, Map<String, Object> model);

    void validateRequest(User user);

    Map<String, Object> validateToken(String token);
}
