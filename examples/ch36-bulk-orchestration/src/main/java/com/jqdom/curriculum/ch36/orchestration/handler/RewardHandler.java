package com.jqdom.curriculum.ch36.orchestration.handler;

import com.jqdom.curriculum.ch36.orchestration.CallContext;
import com.jqdom.curriculum.ch36.orchestration.CallHandler;
import com.jqdom.curriculum.ch36.orchestration.SummaryDraft;
import com.jqdom.curriculum.ch36.reward.api.RewardApi;

import org.springframework.stereotype.Component;

@Component
class RewardHandler implements CallHandler {

    private final RewardApi rewards;

    RewardHandler(RewardApi rewards) {
        this.rewards = rewards;
    }

    @Override
    public String key() {
        return "reward";
    }

    @Override
    public void execute(CallContext context, SummaryDraft draft) {
        draft.putPoints(rewards.pointsOf(context.memberIds()));
    }
}
