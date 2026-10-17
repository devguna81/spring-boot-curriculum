package com.jqdom.curriculum.ch19;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Controller 는 반환한 문자열을 "뷰 이름"으로 해석한다(@RestController 는 응답 본문).
 * 화면에 넘길 데이터는 Model 에 담고, 템플릿은 templates/{뷰 이름}.html 이다.
 */
@Controller
public class UserPageController {

    private final UserRepository repository;

    public UserPageController(UserRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/users";
    }

    @GetMapping("/users")
    public String list(@RequestParam(defaultValue = "false") boolean activeOnly, Model model) {
        List<User> users = repository.findAll().stream()
                .filter(user -> !activeOnly || user.active())
                .toList();
        model.addAttribute("users", users);
        model.addAttribute("activeOnly", activeOnly);
        return "users/list";
    }

    @GetMapping("/users/{id}")
    public String detail(@PathVariable long id, Model model) {
        User user = repository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        model.addAttribute("user", user);
        return "users/detail";
    }
}
