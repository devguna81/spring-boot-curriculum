package com.jqdom.curriculum.finalproject.user.api;

import java.util.Optional;

import com.jqdom.curriculum.finalproject.contract.Role;
import com.jqdom.curriculum.finalproject.contract.SignupRequest;
import com.jqdom.curriculum.finalproject.contract.UserView;

/** user 모듈이 다른 모듈에 공개하는 유일한 입구. */
public interface UserApi {

    /** 일반 회원으로 가입한다. 아이디가 이미 있으면 {@link DuplicateUsernameException}. */
    UserView signup(SignupRequest request);

    /** 역할을 지정해 계정을 만든다. 데모 데이터와 관리 작업용이며 REST로는 열지 않는다. */
    UserView createAccount(String username, String rawPassword, String displayName, Role role);

    Optional<UserView> findById(long id);

    boolean exists(long id);

    /** 로그인 검증에 쓴다. 비밀번호 해시를 포함하므로 응답으로 내보내지 않는다. */
    Optional<UserAccount> findAccount(String username);
}
