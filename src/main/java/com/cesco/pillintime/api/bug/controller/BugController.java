package com.cesco.pillintime.api.bug.controller;

import com.cesco.pillintime.api.bug.dto.BugDto;
import com.cesco.pillintime.api.bug.service.BugService;
import com.cesco.pillintime.response.dto.ResponseDto;
import com.cesco.pillintime.response.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bug")
@RequiredArgsConstructor
public class BugController {

    private final BugService bugService;

    @PostMapping
    public ResponseEntity<ResponseDto> createBugReport(@RequestBody BugDto bugDto) {
        bugService.createBugReport(bugDto);
        return ResponseUtil.makeResponse(200, "Success upload bug report",  null);
    }

}
