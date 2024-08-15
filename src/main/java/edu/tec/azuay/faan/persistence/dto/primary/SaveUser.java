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
public class SaveUser implements Serializable {

    private String id;

    private String email;

    private String name;

    private String lastname;

    private String username;

    private String password;

    private String repeatedPassword;

    private GeoJsonPoint location;

    private String phone;

}
