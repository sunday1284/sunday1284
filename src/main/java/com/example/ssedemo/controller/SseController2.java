package com.example.ssedemo.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/sse2")
public class SseController2 {
    private static final Logger logger = LoggerFactory.getLogger(SseController2.class);
    private final AtomicLong eventIdCounter = new AtomicLong();

    // 모든 SSE 연결을 저장하는 리스트
    private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    public SseController2() {
        // 주기적으로 모든 클라이언트에게 이벤트 전송
        executorService.scheduleAtFixedRate(() -> {
            sendEventToAllClients("서버 시간: " + System.currentTimeMillis());
        }, 0, 20, TimeUnit.SECONDS);
    }

    @GetMapping("/gwStatus")
    public SseEmitter events(HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "text/event-stream;charset=UTF-8");

//        SseEmitter emitter = new SseEmitter(180_000L); // 연결 후 3분 되면 타임아웃됨, 3 * 60 * 1000L
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        this.emitters.add(emitter);

        logger.info("New client subscribed: {}", emitter);

        emitter.onCompletion(() -> {
            this.emitters.remove(emitter);
            logger.info("Client connection completed: {}", emitter);
        });
        emitter.onTimeout(() -> {
            this.emitters.remove(emitter);
            logger.info("Client connection timed out: {}", emitter);
        });
        emitter.onError(e -> {
            this.emitters.remove(emitter);
            logger.info("Client connection error for emitter: \n\n{}, {}\n", emitter, e.getMessage());
        });

        // 연결 시작 시 초기 메시지 전송 (선택 사항)
        try {
            emitter.send(SseEmitter.event()
                    .id(String.valueOf(eventIdCounter.getAndIncrement()))
                    .name("connect") // 이벤트 이름 지정
                    .data("Successfully connected to SSE stream!")
                    .reconnectTime(10 * 1000)); // 재연결 시간 (밀리초)
        } catch (IOException e) {
            logger.error("Error sending initial connection event to emitter: {}", emitter, e);
            this.emitters.remove(emitter); // 실패 시 제거
        }

        return emitter;
    }

    // 특정 이벤트 전송 메서드
    public void sendEventToAllClients(String message) {
        emitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event()
                        .id(String.valueOf(eventIdCounter.getAndIncrement()))
                        .name("message")
                        .data(message));

                // 다른 종류의 이벤트 전송 예시
                emitter.send(SseEmitter.event()
                        .id(String.valueOf(eventIdCounter.getAndIncrement()))
                        .name("heartbeat")
                        .data("Heartbeat event: " + System.currentTimeMillis()));
            } catch (IOException e) {
                emitter.completeWithError(e);
            }
        });
    }
}