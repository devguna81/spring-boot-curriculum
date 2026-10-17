package com.jqdom.curriculum.ch06;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

@SpringBootTest
@AutoConfigureMockMvc
class UserValidationApiTests {

    private static final String VALID = """
            {"name":"kim","email":"kim@example.com","age":28}""";

    @Autowired
    private MockMvc mockMvc;

    private long createUser() throws Exception {
        String location = mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(VALID))
                .andExpect(status().isCreated()).andReturn().getResponse().getHeader("Location");
        return Long.parseLong(location.substring(location.lastIndexOf('/') + 1));
    }

    @Test
    void invalidBodyReturns400AndNamesTheFields() throws Exception {
        MvcResult result = mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"k","email":"kim@gmail.com","age":-1}"""))
                .andExpect(status().isBadRequest())
                .andReturn();

        MethodArgumentNotValidException exception =
                assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException());
        Set<String> fields = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField()).collect(Collectors.toSet());
        assertEquals(Set.of("name", "email", "age"), fields);
    }

    @Test
    void missingFieldsReturn400() throws Exception {
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void validBodyIsStillAccepted() throws Exception {
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(VALID))
                .andExpect(status().isCreated());
    }

    @Test
    void putRequiresEveryField() throws Exception {
        long id = createUser();
        mockMvc.perform(put("/users/" + id).contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"lee"}"""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void patchValidatesOnlySentFields() throws Exception {
        long id = createUser();

        mockMvc.perform(patch("/users/" + id).contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("kim"));
        mockMvc.perform(patch("/users/" + id).contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"bad"}"""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void pathVariableConstraintReturns400() throws Exception {
        MvcResult result = mockMvc.perform(get("/users/-1")).andExpect(status().isBadRequest()).andReturn();

        assertInstanceOf(HandlerMethodValidationException.class, result.getResolvedException());
    }

    @Test
    void requestParamConstraintReturns400() throws Exception {
        mockMvc.perform(get("/users").param("limit", "0")).andExpect(status().isBadRequest());
        mockMvc.perform(get("/users").param("limit", "5")).andExpect(status().isOk());
    }
}
