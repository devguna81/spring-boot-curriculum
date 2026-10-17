package com.devguna.curriculum.finalproject.post.api;

import java.util.List;

import com.devguna.curriculum.finalproject.common.Actor;
import com.devguna.curriculum.finalproject.contract.AuthorStat;
import com.devguna.curriculum.finalproject.contract.PageResponse;
import com.devguna.curriculum.finalproject.contract.PostRequest;
import com.devguna.curriculum.finalproject.contract.PostSummary;
import com.devguna.curriculum.finalproject.contract.PostView;

/** post 모듈이 공개하는 입구. 권한 판단은 행위자(Actor)를 받아 이 안에서 한다. */
public interface PostApi {

    PostView create(Actor actor, PostRequest request);

    /** 작성자 본인이나 관리자만 고칠 수 있다. 다른 사람이면 ForbiddenOperationException. */
    PostView update(Actor actor, long id, PostRequest request);

    void delete(Actor actor, long id);

    PostView get(long id);

    PageResponse<PostSummary> search(PostQuery query);

    /** 작성자별 글 수. 글이 없는 작성자도 0으로 나온다. */
    List<AuthorStat> authorStats();
}
