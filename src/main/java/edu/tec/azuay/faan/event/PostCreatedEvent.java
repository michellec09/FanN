package edu.tec.azuay.faan.event;

import edu.tec.azuay.faan.persistence.dto.primary.SavePost;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;


@Getter
public class PostCreatedEvent extends ApplicationEvent {

    private final SavePost post;

    public PostCreatedEvent(SavePost post) {
        super(post);
        this.post = post;
    }

}
