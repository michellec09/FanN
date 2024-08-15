package edu.tec.azuay.faan.service.interfaces;

import edu.tec.azuay.faan.persistence.dto.email.ResetPasswordRequest;
import edu.tec.azuay.faan.persistence.dto.primary.Author;
import edu.tec.azuay.faan.persistence.dto.primary.SaveUser;
import edu.tec.azuay.faan.persistence.dto.secondary.SavedUser;
import edu.tec.azuay.faan.persistence.entity.User;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface IUserService {

	User createOneUser(SaveUser newUser, MultipartFile file) throws IOException;

	Optional<User> findOneByUsername(String username);

	Author getAuthorByUsername(String username);

	Optional<User> findByUsernameOrEmail(String username, String email);

	Optional<User> findByEmailIgnoreCase(String email);

	User updateUserReference(String username, MultipartFile file) throws IOException;

	SavedUser updateUserData(String id, SavedUser user);

	Boolean existsByEmailIgnoreCase(String email);

	Boolean existsByUsernameIgnoreCase(String username);

	Boolean existsByPhone(String phone);

	Boolean updatePassword(String id, ResetPasswordRequest resetPasswordRequest);

    SavedUser findOneById(String id);

	List<SavedUser> findUsersNear(GeoJsonPoint location);
}
