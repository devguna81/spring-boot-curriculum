package com.jqdom.curriculum.ch24.adapter.address;

import com.jqdom.curriculum.ch24.application.port.AddressLookupPort;
import com.jqdom.curriculum.ch24.application.port.AddressLookupUnavailableException;
import com.jqdom.curriculum.ch24.domain.Address;

import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.net.http.HttpClient;
import java.util.Optional;

/**
 * AddressLookupPort를 외부 HTTP API로 구현한 어댑터.
 * 외부 응답을 도메인 Address로 번역하고, 외부의 실패는 Port가 약속한 예외로 바꿔 던진다.
 */
@Component
public class HttpAddressLookupAdapter implements AddressLookupPort {

    private final RestClient client;

    public HttpAddressLookupAdapter(AddressApiProperties props) {
        HttpClient http = HttpClient.newBuilder().connectTimeout(props.connectTimeout()).build();
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(http);
        factory.setReadTimeout(props.readTimeout());
        this.client = RestClient.builder().baseUrl(props.baseUrl()).requestFactory(factory).build();
    }

    @Override
    public Optional<Address> lookup(String postalCode) {
        try {
            return client.get()
                    .uri("/zipcodes/{code}", postalCode)
                    // 404는 "없는 우편번호"라는 정상적인 답이다. 본문이 있어도 읽지 않는다.
                    // (본문을 읽으면 빈 JSON이 모든 필드가 null인 객체로 바뀌어 "있는 주소"처럼 보인다.)
                    .exchange((request, response) -> {
                        if (response.getStatusCode().value() == 404) {
                            return Optional.<Address>empty();
                        }
                        if (response.getStatusCode().isError()) {
                            throw new AddressLookupUnavailableException(
                                    "주소 API 오류 응답: " + response.getStatusCode().value(), null);
                        }
                        ExternalAddressResponse body = response.bodyTo(ExternalAddressResponse.class);
                        return Optional.ofNullable(body).map(HttpAddressLookupAdapter::toDomain);
                    });
        } catch (AddressLookupUnavailableException e) {
            throw e;
        } catch (RestClientException e) {
            throw new AddressLookupUnavailableException("주소 API를 호출하지 못했습니다", e);
        }
    }

    private static Address toDomain(ExternalAddressResponse r) {
        return new Address(r.zipcode(), r.sido(), r.sigungu(), r.road());
    }
}
