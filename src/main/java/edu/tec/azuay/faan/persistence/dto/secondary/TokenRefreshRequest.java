package edu.tec.azuay.faan.persistence.dto.secondary;

import lombok.Data;

@Data
public class TokenRefreshRequest {

    private String refreshJwt;

}