package edu.tec.azuay.faan.persistence.dto.secondary;

import edu.tec.azuay.faan.persistence.entity.User;
import edu.tec.azuay.faan.persistence.utils.NotificationState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserNotificationState {

    @DBRef
    private User user;

    private NotificationState state;
}
