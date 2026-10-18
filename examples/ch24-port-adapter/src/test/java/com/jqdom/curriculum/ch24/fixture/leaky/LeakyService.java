package com.jqdom.curriculum.ch24.fixture.leaky;

import com.jqdom.curriculum.ch24.adapter.address.HttpAddressLookupAdapter;

/**
 * 응용 계층이 구체 어댑터를 직접 아는 잘못된 모양. 의존 방향 규칙이 이를 잡는지 확인하는 용도다.
 */
public class LeakyService {

    private final HttpAddressLookupAdapter adapter;

    public LeakyService(HttpAddressLookupAdapter adapter) {
        this.adapter = adapter;
    }
}
