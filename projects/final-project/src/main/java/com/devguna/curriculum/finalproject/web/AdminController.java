package com.devguna.curriculum.finalproject.web;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devguna.curriculum.finalproject.common.ForbiddenOperationException;
import com.devguna.curriculum.finalproject.contract.AuthorStat;
import com.devguna.curriculum.finalproject.post.api.PostApi;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/admin")
class AdminController {

    private final PostApi postApi;
    private final CurrentActor currentActor;

    AdminController(PostApi postApi, CurrentActor currentActor) {
        this.postApi = postApi;
        this.currentActor = currentActor;
    }

    @GetMapping("/stats")
    List<AuthorStat> stats(HttpServletRequest http) {
        if (!currentActor.require(http).isAdmin()) {
            throw new ForbiddenOperationException("관리자만 볼 수 있습니다");
        }
        return postApi.authorStats();
    }
}
