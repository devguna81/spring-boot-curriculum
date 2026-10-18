package com.jqdom.curriculum.ch28;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DuplicateKeyException;

/**
 * 영속 계층 슬라이스. DataSource, MyBatis, 매퍼만 띄운다. 컨트롤러와 서비스는 없다.
 * 각 테스트는 트랜잭션 안에서 실행되고 끝나면 롤백된다.
 */
@MybatisTest
class UserMapperSliceTests {

    @Autowired
    UserMapper mapper;

    @Autowired
    ApplicationContext context;

    @Test
    void sliceContainsTheMapperButNotTheWebOrServiceBeans() {
        assertThat(context.getBeanNamesForType(UserMapper.class)).hasSize(1);
        assertThat(context.getBeanNamesForType(UserService.class)).isEmpty();
        assertThat(context.getBeanNamesForType(UserController.class)).isEmpty();
    }

    @Test
    void schemaAndDataScriptsAreApplied() {
        assertThat(mapper.findAll()).extracting(User::getName).containsExactly("kim", "lee", "park");
    }

    @Test
    void insertFillsGeneratedIdAndCreatedAtIsReadBack() {
        User user = new User("choi", "choi@example.com", 30);

        assertThat(mapper.insert(user)).isEqualTo(1);

        assertThat(user.getId()).isNotNull();
        assertThat(mapper.findById(user.getId()).getCreatedAt()).isNotNull();
    }

    @Test
    void duplicateEmailViolatesTheUniqueConstraint() {
        assertThatThrownBy(() -> mapper.insert(new User("other", "kim@example.com", 20)))
                .isInstanceOf(DuplicateKeyException.class);
    }

    @Test
    void updateAndDeleteReportAffectedRows() {
        User changed = new User("kim2", "kim@example.com", 29);
        changed.setId(1L);

        assertThat(mapper.update(changed)).isEqualTo(1);
        assertThat(mapper.findById(1).getName()).isEqualTo("kim2");
        assertThat(mapper.deleteById(1)).isEqualTo(1);
        assertThat(mapper.deleteById(1)).isZero();
    }

    @Test
    void rollbackKeepsTestsIndependent() {
        // 다른 테스트가 행을 지우거나 바꿨더라도 롤백되므로, 이 테스트는 항상 처음 데이터 3건을 본다.
        List<User> users = mapper.findAll();
        assertThat(users).hasSize(3);
    }
}
