package com.example.ssedemo.service;

import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * SSE (Server-Sent Events) 관련 비즈니스 로직 처리
 * SSE 연결 생성, 관리
 * 특정 사용자 or 모든 사용자에게 실시간으로 데이터 전송
 */
@Service
public class SseService {
    /**
     * 로깅을 위한 객체
     * 서비스 내 정보, 경고, 오류 등을 기록하는데 사용
     */
    private static final Logger logger = LoggerFactory.getLogger(SseService.class);

    /**
     * SSE 연결 타입아웃 시간 (단위: 밀리초)
     * 이 시간 동안 아무런 데이터 전송이 없으면 연결 자동 종료 (현재 1시간으로 설정)
     */
    private static final long TIMEOUT = 3600000L;

    /**
     * 연결 유지를 위한 하트비트 메시지 전송 간격 (단위: 밀리초)
     * 중간에 위치한 프록시나 방화벽에 의해 연결이 끊어지는 것을 방지 (15초)
     */
    private static final long HEARTBEAT_INTERVAL = 15000;

    /**
     * 사용자 ID를 키(key)로, SseEmitter 객체를 값으로 가지는 맵
     * 현재 연결된 모든 클라이언트의 SseEmitter를 저장하고 관리
     * 여러 스레드에서 동시에 접근해도 안전하도록 ConcurrentHashMap 사용
     */
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    /**
     *  하트비트 전송과 같은 백그라운드 작업을 처리하기 위한 스레드 풀.
     *  각 클라이언트의 하트비트 전송 작업을 별도의 스테드에서 실행하여 메인 스레드의 부담을 줄임
     */
    private final ExecutorService heartbeatExecutor = Executors.newCachedThreadPool();

    /**
     * 클라이언트 연결 요청을 받아 SseEmitter를 생성하고 반환
     * @param userId 연결을 식별할 고유 사용자 ID (ex) 로그인 아이디, 세션 ID 등 )
     * @return 생성된 SseEmitter 객체, 이 객체를 통해 클라이언트로 데이터 전송 가능
     */
    public SseEmitter createEmitter(String userId) {
        // 1. 동일한 사용자의 이전 연결이 있다면 종료
        if (emitters.containsKey(userId)) {
            logger.info("기존 사용자 연결 종료: {}", userId);
            SseEmitter emitter = emitters.get(userId);
            emitter.complete();
        }
        // 2. 새로운 SseEmitter 객체 생성 후 타임아웃 설정
        SseEmitter emitter = new SseEmitter(TIMEOUT);
        logger.info("새로운 SSE연결 생성. 사용자 ID: {}", userId);
        // 3. Emitter의 생명주기 이벤트를 처리할 콜백 함수들 등록
        // onCompletion: 연결이 정상적으로 완료 되었을 떄
        // onTimeout: 설정한 타음아웃 시간이 지났을 때
        // onError: 오류가 발생하였을 때
        emitter.onCompletion(() -> removeEmitter(userId, "onCompletion 콜백 실행"));
        emitter.onTimeout(() -> removeEmitter(userId, "onTimeout 콜백 실행"));
        emitter.onError((e) -> removeEmitter(userId, "onError 콜백 실행"));

        try {
            // 4. 연결이 성공했다는 초기 더미(dummy) 데이터를 클라이언트로 보냅니다.
            // 이 초기 데이터는 클라이언트가 연결 상태를 확인하는 데 사용될 수 있습니다.

                emitter.send(SseEmitter.event()
                        .id("0") // 이벤트 ID
                        .name("connect")
                        .data(userId + "님이 연결되었습니다.")); // 전송 데이터
                // 5. 생성된 Emitter를 관리 목록에 추가합니다.
                emitters.put(userId, emitter);
                // 6. 연결 유지를 위한 하트비트 전송을 시작합니다.
                startHeartbeat(userId, emitter);

                return emitter;
            } catch (IOException e) {
                logger.error("초기 연결 메시지 전송 중 오류 발생: {}", e.getMessage());
                //오류 발생 시 Emitter를 완료 상태로 만들고, 클라이언트에게 오류 전송
                emitter.completeWithError(e);
                return emitter;
            }


    }

    /**
     * 지정된 사용자의 연결을 종료
     * @param userId 종료할 사용자의 ID
     */
    public void disconnect(String userId) {
        if (emitters.containsKey(userId)) {
            logger.info("사용자 연결 수동 해제: {}", userId);
            emitters.get(userId).complete();
        }
    }
    /**
     * 연결된 모든 사용자에게 메시지를 브로드캐스트합니다.
     * @param message 브로드캐스트할 메시지 내용
     */
    public void broadcastToAll(String message){
        if (emitters.isEmpty()){
            logger.info("연결된 사용자가 없어 브로드캐스트 X");
            return;
        }
        emitters.forEach((userId, emitter) -> {
            try {
                emitter.send(SseEmitter.event().name("broadcast").data(message));
            } catch (IOException e){
                logger.warn("브로드캐스트 메시지 전송 실패: {}", userId);
                removeEmitter(userId, "broadcast failed");
            }
        });
    }

