package yerong.acorn_post_backend.manitto.dto;

import java.time.LocalDateTime;

public record UpdateGroupRequest (
        String name,
        String description,
        LocalDateTime deadline
){
}
