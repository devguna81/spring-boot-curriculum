package com.jqdom.curriculum.finalproject.web;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jqdom.curriculum.finalproject.common.ForbiddenOperationException;
import com.jqdom.curriculum.finalproject.contract.AuthorStat;
import com.jqdom.curriculum.finalproject.post.api.PostApi;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;


@Tag(name = "관리자")
@RestController
@RequestMapping("/api/v1/admin")
class AdminController {

    private final PostApi postApi;
    private final CurrentActor currentActor;

    AdminController(PostApi postApi, CurrentActor currentActor) {
        this.postApi = postApi;
        this.currentActor = currentActor;
    }

    @Operation(summary = "작성자별 글 수", description = "관리자만 볼 수 있다.")
    @GetMapping("/stats")
    List<AuthorStat> stats() {
        if (!currentActor.require().isAdmin()) {
            throw new ForbiddenOperationException("관리자만 볼 수 있습니다");
        }
        return postApi.authorStats();
    }
}
