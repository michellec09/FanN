package edu.tec.azuay.faan.persistence.dto.email;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MailResponse implements Serializable {
    String message;
    Boolean status;
}
