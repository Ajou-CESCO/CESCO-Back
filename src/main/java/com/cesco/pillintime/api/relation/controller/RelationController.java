package com.cesco.pillintime.api.relation.controller;

import com.cesco.pillintime.response.dto.ResponseDto;
import com.cesco.pillintime.api.relation.dto.RelationDto;
import com.cesco.pillintime.api.relation.service.RelationService;
import com.cesco.pillintime.response.util.ResponseUtil;
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

    @DeleteMapping
    public ResponseEntity<ResponseDto> deleteRelation(@RequestParam Long relationId) {
        relationService.deleteRelation(relationId);
        return ResponseUtil.makeResponse(200, "Success delete relation", null);
    }

}