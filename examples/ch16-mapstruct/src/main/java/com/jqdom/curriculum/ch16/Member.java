package com.jqdom.curriculum.ch16;

import java.time.LocalDateTime;

/**
 * 도메인 객체. 외부에 보여 주면 안 되는 값(비밀번호 해시)과 내부 상태(status)를 가진다.
 * 부분 수정(PATCH)을 MapStruct의 @MappingTarget으로 처리하려고 setter가 있는 가변 클래스로 만들었다.
 */
public class Member {

    private Long id;
    private String name;
    private String email;
    private int age;
    private String passwordHash;
    private MemberStatus status;
    private LocalDateTime createdAt;

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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public MemberStatus getStatus() {
        return status;
    }

    public void setStatus(MemberStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
