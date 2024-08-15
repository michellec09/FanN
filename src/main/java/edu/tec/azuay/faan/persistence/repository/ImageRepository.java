package edu.tec.azuay.faan.persistence.repository;

import edu.tec.azuay.faan.persistence.entity.Image;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends MongoRepository<Image, String> {

    List<Image> findByOrderById();

    Image findByImageHash(String imageHash);

    Image findByImagePath(String imagePath);
}
