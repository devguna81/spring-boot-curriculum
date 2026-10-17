package com.jqdom.curriculum.ch36.orchestration;

/**
 * 호출 대상 하나의 규약. key()는 규칙이 가리키는 논리 이름이고, 클래스 이름이 아니다.
 * Handler는 공통 맥락을 모듈이 받는 형태로 바꿔 모듈 api를 부르고, 결과를 초안에 채운다(Adapter 역할).
 * 실패는 예외로 알린다. 어떻게 처리할지는 Handler가 아니라 규칙의 실패 정책이 정한다.
 */
public interface CallHandler {

    String key();

    void execute(CallContext context, SummaryDraft draft);
}
