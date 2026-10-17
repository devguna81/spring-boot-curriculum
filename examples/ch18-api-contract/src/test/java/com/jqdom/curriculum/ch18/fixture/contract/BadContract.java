package com.jqdom.curriculum.ch18.fixture.contract;

import com.jqdom.curriculum.ch18.fixture.internal.SomeEntity;

/** 규칙 검증용: 계약 패키지가 internal 클래스를 필드로 가지는 위반 사례. 실제 코드가 아니다. */
public class BadContract {

    public SomeEntity leaked;
}
