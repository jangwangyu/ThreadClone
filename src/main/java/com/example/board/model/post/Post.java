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

    Long likeCount,

    User user,

    ZonedDateTime createdDateTime,

    ZonedDateTime updatedDateTime,

    ZonedDateTime deletedDateTime

) {
    public static Post from(PostEntity postentity) { //postentity를 post record로 변환시켜줌
        return new Post(
            postentity.getId(),
            postentity.getBody(),
            postentity.getRepliesCount(),
            postentity.getLikeCount(),
            User.from(postentity.getUser()), //UserRecord로 변환
            postentity.getCreatedDateTime(),
            postentity.getUpdatedDateTime(),
            postentity.getDeletedDateTime()
        );
    }
}