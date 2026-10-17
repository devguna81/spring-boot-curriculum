package com.devguna.curriculum.ch24.application.usecase;

import com.devguna.curriculum.ch24.domain.Member;

/** 바깥(웹 등)이 응용 계층을 부르는 입구. */
public interface RegisterMemberUseCase {

    Member register(String name, String postalCode);

    Member get(long id);
}
