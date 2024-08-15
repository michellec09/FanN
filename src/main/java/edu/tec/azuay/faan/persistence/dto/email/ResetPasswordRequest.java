package edu.tec.azuay.faan.persistence.dto.email;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordRequest {

    private String newPassword;

    private String newRepeatedPassword;
}
