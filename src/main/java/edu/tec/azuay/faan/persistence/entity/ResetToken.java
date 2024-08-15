package edu.tec.azuay.faan.persistence.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document("resetToken")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResetToken {

    @Id
    private String id;

    private String token;

    private Boolean isActive = Boolean.TRUE;

    private Date expirationDate;

    @DBRef
    private User user;
}
