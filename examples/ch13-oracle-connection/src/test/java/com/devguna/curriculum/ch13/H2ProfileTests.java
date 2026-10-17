package com.devguna.curriculum.ch13;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/** 기본 Profile(H2)에서 항상 실행되는 테스트. Oracle이 없어도 통과해야 한다. */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class H2ProfileTests {

    @Autowired
    UserMapper mapper;

    @Autowired
    MockMvc mockMvc;

    @Test
    void defaultProfileUsesH2() throws Exception {
        mockMvc.perform(get("/db/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.product").value("H2"))
                .andExpect(jsonPath("$.ping").value(1))
                .andExpect(jsonPath("$.activeProfiles.length()").value(0));
    }

    @Test
    void sameMapperWorksOnH2() {
        assertEquals(3, mapper.count());
        assertEquals(List.of("kim", "lee"), mapper.findPage(0, 2).stream().map(User::getName).toList());
        assertEquals(List.of("park"), mapper.findPage(2, 2).stream().map(User::getName).toList());
    }

    @Test
    void nullNicknameIsInsertedAsNull() {
        User user = new User("choi", "choi@example.com", null, 30);

        mapper.insert(user);

        assertNull(mapper.findById(user.getId()).getNickname());
    }
}
