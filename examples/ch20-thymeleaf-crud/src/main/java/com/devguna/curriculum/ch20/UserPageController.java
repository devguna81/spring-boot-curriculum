package com.devguna.curriculum.ch20;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 폼 처리의 기본 흐름(PRG: Post - Redirect - Get).
 * <ol>
 *   <li>GET  /users/new : 빈 폼</li>
 *   <li>POST /users     : 검증 실패 -> 같은 폼을 다시 보여 줌(입력값과 오류 유지, 상태 200)</li>
 *   <li>POST /users     : 성공 -> 저장 후 redirect. 새로고침해도 다시 저장되지 않는다.</li>
 * </ol>
 * HTML 폼은 GET/POST 만 보내므로 수정과 삭제도 POST 로 받는다.
 */
@Controller
public class UserPageController {

    private final UserService service;

    public UserPageController(UserService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/users";
    }

    @GetMapping("/users")
    public String list(Model model) {
        model.addAttribute("users", service.list());
        return "users/list";
    }

    @GetMapping("/users/new")
    public String newForm(Model model) {
        model.addAttribute("userForm", new UserForm());
        model.addAttribute("action", "/users");
        return "users/form";
    }

    @PostMapping("/users")
    public String create(@Valid @ModelAttribute("userForm") UserForm form, BindingResult result,
            Model model, RedirectAttributes redirect) {
        if (!result.hasErrors()) {
            try {
                User created = service.create(form);
                redirect.addFlashAttribute("message", "등록했습니다: " + created.name());
                return "redirect:/users";
            } catch (DuplicateEmailException e) {
                result.rejectValue("email", "duplicate", e.getMessage());
            }
        }
        model.addAttribute("action", "/users");
        return "users/form";
    }

    @GetMapping("/users/{id}/edit")
    public String editForm(@PathVariable long id, Model model) {
        model.addAttribute("userForm", UserForm.from(service.get(id)));
        model.addAttribute("action", "/users/" + id);
        return "users/form";
    }

    @PostMapping("/users/{id}")
    public String update(@PathVariable long id, @Valid @ModelAttribute("userForm") UserForm form,
            BindingResult result, Model model, RedirectAttributes redirect) {
        if (!result.hasErrors()) {
            try {
                User updated = service.update(id, form);
                redirect.addFlashAttribute("message", "수정했습니다: " + updated.name());
                return "redirect:/users";
            } catch (DuplicateEmailException e) {
                result.rejectValue("email", "duplicate", e.getMessage());
            }
        }
        model.addAttribute("action", "/users/" + id);
        return "users/form";
    }

    @PostMapping("/users/{id}/delete")
    public String delete(@PathVariable long id, RedirectAttributes redirect) {
        service.delete(id);
        redirect.addFlashAttribute("message", "삭제했습니다. id=" + id);
        return "redirect:/users";
    }
}
