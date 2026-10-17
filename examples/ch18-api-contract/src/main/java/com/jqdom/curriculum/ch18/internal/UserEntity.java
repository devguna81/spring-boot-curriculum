package com.jqdom.curriculum.ch18.internal;

/**
 * 저장용 내부 객체. 패키지 밖에서는 보이지 않는다.
 * 내부 메모(internalMemo)와 정지 여부(boolean)는 외부 계약에 그대로 나가지 않는다.
 */
record UserEntity(Long id, String name, String email, int age, String internalMemo, boolean suspended) {

    UserEntity withId(long newId) {
        return new UserEntity(newId, name, email, age, internalMemo, suspended);
    }
}
