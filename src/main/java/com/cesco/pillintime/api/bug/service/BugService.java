package com.cesco.pillintime.api.bug.service;

import com.cesco.pillintime.api.bug.dto.BugDto;
import com.cesco.pillintime.api.bug.entity.Bug;
import com.cesco.pillintime.api.bug.repository.BugRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BugService {

    private final BugRepository bugRepository;

    public void createBugReport(BugDto bugDto) {
        String body = bugDto.getBody();

        Bug bug = new Bug(body);
        bugRepository.save(bug);
    }

}
