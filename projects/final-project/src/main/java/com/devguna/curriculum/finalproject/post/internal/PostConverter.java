package com.devguna.curriculum.finalproject.post.internal;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.devguna.curriculum.finalproject.contract.PostSummary;
import com.devguna.curriculum.finalproject.contract.PostView;

@Mapper(componentModel = "spring")
interface PostConverter {

    @Mapping(target = "tags", source = "meta.tags")
    PostSummary toSummary(PostRow row);

    @Mapping(target = "id", source = "entity.id")
    @Mapping(target = "authorId", source = "entity.authorId")
    @Mapping(target = "title", source = "entity.title")
    @Mapping(target = "content", source = "entity.content")
    @Mapping(target = "pinned", source = "entity.pinned")
    @Mapping(target = "createdAt", source = "entity.createdAt")
    @Mapping(target = "tags", source = "entity.meta.tags")
    @Mapping(target = "authorName", source = "authorName")
    PostView toView(PostEntity entity, String authorName);
}
