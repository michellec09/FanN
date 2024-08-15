package edu.tec.azuay.faan.persistence.repository;

import edu.tec.azuay.faan.persistence.entity.ResetToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ITokenRepository extends MongoRepository<ResetToken, String> {

    Optional<ResetToken> findByToken(String token);

    Optional<ResetToken> findByUserEmail(String email);

    @Query("{ 'user.email' : ?0, 'user.username' : ?0}")
    Optional<ResetToken> findByUserEmailOrUserUsername(String email);

    List<ResetToken> findByUserId(String userId);
}
