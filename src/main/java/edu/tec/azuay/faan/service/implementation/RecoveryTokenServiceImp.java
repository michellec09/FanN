package edu.tec.azuay.faan.service.implementation;

import edu.tec.azuay.faan.persistence.entity.ResetToken;
import edu.tec.azuay.faan.persistence.repository.ITokenRepository;
import edu.tec.azuay.faan.service.interfaces.IRecoveryTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecoveryTokenServiceImp implements IRecoveryTokenService {

    private final ITokenRepository tokenRepository;

    @Override
    public ResetToken save(ResetToken resetToken) {
        return tokenRepository.save(resetToken);
    }

    @Override
    public Optional<ResetToken> findByToken(String token) {
        return tokenRepository.findByToken(token);
    }

    @Override
    public Optional<ResetToken> findByUserEmailOrUserUsername(String data) {
        return tokenRepository.findByUserEmailOrUserUsername(data);
    }

    @Override
    public List<ResetToken> findByUserId(String userId) {
        return tokenRepository.findByUserId(userId);
    }
}
