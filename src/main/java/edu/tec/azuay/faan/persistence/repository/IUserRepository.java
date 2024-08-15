package edu.tec.azuay.faan.persistence.repository;

import edu.tec.azuay.faan.persistence.entity.User;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IUserRepository extends MongoRepository<User, String> {



    /**
     * Method to check if a user exists by email
     *
     * @param email email to check.
     * @return true if the user exists, false otherwise.
     */
    Boolean existsByEmailIgnoreCase(String email);

    /**
     * Method to check if a user exists by username
     *
     * @param username username to check.
     * @return true if the user exists, false otherwise.
     */
    Boolean existsByUsernameIgnoreCase(String username);

    /**
     * Method to check if a user exists by phone
     *
     * @param phone phone to check.
     * @return true if the user exists, false otherwise.
     */
    Boolean existsByPhone(String phone);

    /**
     * Method to find a user by username
     *
     * @param username email to find.
     * @return the user if it exists, null otherwise.
     */
    Optional<User> findByUsername(String username);

    /**
     * Method to find a user by email
     *
     * @param email email to find.
     * @return the user if it exists, null otherwise.
     */
    Optional<User> findByEmailIgnoreCase(String email);

    /**
     * Method to find a user by username or email
     *
     * @param username username to find.
     * @param email email to find.
     * @return the user if it exists, null otherwise.
     */
    Optional<User> findByUsernameOrEmailIgnoreCase(String username, String email);

    /**
     * Method to find users nears from coordinates
     *
     * @param point are the coords of the point
     * @param distance is the distance in meters
     * @return a list of users near the point
     */
    @Query("{ 'location': { $nearSphere: { $geometry: { type: 'Point', coordinates: ?0 }, $maxDistance: ?1, $minDistance: 0 } } }")
    List<User> findByLocationNear(Point point, double distance);
}
