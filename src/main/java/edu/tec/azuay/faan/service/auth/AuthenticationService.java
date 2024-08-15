package edu.tec.azuay.faan.service.auth;

import edu.tec.azuay.faan.exceptions.ObjectNotFoundException;
import edu.tec.azuay.faan.persistence.dto.auth.AuthenticationRequest;
import edu.tec.azuay.faan.persistence.dto.auth.AuthenticationResponse;
import edu.tec.azuay.faan.persistence.dto.primary.RegisteredUser;
import edu.tec.azuay.faan.persistence.dto.primary.SaveUser;
import edu.tec.azuay.faan.persistence.dto.secondary.TokenRefreshRequest;
import edu.tec.azuay.faan.persistence.entity.User;
import edu.tec.azuay.faan.service.interfaces.IUserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final IUserService userService;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    private final ModelMapper modelMapper;

    public User dtoToEntity(SaveUser newUser) {
        User user = modelMapper.map(newUser, User.class);
        user.setLocation(newUser.getLocation());

        return user;
    }

    public RegisteredUser entityToDto(User user) {
        return modelMapper.map(user, RegisteredUser.class);
    }

    public RegisteredUser registerUser(SaveUser newUser, MultipartFile file) throws IOException {
        User user = userService.createOneUser(newUser, file);

        RegisteredUser userDto = entityToDto(user);

        String jwt = jwtService.getAccessToken(user, generateExtraClaims(user));
        userDto.setJwt(jwt);

        return userDto;
    }

    private Map<String, Object> generateExtraClaims(User user) {
        Map<String, Object> extraClaims = new HashMap<>();

        extraClaims.put("role", user.getRole().name());
        extraClaims.put("authorities", user.getAuthorities());
        extraClaims.put("userId", user.getId());

        return extraClaims;
    }

    public AuthenticationResponse login(AuthenticationRequest requestAuthentication) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(requestAuthentication.getUsername(), requestAuthentication.getPassword());

        authenticationManager.authenticate(authentication);

        UserDetails user = userService.findOneByUsername(requestAuthentication.getUsername()).get();

        String accessToken = jwtService.getAccessToken(user, generateExtraClaims((User) user));
        String refreshToken = jwtService.getRefreshToken(user, generateExtraClaims((User) user));

        AuthenticationResponse authRsp = new AuthenticationResponse();
        authRsp.setAccessJwt(accessToken);
        authRsp.setRefreshJwt(refreshToken);

        return authRsp;
    }

    public boolean validate(String jwt) {

        try {
            jwtService.extractUsername(jwt);

            return true;
        } catch (Exception e) {
            Logger.getGlobal().info(e.getMessage());
            return false;
        }
    }

    public User findLoggedInUser() {
        Authentication auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        String username = (String) auth.getPrincipal();

        return userService.findOneByUsername(username)
                .orElseThrow(() -> new ObjectNotFoundException("User not found by username: " + username));
    }

    public AuthenticationResponse refreshToken(TokenRefreshRequest refreshToken) {
        User userDetails = userService.findOneByUsername(jwtService.extractUsername(refreshToken.getRefreshJwt())).get();

        if (validate(refreshToken.getRefreshJwt())) {
            String newAccessToken = jwtService.getAccessToken(userDetails, generateExtraClaims(userDetails));

            AuthenticationResponse authRsp = new AuthenticationResponse();
            authRsp.setAccessJwt(newAccessToken);
            authRsp.setRefreshJwt(refreshToken.getRefreshJwt());

            return authRsp;
        }

        return null;
    }
}
