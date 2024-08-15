package edu.tec.azuay.faan.persistence.dto.primary;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegisteredUser implements Serializable {
    
    private String id;

    private String name;

    private String lastname;

    private String username;

    private String imageUrl;

    private String role;

    private String phone;

    private String email;

    private GeoJsonPoint location;

    private String jwt;
}
