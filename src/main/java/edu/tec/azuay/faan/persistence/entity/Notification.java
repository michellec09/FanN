package edu.tec.azuay.faan.persistence.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import edu.tec.azuay.faan.persistence.utils.NotificationState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

@Builder
@Data
@Document("notifications")
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Notification {

    @Id
    private String id;

    private String title;

    private Map<String, String> content;

    private Map<String, NotificationState> userStates;

    private String imageUrl;

    private LocalDateTime createdAt;

}
