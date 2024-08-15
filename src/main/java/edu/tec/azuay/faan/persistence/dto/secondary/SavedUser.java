package edu.tec.azuay.faan.persistence.dto.secondary;

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
public class SavedUser implements Serializable {

    private String id;

    private String email;

    private String name;

    private String lastname;

    private String username;

    private GeoJsonPoint location;

    private String phone;

    private String imageUrl;

    private String imagePath;

}
