package com.devguna.curriculum.finalproject.bootstrap;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.devguna.curriculum.finalproject.common.Actor;
import com.devguna.curriculum.finalproject.contract.PostRequest;
import com.devguna.curriculum.finalproject.contract.Role;
import com.devguna.curriculum.finalproject.contract.UserView;
import com.devguna.curriculum.finalproject.post.api.PostApi;
import com.devguna.curriculum.finalproject.user.api.UserApi;

/**
 * 화면을 바로 확인할 수 있도록 데모 계정과 글을 만든다. app.seed-demo-data=true일 때만 동작하며,
 * 이미 계정이 있으면 아무것도 하지 않아 재시작해도 중복되지 않는다.
 */
@Component
@ConditionalOnProperty(name = "app.seed-demo-data", havingValue = "true")
class DemoDataSeeder implements ApplicationRunner {

    private final UserApi userApi;
    private final PostApi postApi;
    private final String demoPassword;

    DemoDataSeeder(UserApi userApi, PostApi postApi, @Value("${app.demo-password}") String demoPassword) {
        this.userApi = userApi;
        this.postApi = postApi;
        this.demoPassword = demoPassword;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (userApi.findAccount("admin").isPresent()) {
            return;
        }
        UserView admin = userApi.createAccount("admin", demoPassword, "관리자", Role.ADMIN);
        UserView alice = userApi.createAccount("alice", demoPassword, "앨리스", Role.USER);
        UserView bob = userApi.createAccount("bob", demoPassword, "밥", Role.USER);

        postApi.create(new Actor(admin.id(), Role.ADMIN),
                new PostRequest("공지: 게시판 사용 안내", "글은 로그인 후 쓸 수 있고, 본인 글만 고치거나 지울 수 있습니다.",
                        List.of("공지"), true));
        postApi.create(new Actor(alice.id(), Role.USER),
                new PostRequest("Spring Boot 첫걸음", "의존성 주입과 설정 파일부터 정리했습니다.",
                        List.of("spring", "입문"), false));
        postApi.create(new Actor(alice.id(), Role.USER),
                new PostRequest("MyBatis 동적 SQL 메모", "where와 if로 조건을 조립하는 방법을 적었습니다.",
                        List.of("mybatis", "sql"), false));
        postApi.create(new Actor(bob.id(), Role.USER),
                new PostRequest("Oracle JSON 컬럼 써 보기", "JSON_EXISTS로 태그를 찾는 예제입니다.",
                        List.of("oracle", "sql"), false));
    }
}
