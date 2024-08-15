package edu.tec.azuay.faan.persistence.dto.primary;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Author implements Serializable {

    private String id;

    private String username;

    private String email;

    private String phone;

    private String imageUrl;
}
