package com.ygmg.game.api.controller;

import com.ygmg.game.api.request.AreaModifyPutReq;
import com.ygmg.game.api.request.AreaRegisterPostReq;
import com.ygmg.game.api.response.AreaRes;
import com.ygmg.game.api.service.GameAreaService;
import com.ygmg.game.db.model.Area;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

@RestController
@RequestMapping("/api/game/area")
public class GameAreaController {

    // SSEEmitter를 저장할 ConcurrentLinkedQueue
    private final ConcurrentLinkedQueue<SseEmitter> emitters = new ConcurrentLinkedQueue<>();

    private final GameAreaService areaService;
    public GameAreaController(GameAreaService areaService){
        this.areaService = areaService;
    }

    @PostMapping("/")
    public ResponseEntity<String> createArea(@RequestBody AreaRegisterPostReq areaInfo) throws Exception {

        areaService.createArea(areaInfo);
//
//        면적이 생성되면  모든 클라이언트에게 데이터베이스에서 값을 가져와 랭킹을 매겨 전달합니다.
//        랭킹 데이터를 가져옵니다. -> Rankings
//        for (SseEmitter emitter : emitters) {
//            try {
//                // SSEEmitter로 데이터를 전달합니다.
//                emitter.send(Rankings);
//            } catch (IOException e) {
//                // 에러 처리 로직을 작성합니다.
//            }
//        }
//    }


        return ResponseEntity.status(200).body("면적이 생성되었습니다.");
    }
//  해당 면적 ID에 해당하는 면적의 넓이 수정
    @PutMapping("/")
    public ResponseEntity<String> modifyArea(@RequestBody AreaModifyPutReq areaInfo) throws Exception {
        areaService.modifyArea(areaInfo);

        return ResponseEntity.status(200).body("면적이 변경되었습니다.");
    }
    @GetMapping("/{areaId}")
    public ResponseEntity<AreaRes> getArea(@PathVariable int areaId) throws Exception {
        Area area = areaService.getAreaByAreaId(areaId);
        return ResponseEntity.status(200).body(AreaRes.of(area));
    }

    @GetMapping("game/{gameId}")
    public ResponseEntity<List<AreaRes>> getGameArea(@PathVariable int gameId) throws Exception {
        List<AreaRes> areas = areaService.getArea(gameId);
        return ResponseEntity.status(200).body(areas);
    }


    @GetMapping("member/{memberId}")
    public ResponseEntity<List<AreaRes>> getMemberAreaAll(@PathVariable int memberId) throws Exception {
        List<AreaRes> areas = areaService.getAreaByMemberId(memberId);
        return ResponseEntity.status(200).body(areas);
    }
    @GetMapping("member/{memberId}/{areaDate}")
    public ResponseEntity<List<AreaRes>> getMemberArea(@PathVariable int memberId, @PathVariable @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate areaDate) throws Exception {
        List<AreaRes> areas = areaService.getAreaByMemberIdAndAreaDate(memberId, areaDate);
        return ResponseEntity.status(200).body(areas);
    }

    // SSE를 구현하는 API
    @GetMapping("/connection")
    public SseEmitter subscribe() {
        // SSEEmitter를 생성합니다.
        SseEmitter emitter = new SseEmitter();

        // 생성한 SSEEmitter를 ConcurrentLinkedQueue에 저장합니다.
        emitters.add(emitter);

        // SSEEmitter의 onCompletion과 onTimeout 메소드를 사용하여 SSEEmitter를 제거합니다.
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));

        // SSEEmitter를 반환합니다.
        return emitter;
    }

}