package com.example.ssedemo.controller;

import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/sse")
public class SseController {

    private static final Logger logger = LoggerFactory.getLogger(SseController.class);

    // 사용자 ID와 SSE 연결을 매핑하는 맵
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    // 하트비트 관리를 위한 스레드 풀
    private final ExecutorService heartbeatExecutor = Executors.newCachedThreadPool();

    // 하트비트 간격 (밀리초)
    private static final long HEARTBEAT_INTERVAL = 15000; // 15초

    // 연결 타임아웃 (밀리초)
    private static final long TIMEOUT = 3600000L; // 1시간

    // SSE 연결 생성
    @GetMapping("/gwStatus")
    public SseEmitter gwStatus() {
        return connect("gwStatus");
    }

    // SSE 연결 생성
    @GetMapping("/connect/{userId}")
    public SseEmitter connect(@PathVariable String userId) {
        // 기존 연결이 있으면 완료 처리
        if (emitters.containsKey(userId)) {
            logger.info("기존 사용자 연결 종료: {}", userId);
            SseEmitter oldEmitter = emitters.get(userId);
            oldEmitter.complete();
        }

        // 새 연결 생성
        SseEmitter emitter = new SseEmitter(TIMEOUT);
        logger.info("새 SSE 연결 생성: {}", userId);

        // 연결 해제 처리
        emitter.onCompletion(() -> {
            logger.info("SSE 연결 완료: {}", userId);
            emitters.remove(userId);
        });

        emitter.onTimeout(() -> {
            logger.info("SSE 연결 타임아웃: {}", userId);
            emitters.remove(userId);
        });

        emitter.onError((e) -> {
            logger.warn("SSE 연결 오류 ({}): {}", userId, e.getMessage());
            emitters.remove(userId);
        });

        // 연결 시 초기 메시지 전송
        try {
            emitter.send(SseEmitter.event()
                    .id("0")
                    .name("connect")
                    .data(userId + "님이 연결되었습니다."));

            // 하트비트 스레드 시작
            startHeartbeat(userId, emitter);

            // 사용자 ID와 emitter 매핑 저장
            emitters.put(userId, emitter);
            return emitter;
        } catch (IOException e) {
            logger.error("초기 메시지 전송 중 오류: {}", e.getMessage());
            emitter.completeWithError(e);
            return emitter;
        }
    }

