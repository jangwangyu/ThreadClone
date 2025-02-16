package com.example.board.model.post;

import com.example.board.model.entity.PostEntity;
import com.example.board.model.user.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.ZonedDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Post(
    Long postId,

    String body,

    Long repliesCount,

    Long likesCount,

    User user,

    ZonedDateTime createdDateTime,

    ZonedDateTime updatedDateTime,

    ZonedDateTime deletedDateTime,

    Boolean isLiking

) {
    public static Post from(PostEntity postEntity) { //postentity를 post record로 변환시켜줌
        return new Post(
            postEntity.getId(),
            postEntity.getBody(),
            postEntity.getRepliesCount(),
            postEntity.getLikesCount(),
            User.from(postEntity.getUser()), //UserRecord로 변환
            postEntity.getCreatedDateTime(),
            postEntity.getUpdatedDateTime(),
            postEntity.getDeletedDateTime(),
            null
        );
    }

    public static Post from(PostEntity postEntity, boolean isLiking) { //postentity를 post record로 변환시켜줌
        return new Post(
            postEntity.getId(),
            postEntity.getBody(),
            postEntity.getRepliesCount(),
            postEntity.getLikesCount(),
            User.from(postEntity.getUser()), //UserRecord로 변환
            postEntity.getCreatedDateTime(),
            postEntity.getUpdatedDateTime(),
            postEntity.getDeletedDateTime(),
            isLiking
        );
    }
}