    /**
     *  현재 연결된 모든 사용자의 ID와 수를 반환합니다.
     * @return 연결된 사용자 정보 맵
     */
    public Map<String, Object> getConnectedUser(){
        return Map.of(
                "count", emitters.size(),
                "users", emitters.keySet()
        );
    }

    /**
     * 특정 사용자에게 데이터 전송
     * @param userId 데이터를 받을 사용자 ID
     * @param eventName 이벤트 이름 (클라이언트에서 이 이름으로 이벤트 수신)
     * @param data 전송할 데이터 (객체 형태 가능, JSON 형태로 전송)
     */
    public void sendToUser(String userId, String eventName, Object data){
        // 해당 사용자의 SseEmitter를 관리 목록에서 찾음
        SseEmitter emitter = emitters.get(userId);
        if(emitter != null){
            try {
                //데이터를 이벤트 형태로 만들어 전송
                emitter.send(SseEmitter.event().name(eventName).data(data));
                logger.info("사용자에게 데이터 전송 성공: [ID: {}, Event: {}]", userId, eventName);
            } catch (IOException e) {
                // 데이터 전송 중 오류가 발생하면 (주로 클라이언트 연결 끊김), 해당 Emitter 제거
                logger.warn("사용자에게 데이터 전송 실패 [ID: {}, Event: {}]", userId, eventName, e);
                removeEmitter(userId, "데이터 전송 실패");
            }
        }
    }

    /**
     * 주기적으로 하트비트(ping) 메시지를 보내 중간 프록시의 연결 타임아웃 방지
     * createEmitter 내부에서만 호출
     * @param userId
     * @param emitter
     */
    private void startHeartbeat(String userId, SseEmitter emitter) {
        // 별도의 스레드에서 하트비트 작업 실행
            heartbeatExecutor.execute(()->{
            // 현재 사용자의 Emitter가 관리 목록에 있는 동안에만 반복
                while (emitters.get(userId) == emitter){
                    try {
                        Thread.sleep(HEARTBEAT_INTERVAL);
                        emitter.send(SseEmitter.event().name("heartbeat").data("ping"));
                    } catch (Exception e){
                        // 하트비트 전송 중 예외가 발생하면 (대부분 클라이언트 연결이 끊어진 경우),
                        // 해당 Emitter를 제거하고 루프를 중단합니다.
                        logger.debug("하트비트 전송 중단 (사용자 ID: {})", userId);
                        removeEmitter(userId, "하트비트 실패");
                        break;
                    }

                }
        });
    }

    /**
     * @param userId  지정된 사용자의 SseEmitter 관리 목록에서 제거
     * @param reason onCompletion, onTimeout, onError 콜백에서 호출되어 Emitter 리소스를 정리
     */
    private void removeEmitter(String userId, String reason) {
        if (emitters.containsKey(userId)) {
            logger.info("SseEmitter 제거. 이유: [{}], 사용자 ID : [{}]", reason, userId);
            emitters.remove(userId);
        }
    }

    public int triggerSystemEvent(String eventType, String message) {
        if (emitters.isEmpty()){
            logger.info("연결된 사용자가 없어 시스템 이벤트를 전송하지 않음");
            return  0;
        }
        int successCount = 0;
        for (Map.Entry<String, SseEmitter> entry : emitters.entrySet()){
            String userId = entry.getKey();
            try {
                entry.getValue().send(SseEmitter.event()
                        .name("system-" + eventType)
                        .data(message)
                );
                successCount++;
            } catch (IOException e) {
                logger.debug("시스템 이벤트 전송 실패: {}", userId);
                removeEmitter(userId, "system event send failed");
            }
        }
        logger.info("시스템 이벤트 전송 결과 : {} 사용자에게 성공", successCount);
        return successCount;
    }

    /**
     * 애플리케이션이 종료될 때 호출되어 모든 자원을 안전하게 종료
     * &#064;PreDestroy  어노테이션에 의해 스프링이 애플리케이션 종료 직전에 자동 실행
     */
    @PreDestroy
    public void cleanup(){
        logger.info("애플리케이션 종료 - 모든 SSE 연결 및 리소스 정리 시작");
        //하트비트 스레드 풀을 안전하게 종료
        heartbeatExecutor.shutdown();
        // 남아있는 모든 Emitter 연결을 완료 처리
        emitters.values().forEach(SseEmitter::complete);
        // 관리 목록을 비웁니다.
        emitters.clear();
        logger.info("모든 SSE 연결 및 리소스 정리 완료!!");
    }
}


