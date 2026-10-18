package com.jqdom.curriculum.finalproject.post.internal;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;

import com.jqdom.curriculum.finalproject.common.Actor;
import com.jqdom.curriculum.finalproject.common.ForbiddenOperationException;
import com.jqdom.curriculum.finalproject.common.InvalidRequestException;
import com.jqdom.curriculum.finalproject.contract.AuthorStat;
import com.jqdom.curriculum.finalproject.contract.PageResponse;
import com.jqdom.curriculum.finalproject.contract.PostRequest;
import com.jqdom.curriculum.finalproject.contract.PostSummary;
import com.jqdom.curriculum.finalproject.contract.PostView;
import com.jqdom.curriculum.finalproject.post.api.PostApi;
import com.jqdom.curriculum.finalproject.post.api.PostNotFoundException;
import com.jqdom.curriculum.finalproject.post.api.PostQuery;
import com.jqdom.curriculum.finalproject.user.api.UserApi;

@Service
class PostService implements PostApi {

    private final PostMapper mapper;
    private final PostConverter converter;
    private final UserApi userApi;
    private final JsonMapper jsonMapper;

    PostService(PostMapper mapper, PostConverter converter, UserApi userApi, JsonMapper jsonMapper) {
        this.mapper = mapper;
        this.converter = converter;
        this.userApi = userApi;
        this.jsonMapper = jsonMapper;
    }

    @Override
    @Transactional
    public PostView create(Actor actor, PostRequest request) {
        PostEntity entity = new PostEntity(actor.id(), request.title(), request.content(),
                Boolean.TRUE.equals(request.pinned()), new PostMeta(request.tags()));
        mapper.insert(entity);
        // created_at은 DB 기본값이 채웠으므로 다시 읽어 응답에 담는다.
        return get(entity.getId());
    }

    @Override
    @Transactional
    public PostView update(Actor actor, long id, PostRequest request) {
        PostEntity entity = find(id);
        requireOwnerOrAdmin(actor, entity);
        entity.setTitle(request.title());
        entity.setContent(request.content());
        entity.setPinned(Boolean.TRUE.equals(request.pinned()));
        entity.setMeta(new PostMeta(request.tags()));
        mapper.updateById(entity);
        return get(id);
    }

    @Override
    @Transactional
    public void delete(Actor actor, long id) {
        requireOwnerOrAdmin(actor, find(id));
        mapper.deleteById(id);
    }

    @Override
    public PostView get(long id) {
        PostEntity entity = find(id);
        String authorName = userApi.findById(entity.getAuthorId()).map(u -> u.displayName()).orElse("(알 수 없음)");
        return converter.toView(entity, authorName);
    }

    @Override
    public PageResponse<PostSummary> search(PostQuery query) {
        if (query.page() < 0) {
            throw new InvalidRequestException("page", "0 이상이어야 합니다");
        }
        if (query.size() < 1 || query.size() > PostQuery.MAX_SIZE) {
            throw new InvalidRequestException("size", "1 이상 " + PostQuery.MAX_SIZE + " 이하여야 합니다");
        }
        PostSearch condition = new PostSearch(
                likePattern(query.keyword()), query.authorId(), blankToNull(query.tag()), tagPattern(query.tag()),
                (long) query.page() * query.size(), query.size());
        List<PostSummary> items = mapper.search(condition).stream().map(converter::toSummary).toList();
        return new PageResponse<>(items, query.page(), query.size(), mapper.count(condition));
    }

    @Override
    public List<AuthorStat> authorStats() {
        return mapper.authorStats();
    }

    private PostEntity find(long id) {
        PostEntity entity = mapper.selectById(id);
        if (entity == null) {
            throw new PostNotFoundException(id);
        }
        return entity;
    }

    private void requireOwnerOrAdmin(Actor actor, PostEntity entity) {
        if (!actor.isAdmin() && !entity.getAuthorId().equals(actor.id())) {
            throw new ForbiddenOperationException("작성자 또는 관리자만 할 수 있습니다");
        }
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    /** 검색어를 소문자로 바꾸고 LIKE의 특수문자를 이스케이프한다. 사용자가 %를 쳐도 글자 그대로 찾는다. */
    private static String likePattern(String keyword) {
        String value = blankToNull(keyword);
        return value == null ? null : "%" + escapeLike(value.toLowerCase()) + "%";
    }

    /** H2용: JSON 배열에 들어 있는 "태그" 모양(따옴표 포함)을 찾는 패턴. */
    private String tagPattern(String tag) {
        String value = blankToNull(tag);
        return value == null ? null : "%" + escapeLike(jsonMapper.writeValueAsString(value)) + "%";
    }

    private static String escapeLike(String value) {
        return value.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }
}
