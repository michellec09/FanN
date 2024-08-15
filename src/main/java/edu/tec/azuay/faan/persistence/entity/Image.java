package edu.tec.azuay.faan.persistence.entity;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@Document("images")
@RequiredArgsConstructor
public class Image {

    @Id
    private String id;

    private final String name;

    private String imagePath;

    private String imageUrl;

    private String imageHash;

    private String imageId;
}
