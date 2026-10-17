package com.devguna.curriculum.ch24;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/** 외부 주소 API 역할을 하는 진짜 HTTP 서버(JDK 내장). 어댑터가 실제로 HTTP를 주고받는지 확인하는 데 쓴다. */
final class FakeAddressServer implements AutoCloseable {

    private final HttpServer server;
    final List<String> requestedPaths = new CopyOnWriteArrayList<>();
    final AtomicInteger forcedStatus = new AtomicInteger(0);
    volatile long delayMillis = 0;

    FakeAddressServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/zipcodes/", exchange -> {
            requestedPaths.add(exchange.getRequestURI().getPath());
            try {
                if (delayMillis > 0) {
                    Thread.sleep(delayMillis);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            String code = exchange.getRequestURI().getPath().substring("/zipcodes/".length());
            int status;
            String body;
            if (forcedStatus.get() != 0) {
                status = forcedStatus.get();
                body = "{}";
            } else if (code.equals("06236")) {
                status = 200;
                body = "{\"zipcode\":\"06236\",\"sido\":\"서울\",\"sigungu\":\"강남구\",\"road\":\"테헤란로\"}";
            } else {
                status = 404;
                body = "{}";
            }
            byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            try {
                exchange.sendResponseHeaders(status, bytes.length);
                exchange.getResponseBody().write(bytes);
            } catch (IOException ignored) {
                // 클라이언트가 타임아웃으로 먼저 끊은 경우
            } finally {
                exchange.close();
            }
        });
        server.start();
    }

    String baseUrl() {
        return "http://127.0.0.1:" + server.getAddress().getPort();
    }

    int port() {
        return server.getAddress().getPort();
    }

    @Override
    public void close() {
        server.stop(0);
    }
}
