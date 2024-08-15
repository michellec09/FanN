package edu.tec.azuay.faan.persistence.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import edu.tec.azuay.faan.persistence.dto.primary.Animal;
import edu.tec.azuay.faan.persistence.utils.PostState;
import edu.tec.azuay.faan.persistence.utils.PostType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@Document("posts")
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Post {

    @Id
    private String id;

    private String title;

    private String additionalComment;

    private List<String> likes;

    private LocalDateTime createAt = LocalDateTime.now();

    private Animal animal;

    private LocalDate date;

    @DBRef
    private User author;

    private String imagePath;

    @Transient
    private String imageUrl;

    private GeoJsonPoint location;

    private PostType type;

    private PostState state;
}
