package edu.tec.azuay.faan.service.implementation;

import edu.tec.azuay.faan.exceptions.DuplicatedObjectFoundException;
import edu.tec.azuay.faan.exceptions.InvalidPasswordException;
import edu.tec.azuay.faan.exceptions.ObjectNotFoundException;
import edu.tec.azuay.faan.persistence.dto.email.ResetPasswordRequest;
import edu.tec.azuay.faan.persistence.dto.primary.Author;
import edu.tec.azuay.faan.persistence.dto.primary.SaveUser;
import edu.tec.azuay.faan.persistence.dto.secondary.SavedUser;
import edu.tec.azuay.faan.persistence.entity.Image;
import edu.tec.azuay.faan.persistence.entity.User;
import edu.tec.azuay.faan.persistence.repository.IUserRepository;
import edu.tec.azuay.faan.persistence.repository.ImageRepository;
import edu.tec.azuay.faan.persistence.utils.Role;
import edu.tec.azuay.faan.service.interfaces.IUploadService;
import edu.tec.azuay.faan.service.interfaces.IUserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final IUserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final ModelMapper modelMapper;

    private final ImageRepository imageRepository;

    private final IUploadService uploadService;

    private final PasswordEncoder bCryptPasswordEncoder;

    private static final String FOLDER = "images";

    public User saveUserToEntity(SaveUser saveUser) {
        return modelMapper.map(saveUser, User.class);
    }

    public User saveUserToEntity(SavedUser savedUser) {
        return modelMapper.map(savedUser, User.class);
    }

    public SavedUser entityToDto(User user){
        Image image = imageRepository.findByImagePath(user.getImagePath());
        user.setImageUrl(image.getImageUrl());

        return modelMapper.map(user, SavedUser.class);
    }

    @Transactional
    @Override
    public User createOneUser(SaveUser newUser, MultipartFile file) throws IOException {
        Optional<User> presentUser = userRepository.findByUsernameOrEmailIgnoreCase(newUser.getUsername(), newUser.getEmail());

        if (presentUser.isPresent()) {
            throw new DuplicatedObjectFoundException(String.format("User with username: %s or email: %s already exists", newUser.getUsername(), newUser.getEmail()));
        }

        String imageResponse = uploadService.saveFile(file, FOLDER);

        validatePassword(newUser);

        User user = saveUserToEntity(newUser);
        user.setPassword(passwordEncoder.encode(newUser.getPassword()));
        user.setRole(Role.USER);
        user.setImagePath(imageResponse);

        return userRepository.insert(user);
    }

    @Transactional
    @Override
    public SavedUser updateUserData(String id, SavedUser user) {
        User currentUser = userRepository.findById(id).orElseThrow(
                () -> new ObjectNotFoundException("User with id: " + id + " not found")
        );

        currentUser.setName(user.getName().isEmpty() ? currentUser.getName() : user.getName());
        currentUser.setLastname(user.getLastname().isEmpty() ? currentUser.getLastname() : user.getLastname());
        currentUser.setLocation(user.getLocation() == null ? currentUser.getLocation() : user.getLocation());

        if (!user.getEmail().isEmpty() && !currentUser.getEmail().equalsIgnoreCase(user.getEmail())) {
            if (userRepository.existsByEmailIgnoreCase(user.getEmail())) {
                throw new DuplicatedObjectFoundException("Email already exists");
            } else {
                currentUser.setEmail(user.getEmail().toLowerCase());
            }
        }

        if (!user.getPhone().isEmpty() && !currentUser.getPhone().equalsIgnoreCase(user.getPhone())) {
            if (userRepository.existsByPhone(user.getPhone())) {
                throw new DuplicatedObjectFoundException("Phone already exists");
            } else {
                currentUser.setPhone(user.getPhone());
            }
        }
        if (!user.getUsername().isEmpty() && !currentUser.getUsername().equalsIgnoreCase(user.getUsername())) {
            if (userRepository.existsByUsernameIgnoreCase(user.getUsername())) {
                throw new DuplicatedObjectFoundException("Username already exists");
            } else {
                currentUser.setUsername(user.getUsername().toLowerCase());
            }
        }

        userRepository.save(currentUser);
        return user;
    }

    @Override
    public Boolean updatePassword(String id, ResetPasswordRequest password) {
        User currentUser = userRepository.findById(id).orElseThrow(
                () -> new ObjectNotFoundException("User with id: " + id + " not found")
        );

        validatePassword(password);

        currentUser.setPassword(bCryptPasswordEncoder.encode(password.getNewPassword()));
        userRepository.save(currentUser);
        return true;
    }

    @Override
    public SavedUser findOneById(String id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("User not found"));

        return entityToDto(user);
    }

    @Override
    public List<SavedUser> findUsersNear(GeoJsonPoint location) {
        int distance = 15000;
        Point point = new Point(location.getCoordinates().get(0), location.getCoordinates().get(1));

        List<User> usersNear = userRepository.findByLocationNear(point, distance);

        if (!usersNear.isEmpty()) {
            return usersNear.stream().map(this::entityToDto).toList();
        }

        return List.of();
    }

    @Override
    public Optional<User> findOneByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> findByUsernameOrEmail(String username, String email) {
        return userRepository.findByUsernameOrEmailIgnoreCase(username, email);
    }

    @Override
    public Optional<User> findByEmailIgnoreCase(String email) {
        return userRepository.findByEmailIgnoreCase(email);
    }

    @Transactional
    @Override
    public User updateUserReference(String username, MultipartFile file) throws IOException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ObjectNotFoundException("User not found"));

        String imageResponse = uploadService.saveFile(file, FOLDER);

        if (ObjectUtils.isEmpty(imageResponse)) {
            throw new IOException("Unable to update the photo in user with username:" + username);
        }

        user.setImagePath(imageResponse);
        user.setImageUrl(uploadService.getUrlFile(imageResponse, FOLDER));

        return userRepository.save(user);
    }

    /**
     * @param email
     * @return
     */
    @Override
    public Boolean existsByEmailIgnoreCase(String email) {
        return userRepository.existsByEmailIgnoreCase(email);
    }

    /**
     * @param username
     * @return
     */
    @Transactional
    @Override
    public Boolean existsByUsernameIgnoreCase(String username) {
        return userRepository.existsByUsernameIgnoreCase(username);
    }

    /**
     * @param phone
     * @return
     */
    @Transactional
    @Override
    public Boolean existsByPhone(String phone) {
        return userRepository.existsByPhone(phone);
    }

    @Transactional(readOnly = true)
    @Override
    public Author getAuthorByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ObjectNotFoundException("User not found"));
        Image image = imageRepository.findByImagePath(user.getImagePath());
        user.setImageUrl(image.getImageUrl());

        return modelMapper.map(user, Author.class);
    }

    private void validatePassword(SaveUser newUser) {
        if (!StringUtils.hasText(newUser.getPassword()) || !StringUtils.hasText(newUser.getRepeatedPassword())) {
            throw new InvalidPasswordException("Passwords doesn't match");
        }

        if (!newUser.getPassword().equals(newUser.getRepeatedPassword())) {
            throw new InvalidPasswordException("Passwords doesn't match");
        }
    }

    private void validatePassword(ResetPasswordRequest passwordRequest) {
        if (!StringUtils.hasText(passwordRequest.getNewPassword()) || !StringUtils.hasText(passwordRequest.getNewRepeatedPassword())) {
            throw new InvalidPasswordException("Passwords doesn't match");
        }

        if (!passwordRequest.getNewPassword().equals(passwordRequest.getNewRepeatedPassword())) {
            throw new InvalidPasswordException("Passwords doesn't match");
        }
    }
}
