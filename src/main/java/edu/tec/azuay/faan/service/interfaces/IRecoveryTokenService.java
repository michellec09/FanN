package edu.tec.azuay.faan.service.interfaces;

import edu.tec.azuay.faan.persistence.entity.ResetToken;

import java.util.List;
import java.util.Optional;

public interface IRecoveryTokenService {

    ResetToken save(ResetToken resetToken);

    Optional<ResetToken> findByToken(String token);

    Optional<ResetToken> findByUserEmailOrUserUsername(String email);

    List<ResetToken> findByUserId(String userId);
}
