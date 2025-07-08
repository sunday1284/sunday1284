package com.example.ssedemo.controller;

import com.example.ssedemo.service.SseService;
import jakarta.annotation.PreDestroy;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final SseService sseService;

    public SseController(SseService sseService) {
        this.sseService = sseService;
    }

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
       return sseService.createEmitter(userId);
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
        sseService.disconnect(userId);
        return ResponseEntity.ok(userId + "님의 연결이 해제되었습니다.");
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
        sseService.sendToUser(userId, "message", message);
        return ResponseEntity.ok(userId + "님에게 메시지가 전송되었습니다.");

    }

    // 모든 사용자에게 메시지 전송
    @PostMapping("/broadcast")
    public ResponseEntity<String> broadcastToAll(@RequestBody Map<String, String> payload) {
        String message = payload.get("message");
        if (message == null || message.isEmpty()) {
            return ResponseEntity.badRequest().body("메시지 내용이 필요합니다.");
        }
        sseService.broadcastToAll(message);
        return ResponseEntity.ok("메시지가 모든 사용자에게 브로드캐스트되었습니다.");
    }

    // 현재 연결된 사용자 목록 조회
    @GetMapping("/users")
    public ResponseEntity<Object> getConnectedUsers() {
        Map<String, Object> users = sseService.getConnectedUser();
        return ResponseEntity.ok(users);
    }

    // 외부 이벤트 시뮬레이션 (예: 시스템 알림)
    @PostMapping("/system-event")
    public ResponseEntity<String> triggerSystemEvent(@RequestBody Map<String, String> payload) {
        String eventType = payload.getOrDefault("eventType", "info");
        String message = payload.getOrDefault("message", "시스템 이벤트가 발생했습니다.");

        int successCount = sseService.triggerSystemEvent(eventType, message);

        return ResponseEntity.ok("시스템 이벤트가 " + successCount + "명의 사용자에게 전송되었습니다.");
    }

//    // 애플리케이션 종료 시 리소스 정리
//    @PreDestroy
//    public void cleanup() {
//        logger.info("애플리케이션 종료 - 모든 SSE 연결 및 리소스 정리");
//        heartbeatExecutor.shutdown();
//
//        // 모든 연결 종료
//        for (Map.Entry<String, SseEmitter> entry : emitters.entrySet()) {
//            try {
//                entry.getValue().complete();
//            } catch (Exception e) {
//                logger.warn("연결 종료 중 오류: {}", e.getMessage());
//            }
//        }
//        emitters.clear();
//    }
}