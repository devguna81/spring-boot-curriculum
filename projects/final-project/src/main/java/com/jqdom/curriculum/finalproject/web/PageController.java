package com.jqdom.curriculum.finalproject.web;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.jqdom.curriculum.finalproject.common.InvalidRequestException;
import com.jqdom.curriculum.finalproject.contract.PageResponse;
import com.jqdom.curriculum.finalproject.contract.PostSummary;
import com.jqdom.curriculum.finalproject.post.api.PostApi;
import com.jqdom.curriculum.finalproject.post.api.PostNotFoundException;
import com.jqdom.curriculum.finalproject.post.api.PostQuery;

/** 서버가 HTML을 만들어 주는 읽기 전용 화면. 쓰기는 React 화면(/app)과 REST API가 맡는다. */
@Controller
class PageController {

    private static final int PAGE_SIZE = 10;

    private final PostApi postApi;

    PageController(PostApi postApi) {
        this.postApi = postApi;
    }

    @GetMapping("/pages/posts")
    String list(@RequestParam(required = false) String keyword,
                @RequestParam(required = false) String tag,
                @RequestParam(defaultValue = "0") int page,
                Model model) {
        PageResponse<PostSummary> result = postApi.search(new PostQuery(keyword, null, tag, page, PAGE_SIZE));
        model.addAttribute("result", result);
        model.addAttribute("keyword", keyword == null ? "" : keyword);
        model.addAttribute("tag", tag == null ? "" : tag);
        model.addAttribute("totalPages", (int) Math.ceil(result.total() / (double) PAGE_SIZE));
        return "posts/list";
    }

    @GetMapping("/pages/posts/{id}")
    String detail(@PathVariable long id, Model model) {
        model.addAttribute("post", postApi.get(id));
        return "posts/detail";
    }

    @ExceptionHandler(PostNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String notFound(PostNotFoundException e, Model model) {
        model.addAttribute("message", e.getMessage());
        return "error/404";
    }

    @ExceptionHandler(InvalidRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String invalid(InvalidRequestException e, Model model) {
        model.addAttribute("message", e.getMessage());
        return "error/400";
    }
}
