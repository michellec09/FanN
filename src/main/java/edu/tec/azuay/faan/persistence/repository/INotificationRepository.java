package edu.tec.azuay.faan.persistence.repository;

import edu.tec.azuay.faan.persistence.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface INotificationRepository extends MongoRepository<Notification, String> {

    @Query(value="{$and: [{'userStates.?0': { $exists:  true }}, {'userStates.?0': 'UNREAD'}] }", sort="{ 'createdAt': -1 }")
    Page<Notification> findAllByUserStatesKeyUserIdAndUnread(String userId, Pageable pageable);

    @Query(value="{ 'userStates.user.id': ?0 }", sort="{ 'createdAt': -1 }")
    Page<Notification> findByUserOrderByCreatedAtDesc(String userId, Pageable pageable);

}
