package edu.tec.azuay.faan.persistence.dto.primary;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class SavePost implements Serializable {
    private String id;

    private String title;

    private String additionalComment;

    private String typePost;

    private Author author;

    private LocalDate date;

    private Animal animal;

    private GeoJsonPoint location;

    private String state;

    private String imageUrl;

    private List<String> likes;
}
