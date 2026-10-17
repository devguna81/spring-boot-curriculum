package com.devguna.curriculum.finalproject.post.internal;

import org.apache.ibatis.type.MappedTypes;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Component
@MappedTypes(PostMeta.class)
class PostMetaTypeHandler extends JsonTypeHandler<PostMeta> {

    PostMetaTypeHandler(JsonMapper mapper) {
        super(mapper, PostMeta.class);
    }
}
