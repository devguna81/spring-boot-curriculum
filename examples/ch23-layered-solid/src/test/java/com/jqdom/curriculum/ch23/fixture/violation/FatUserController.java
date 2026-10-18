package com.jqdom.curriculum.ch23.fixture.violation;

import com.jqdom.curriculum.ch23.repository.InMemoryUserRepository;

/**
 * 리팩터링 전의 모습을 흉내 낸 코드다. 컨트롤러가 구체 저장소를 직접 만들어 쓰고 업무 규칙도 안에 둔다.
 * 계층 규칙 테스트가 이 코드를 위반으로 잡는지 확인하는 용도라서 어디에도 등록하지 않는다.
 */
public class FatUserController {

    private final InMemoryUserRepository repository = new InMemoryUserRepository();

    public String register(String name, String email) {
        if (repository.existsByEmail(email)) {
            return "duplicate";
        }
        repository.save(name, email);
        return "ok";
    }
}
