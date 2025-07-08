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

}