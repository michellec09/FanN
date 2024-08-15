package edu.tec.azuay.faan.persistence.dto.email;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MailRequest implements Serializable {

    private String to;

    private String from;

    private String subject;

    private String message;
}
