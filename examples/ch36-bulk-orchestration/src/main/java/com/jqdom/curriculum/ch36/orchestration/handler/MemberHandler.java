package com.jqdom.curriculum.ch36.orchestration.handler;

import com.jqdom.curriculum.ch36.member.api.MemberApi;
import com.jqdom.curriculum.ch36.orchestration.CallContext;
import com.jqdom.curriculum.ch36.orchestration.CallHandler;
import com.jqdom.curriculum.ch36.orchestration.SummaryDraft;

import org.springframework.stereotype.Component;

/** member 모듈 호출 어댑터. 공통 맥락에서 id 목록을 꺼내 member.api에 한 번만 넘긴다. */
@Component
class MemberHandler implements CallHandler {

    private final MemberApi members;

    MemberHandler(MemberApi members) {
        this.members = members;
    }

    @Override
    public String key() {
        return "member";
    }

    @Override
    public void execute(CallContext context, SummaryDraft draft) {
        draft.putMembers(members.gradesOf(context.memberIds()));
    }
}
