package com.devguna.curriculum.ch17;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

/** 코드에서 만들어진 OpenAPI 문서가 의도한 내용을 담고 있는지 확인한다. */
@SpringBootTest
@AutoConfigureMockMvc
class OpenApiDocsTests {

    @Autowired
    MockMvc mockMvc;

    @Test
    void infoComesFromOpenApiBean() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.openapi").value(org.hamcrest.Matchers.startsWith("3.")))
                .andExpect(jsonPath("$.info.title").value("Spring Boot Curriculum - Users API"))
                .andExpect(jsonPath("$.info.version").value("v1"));
    }

    @Test
    void controllerMethodsBecomePaths() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(jsonPath("$.paths", hasKey("/users")))
                .andExpect(jsonPath("$.paths", hasKey("/users/{id}")))
                .andExpect(jsonPath("$.paths['/users'].post.summary").value("사용자 생성"))
                .andExpect(jsonPath("$.paths['/users/{id}']", hasKey("get")))
                .andExpect(jsonPath("$.paths['/users/{id}']", hasKey("put")))
                .andExpect(jsonPath("$.paths['/users/{id}']", hasKey("patch")))
                .andExpect(jsonPath("$.paths['/users/{id}']", hasKey("delete")));
    }

    @Test
    void hiddenEndpointIsNotDocumented() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(jsonPath("$.paths", not(hasKey("/demo/error"))));
    }

    @Test
    void responseCodesAreListedPerOperation() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(jsonPath("$.paths['/users'].post.responses.keys()", containsInAnyOrder("201", "400", "409")))
                .andExpect(jsonPath("$.paths['/users/{id}'].delete.responses.keys()", containsInAnyOrder("204", "404")));
    }

    @Test
    void validationAnnotationsAppearInTheSchema() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(jsonPath("$.components.schemas.CreateUserRequest.required",
                        containsInAnyOrder("name", "email", "age")))
                .andExpect(jsonPath("$.components.schemas.CreateUserRequest.properties.name.minLength").value(2))
                .andExpect(jsonPath("$.components.schemas.CreateUserRequest.properties.name.maxLength").value(20))
                .andExpect(jsonPath("$.components.schemas.CreateUserRequest.properties.email.format").value("email"))
                .andExpect(jsonPath("$.components.schemas.CreateUserRequest.properties.age.minimum").value(0))
                .andExpect(jsonPath("$.components.schemas.CreateUserRequest.properties.age.maximum").value(150));
    }

    @Test
    void patchRequestHasNoRequiredFields() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(jsonPath("$.components.schemas.PatchUserRequest.required").doesNotExist());
    }

    @Test
    void schemaAnnotationAddsDescriptionAndExample() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(jsonPath("$.components.schemas.CreateUserRequest.properties.email.example")
                        .value("kim@example.com"))
                .andExpect(jsonPath("$.components.schemas.UserResponse.description").value("사용자 응답"));
    }

    @Test
    void responseMediaTypeIsDeclaredAsJson() throws Exception {
        // produces를 선언하지 않으면 문서의 응답 형식이 */* 로 나온다.
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(jsonPath("$.paths['/users'].post.responses['201'].content", hasKey("application/json")))
                .andExpect(jsonPath("$.paths['/users'].post.responses['201'].content", not(hasKey("*/*"))));
    }

    @Test
    void swaggerUiIsServed() throws Exception {
        mockMvc.perform(get("/swagger-ui.html"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", org.hamcrest.Matchers.containsString("/swagger-ui/index.html")));
        mockMvc.perform(get("/swagger-ui/index.html")).andExpect(status().isOk());
    }

    @Test
    void documentationDoesNotChangeApiBehavior() throws Exception {
        mockMvc.perform(get("/users/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("NOT_FOUND"));
    }
}
