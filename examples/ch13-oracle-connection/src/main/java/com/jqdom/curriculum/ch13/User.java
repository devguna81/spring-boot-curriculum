package com.jqdom.curriculum.ch13;

import java.time.LocalDateTime;

public class User {

    private Long id;
    private String name;
    private String email;
    private String nickname;
    private Integer age;
    private LocalDateTime createdAt;

    public User() {
    }

    public User(String name, String email, String nickname, Integer age) {
        this.name = name;
        this.email = email;
        this.nickname = nickname;
        this.age = age;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
