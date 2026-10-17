package com.devguna.curriculum.ch30;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class ApiController {

    private final NoteService notes;

    ApiController(NoteService notes) {
        this.notes = notes;
    }

    @GetMapping("/api/public/ping")
    Map<String, String> ping() {
        return Map.of("status", "ok");
    }

    /** 인증된 사용자 정보. Authentication은 필터 체인이 채워 둔 현재 사용자다. */
    @GetMapping("/api/me")
    Map<String, Object> me(Authentication authentication) {
        List<String> roles = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).sorted().toList();
        return Map.of("username", authentication.getName(), "roles", roles);
    }

    @GetMapping("/api/admin/stats")
    Map<String, Object> stats() {
        return Map.of("notes", "visible to admins only");
    }

    record NoteRequest(String text) {
    }

    @PostMapping("/api/notes")
    ResponseEntity<Note> add(Authentication authentication, @RequestBody NoteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(notes.add(authentication.getName(), request.text()));
    }

    @GetMapping("/api/notes")
    List<Note> mine(Authentication authentication) {
        return notes.ownedBy(authentication.getName());
    }

    @DeleteMapping("/api/notes/{id}")
    ResponseEntity<Void> delete(@PathVariable long id) {
        notes.get(id); // 없으면 404
        notes.delete(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(NoSuchElementException.class)
    ResponseEntity<Map<String, String>> notFound(NoSuchElementException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("code", "NOT_FOUND"));
    }
}
