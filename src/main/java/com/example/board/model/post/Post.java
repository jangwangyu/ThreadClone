package com.example.board.model.post;

import com.example.board.model.entity.PostEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.ZonedDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Post(
    Long postId,

    String body,

    ZonedDateTime createdDateTime,

    ZonedDateTime updatedDateTime,

    ZonedDateTime deletedDateTime

) {
    public static Post from(PostEntity entity) { //postentity를 post record로 변환시켜줌
        return new Post(
            entity.getId(),
            entity.getBody(),
            entity.getCreatedDateTime(),
            entity.getUpdatedDateTime(),
            entity.getDeletedDateTime()
        );
    }
}