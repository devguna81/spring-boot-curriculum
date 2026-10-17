package com.devguna.curriculum.ch24;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.devguna.curriculum.ch24.adapter.address.AddressApiProperties;
import com.devguna.curriculum.ch24.adapter.address.HttpAddressLookupAdapter;
import com.devguna.curriculum.ch24.application.port.AddressLookupUnavailableException;
import com.devguna.curriculum.ch24.domain.Address;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Optional;

/** 어댑터가 외부 응답을 번역하고 외부 실패를 Port의 예외로 바꾸는지 진짜 HTTP로 확인한다. */
class HttpAddressLookupAdapterTests {

    private FakeAddressServer server;

    @BeforeEach
    void start() throws Exception {
        server = new FakeAddressServer();
    }

    @AfterEach
    void stop() {
        server.close();
    }

    private HttpAddressLookupAdapter adapter(Duration readTimeout) {
        return new HttpAddressLookupAdapter(
                new AddressApiProperties(server.baseUrl(), Duration.ofMillis(500), readTimeout));
    }

    @Test
    void translatesExternalFieldNamesToDomainAddress() {
        Optional<Address> found = adapter(Duration.ofSeconds(2)).lookup("06236");

        assertEquals(Optional.of(new Address("06236", "서울", "강남구", "테헤란로")), found);
        assertEquals("/zipcodes/06236", server.requestedPaths.get(0));
    }

    @Test
    void unknownCodeIsEmptyNotAnError() {
        assertEquals(Optional.empty(), adapter(Duration.ofSeconds(2)).lookup("99999"));
    }

    @Test
    void serverErrorBecomesUnavailable() {
        server.forcedStatus.set(500);

        AddressLookupUnavailableException e = assertThrows(AddressLookupUnavailableException.class,
                () -> adapter(Duration.ofSeconds(2)).lookup("06236"));
        assertTrue(e.getMessage().contains("500"));
    }

    @Test
    void slowServerBecomesUnavailableByReadTimeout() {
        server.delayMillis = 1500;

        long started = System.nanoTime();
        assertThrows(AddressLookupUnavailableException.class,
                () -> adapter(Duration.ofMillis(200)).lookup("06236"));
        long elapsedMillis = (System.nanoTime() - started) / 1_000_000;

        assertTrue(elapsedMillis < 1400, "타임아웃이 적용되지 않았다: " + elapsedMillis + "ms");
    }

    @Test
    void connectionRefusedBecomesUnavailable() {
        HttpAddressLookupAdapter adapter = adapter(Duration.ofSeconds(2));
        server.close();

        assertThrows(AddressLookupUnavailableException.class, () -> adapter.lookup("06236"));
    }
}
