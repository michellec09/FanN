package edu.tec.azuay.faan.controller.notification;

import edu.tec.azuay.faan.persistence.dto.secondary.MarkAsRead;
import edu.tec.azuay.faan.persistence.entity.Notification;
import edu.tec.azuay.faan.service.interfaces.INotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final INotificationService notificationService;

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/by-user")
    public ResponseEntity<Page<Notification>> getNotificationsByUser(@RequestParam String userId,
                                                                     @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                                                                     @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        return ResponseEntity.ok(notificationService.getNotificationsByUser(userId, PageRequest.of(pageNumber, pageSize)));
    }

    @MessageMapping("/mark-as-read")
    public ResponseEntity<?> markAsRead(@RequestBody MarkAsRead markAsRead) {
        notificationService.markAsRead(markAsRead);
        return ResponseEntity.ok().build();
    }
}