    // 하트비트 처리를 별도 메서드로 분리
    private void startHeartbeat(String userId, SseEmitter emitter) {
        AtomicInteger heartbeatCount = new AtomicInteger(0);

        heartbeatExecutor.execute(() -> {
            while (emitters.containsKey(userId) && emitters.get(userId) == emitter) {
                try {
                    Thread.sleep(HEARTBEAT_INTERVAL);

                    // 사용자가 아직 연결되어 있는지 확인
                    if (emitters.containsKey(userId) && emitters.get(userId) == emitter) {
                        try {
                            int count = heartbeatCount.incrementAndGet();
                            emitter.send(SseEmitter.event()
                                    .id("heartbeat-" + count)
                                    .name("heartbeat")
                                    .data("ping"));

                            logger.debug("하트비트 전송: {} (count: {})", userId, count);
                        } catch (IOException e) {
                            // 연결 중단 시 조용히 처리
                            logger.debug("하트비트 전송 실패 (연결 종료): {}", userId);
                            emitters.remove(userId);
                            break;
                        }
                    } else {
                        logger.debug("사용자 연결 종료로 하트비트 중단: {}", userId);
                        break;
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.warn("하트비트 스레드 중단: {}", userId);
                    break;
                } catch (Exception e) {
                    logger.warn("하트비트 처리 중 예외 발생: {} - {}", userId, e.getMessage());
                    emitters.remove(userId);
                    break;
                }
            }
        });
    }

    // 연결 해제
    @PostMapping("/disconnect/{userId}")
    public ResponseEntity<String> disconnect(@PathVariable String userId) {
        if (emitters.containsKey(userId)) {
            logger.info("사용자 연결 수동 해제: {}", userId);
            SseEmitter emitter = emitters.get(userId);
            emitter.complete();
            emitters.remove(userId);
            return ResponseEntity.ok(userId + "님의 연결이 해제되었습니다.");
        } else {
            return ResponseEntity.badRequest().body(userId + "님의 연결을 찾을 수 없습니다.");
        }
    }

    // 특정 사용자에게 메시지 전송
    @PostMapping("/send/{userId}")
    public ResponseEntity<String> sendToUser(
            @PathVariable String userId,
            @RequestBody Map<String, String> payload) {

        String message = payload.get("message");
        if (message == null || message.isEmpty()) {
            return ResponseEntity.badRequest().body("메시지 내용이 필요합니다.");
        }

        if (emitters.containsKey(userId)) {
            try {
                emitters.get(userId).send(SseEmitter.event()
                        .name("message")
                        .data(message));
                logger.info("사용자에게 메시지 전송: {}", userId);
                return ResponseEntity.ok(userId + "님에게 메시지가 전송되었습니다.");
            } catch (IOException e) {
                logger.warn("메시지 전송 실패: {} - {}", userId, e.getMessage());
                emitters.remove(userId);
                return ResponseEntity.internalServerError().body("메시지 전송 중 오류가 발생했습니다.");
            }
        } else {
            return ResponseEntity.badRequest().body(userId + "님은 현재 연결되어 있지 않습니다.");
        }
    }

    // 모든 사용자에게 메시지 전송
    @PostMapping("/broadcast")
    public ResponseEntity<String> broadcastToAll(@RequestBody Map<String, String> payload) {
        String message = payload.get("message");
        if (message == null || message.isEmpty()) {
            return ResponseEntity.badRequest().body("메시지 내용이 필요합니다.");
        }

        if (emitters.isEmpty()) {
            return ResponseEntity.ok("연결된 사용자가 없습니다.");
        }

        // 전송 성공 및 실패 사용자 카운트
        int successCount = 0;
        int failCount = 0;

        for (Map.Entry<String, SseEmitter> entry : emitters.entrySet()) {
            try {
                entry.getValue().send(SseEmitter.event()
                        .name("broadcast")
                        .data(message));
                successCount++;
            } catch (IOException e) {
                emitters.remove(entry.getKey());
                failCount++;
                logger.debug("브로드캐스트 메시지 전송 실패: {}", entry.getKey());
            }
        }

        logger.info("브로드캐스트 메시지 전송 결과: 성공 {}명, 실패 {}명", successCount, failCount);
        return ResponseEntity.ok("메시지 브로드캐스트 결과: 성공 " + successCount + "명, 실패 " + failCount + "명");
    }

    // 현재 연결된 사용자 목록 조회
    @GetMapping("/users")
    public ResponseEntity<Object> getConnectedUsers() {
        logger.info("연결된 사용자 수: {}", emitters.size());
        return ResponseEntity.ok(Map.of(
                "count", emitters.size(),
                "users", emitters.keySet()
        ));
    }

    // 외부 이벤트 시뮬레이션 (예: 시스템 알림)
    @PostMapping("/system-event")
    public ResponseEntity<String> triggerSystemEvent(@RequestBody Map<String, String> payload) {
        String eventType = payload.getOrDefault("eventType", "info");
        String message = payload.getOrDefault("message", "시스템 이벤트가 발생했습니다.");

        if (emitters.isEmpty()) {
            return ResponseEntity.ok("연결된 사용자가 없습니다.");
        }

        int successCount = 0;

        for (Map.Entry<String, SseEmitter> entry : emitters.entrySet()) {
            try {
                entry.getValue().send(SseEmitter.event()
                        .name("system-" + eventType)
                        .data(message));
                successCount++;
            } catch (IOException e) {
                logger.debug("시스템 이벤트 전송 실패: {}", entry.getKey());
                emitters.remove(entry.getKey());
            }
        }

        logger.info("시스템 이벤트 전송 결과: {} 사용자에게 성공", successCount);
        return ResponseEntity.ok("시스템 이벤트가 " + successCount + "명의 사용자에게 전송되었습니다.");
    }

    // 애플리케이션 종료 시 리소스 정리
    @PreDestroy
    public void cleanup() {
        logger.info("애플리케이션 종료 - 모든 SSE 연결 및 리소스 정리");
        heartbeatExecutor.shutdown();

        // 모든 연결 종료
        for (Map.Entry<String, SseEmitter> entry : emitters.entrySet()) {
            try {
                entry.getValue().complete();
            } catch (Exception e) {
                logger.warn("연결 종료 중 오류: {}", e.getMessage());
            }
        }
        emitters.clear();
    }
}