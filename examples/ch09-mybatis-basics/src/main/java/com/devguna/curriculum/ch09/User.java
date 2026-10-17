package com.devguna.curriculum.ch09;

import java.time.LocalDateTime;

/**
 * 테이블 한 행에 대응하는 객체.
 * INSERT 뒤에 MyBatis가 생성된 id를 채워 넣어야 하므로 record가 아니라 setter가 있는 클래스를 쓴다.
 */
public class User {

    private Long id;
    private String name;
    private String email;
    private Integer age;
    private LocalDateTime createdAt;

    public User() {
    }

    public User(String name, String email, Integer age) {
        this.name = name;
        this.email = email;
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
