package edu.tec.azuay.faan.service.secondary;

import edu.tec.azuay.faan.config.websocket.WebSocketConfig;
import edu.tec.azuay.faan.persistence.dto.primary.LikedPost;
import edu.tec.azuay.faan.persistence.dto.primary.SavePost;
import edu.tec.azuay.faan.persistence.dto.secondary.SavedUser;
import edu.tec.azuay.faan.persistence.entity.Notification;
import edu.tec.azuay.faan.persistence.utils.NotificationState;
import edu.tec.azuay.faan.service.interfaces.INotificationService;
import edu.tec.azuay.faan.service.interfaces.IUserService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    private final WebSocketConfig webSocketConfig;

    private final INotificationService notificationService;

    private final IUserService userService;

    private final MessageSource messageSource;

    private final static String TITLE_CODE = "notification.new_post.title";

    private final static String LOST_STATE = "notification.new_post.state.lost";

    private final static String FOUND_STATE = "notification.new_post.state.found";

    private final static String ADOPTED_STATE = "notification.new_post.state.adopted";

    public WebSocketService(SimpMessagingTemplate messagingTemplate, WebSocketConfig webSocketConfig, INotificationService notificationService, IUserService userService, MessageSource messageSource) {
        this.messagingTemplate = messagingTemplate;
        this.webSocketConfig = webSocketConfig;
        this.notificationService = notificationService;
        this.userService = userService;
        this.messageSource = messageSource;
    }

    private String getMessage(String code, Object... args){
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }

    public void notifyNewPost(SavePost post) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = authentication.getName();

        ConcurrentHashMap<String, String> allUsers = webSocketConfig.getConnectedUsers();

        Notification notification = createNotification(post.getImageUrl(), createNotificationContent(post.getId(), post.getAuthor().getUsername(), post.getAnimal().getName(), post.getState()));

        List<String> users = userService.findUsersNear(post.getLocation()).stream()
                .map(SavedUser::getUsername)
                .filter(username -> !username.equals(currentUser))
                .toList();

        users.forEach(username -> {
            notification.getUserStates().put(username, NotificationState.UNREAD);
            if (allUsers.containsKey(username)) {
                System.out.println("Sending notification to: " + username);
                sendNotification(username, notification);
            }
        });

        notificationService.saveNotification(notification);
    }

    public void notifyLikePost(LikedPost post) {
        messagingTemplate.convertAndSend("/specific/likePost", post);
    }

    private void sendNotification(String username, Notification notification) {
        messagingTemplate.convertAndSendToUser(username, "/specific/notification", notification);
    }

    private Map<String, String> createNotificationContent(String id, String author, String animal, String state){
        Map<String, String> content = new HashMap<>();
        content.put("id", id);
        content.put("title", replaceCharTitle(author, animal, state));
        content.put("author", author);
        content.put("animal", animal);
        content.put("state", state);

        return content;
    }

    private Notification createNotification(String imageUrl, Map<String, String> content){
        Notification notification = new Notification();
        notification.setContent(content);
        notification.setTitle(content.get("title"));
        notification.setImageUrl(imageUrl);
        notification.setUserStates(new HashMap<>());
        notification.setCreatedAt(LocalDateTime.now());

        return notification;
    }

    private String replaceCharTitle(String user, String animal, String state){
        String stateTranslated = state.equals("LOST") ? getMessage(LOST_STATE) : state.equals("FOUND") ? getMessage(FOUND_STATE) : getMessage(ADOPTED_STATE);

        return getMessage(TITLE_CODE, user, animal, stateTranslated);
    }
}