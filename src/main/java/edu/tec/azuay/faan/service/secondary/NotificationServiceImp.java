package edu.tec.azuay.faan.service.secondary;

import edu.tec.azuay.faan.exceptions.ObjectNotFoundException;
import edu.tec.azuay.faan.persistence.dto.secondary.MarkAsRead;
import edu.tec.azuay.faan.persistence.entity.Notification;
import edu.tec.azuay.faan.persistence.repository.INotificationRepository;
import edu.tec.azuay.faan.persistence.utils.NotificationState;
import edu.tec.azuay.faan.service.interfaces.INotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationServiceImp implements INotificationService {

    private final INotificationRepository notificationRepository;

    /**
     * This method save a notification
     *
     * @param notification is a Notification object that contains the notification data
     */
    @Override
    public void saveNotification(Notification notification) {
        notificationRepository.save(notification);
    }

    /**
     * This method find all notifications by user
     *
     * @param userId is a String
     * @return a Page of Notification
     */
    @Override
    public Page<Notification> getNotificationsByUser(String userId, Pageable pageable) {
        return notificationRepository.findAllByUserStatesKeyUserIdAndUnread(userId, pageable);
    }

    /**
     * This method mark as read a notification if the user is the owner of the notification in the Map of userStates
     *
     * @param markAsRead is a MarkAsRead object what contains the notificationId, userId and state
     */
    @Override
    public void markAsRead(MarkAsRead markAsRead) {
        Notification notification = notificationRepository.findById(markAsRead.getNotificationId()).orElseThrow(() -> new ObjectNotFoundException("Notification not found"));
        notification.getUserStates().computeIfPresent(markAsRead.getUserId(), (k, v) -> NotificationState.READ);

        notificationRepository.save(notification);
    }
}
