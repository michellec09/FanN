package edu.tec.azuay.faan.event;

import edu.tec.azuay.faan.service.secondary.WebSocketService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostCreatedEventListener implements ApplicationListener<PostCreatedEvent> {

    private final WebSocketService webSocketService;

    @Override
    public void onApplicationEvent(PostCreatedEvent event) {
        webSocketService.notifyNewPost(event.getPost());
    }

    @Override
    public boolean supportsAsyncExecution() {
        return ApplicationListener.super.supportsAsyncExecution();
    }
}
