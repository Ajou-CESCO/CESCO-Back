package com.cesco.pillintime.controller;

import com.cesco.pillintime.dto.RelationDto;
import com.cesco.pillintime.dto.ResponseDto;
import com.cesco.pillintime.service.RelationService;
import com.cesco.pillintime.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/relation")
@RequiredArgsConstructor
public class RelationController {

    private final RelationService relationService;

    @PostMapping
    public ResponseEntity<ResponseDto> createRelation(@RequestParam Long requestId) {
        relationService.createRelation(requestId);
        return ResponseUtil.makeResponse(200, "Success create relation", null);
    }

    // 보호자만 사용, 피보호자 사용x
    @GetMapping
    public ResponseEntity<ResponseDto> getRelationList() {
        List<RelationDto> relationList = relationService.getRelationList();
        return ResponseUtil.makeResponse(200, "Success get relation", relationList);
    }

}