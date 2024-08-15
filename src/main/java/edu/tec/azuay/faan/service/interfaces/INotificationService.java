package edu.tec.azuay.faan.service.interfaces;

import edu.tec.azuay.faan.persistence.dto.secondary.MarkAsRead;
import edu.tec.azuay.faan.persistence.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface INotificationService {

    void saveNotification(Notification notification);

    Page<Notification> getNotificationsByUser(String userId, Pageable pageable);

    void markAsRead(MarkAsRead markAsRead);

}
