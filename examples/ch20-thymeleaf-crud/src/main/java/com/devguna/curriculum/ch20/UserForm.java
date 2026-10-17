package com.devguna.curriculum.ch20;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 폼과 바인딩되는 객체. 화면의 input 이름(name, email, age)이 필드 이름과 같다.
 * Thymeleaf 의 th:field 는 getter/setter 로 값을 읽고 쓰므로 record 가 아니라 일반 클래스를 쓴다.
 * 나이를 Integer 로 둔 이유: 비어 있는 입력을 null 로 받아 "필수" 오류를 보여 주기 위해서다(int 는 빈 값이 변환 오류).
 */
public class UserForm {

    @NotBlank(message = "이름은 필수입니다")
    @Size(min = 2, max = 20, message = "이름은 2자 이상 20자 이하여야 합니다")
    private String name;

    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "이메일 형식이 올바르지 않습니다")
    private String email;

    @NotNull(message = "나이는 필수입니다")
    @Min(value = 0, message = "나이는 0 이상이어야 합니다")
    @Max(value = 150, message = "나이는 150 이하여야 합니다")
    private Integer age;

    public UserForm() { }

    public UserForm(String name, String email, Integer age) {
        this.name = name;
        this.email = email;
        this.age = age;
    }

    public static UserForm from(User user) {
        return new UserForm(user.name(), user.email(), user.age());
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
}
