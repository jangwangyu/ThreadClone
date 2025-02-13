package com.example.board.model.post;

import com.example.board.model.entity.PostEntity;
import com.example.board.model.user.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.ZonedDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Post(
    Long postId,

    String body,

    User user,

    ZonedDateTime createdDateTime,

    ZonedDateTime updatedDateTime,

    ZonedDateTime deletedDateTime

) {
    public static Post from(PostEntity entity) { //postentity를 post record로 변환시켜줌
        return new Post(
            entity.getId(),
            entity.getBody(),
            User.from(entity.getUser()), //UserRecord로 변환
            entity.getCreatedDateTime(),
            entity.getUpdatedDateTime(),
            entity.getDeletedDateTime()
        );
    }
}