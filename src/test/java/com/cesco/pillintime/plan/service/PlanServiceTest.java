package com.cesco.pillintime.plan.service;

import com.cesco.pillintime.log.service.LogService;
import com.cesco.pillintime.medicine.dto.MedicineDto;
import com.cesco.pillintime.medicine.service.MedicineService;
import com.cesco.pillintime.member.entity.Member;
import com.cesco.pillintime.member.repository.MemberRepository;
import com.cesco.pillintime.plan.dto.PlanDto;
import com.cesco.pillintime.plan.dto.RequestPlanDto;
import com.cesco.pillintime.plan.entity.Plan;
import com.cesco.pillintime.plan.mapper.PlanMapper;
import com.cesco.pillintime.plan.repository.PlanRepository;
import com.cesco.pillintime.relation.entity.Relation;
import com.cesco.pillintime.relation.repository.RelationRepository;
import com.cesco.pillintime.security.CustomUserDetails;
import com.cesco.pillintime.util.SecurityUtil;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PlanServiceTest {

    private final RelationRepository relationRepository = mock(RelationRepository.class);
    @Mock private PlanRepository planRepository;
    @Mock private MemberRepository memberRepository;
    @Mock private MedicineService medicineService;
    @Mock private LogService logService;
    @Mock private SecurityUtil securityUtil;
    @InjectMocks
    private PlanService planService;

    public static Plan createPlan() {
        long longValue = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
        Plan plan = new Plan();
        plan.setId(1L);
//        plan.setName(UUID.randomUUID().toString().replace("-", "").substring(0, 4));
//        plan.setSsn(String.format("%06d", longValue % 1000000) +"-"+ String.format("%07d", longValue % 10000000));

        return plan;
    }
    @Test
    void createPlan_Success() {
        // Given
        PlanDto planDto = PlanMapper.INSTANCE.toDto(createPlan());

        Member requestMember = mock(Member.class);
        Member targetMember = mock(Member.class);
        when(memberRepository.findById(planDto.getMemberId())).thenReturn(Optional.of(targetMember));

        // securityUtil.checkPermission 내부
        List<Relation> relationList = new ArrayList<>();
        relationList.add(new Relation(requestMember, targetMember));
        when(relationRepository.findByMember(requestMember)).thenReturn(Optional.of(relationList));

        List<Plan> planList = mock(ArrayList.class);
        when(planRepository.findActivePlan(any())).thenReturn(Optional.of(planList));

        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getName()).thenReturn("username");
        when(authentication.getPrincipal()).thenReturn(mock(CustomUserDetails.class));
        when(SecurityUtil.getCurrentMember()).thenReturn(Optional.of(requestMember));

        // When
//        planService.createPlan(planDto);

        // THen
        verify(memberRepository, times(1)).findById(planDto.getMemberId());
        verify(relationRepository, times(1)).findByMember(requestMember);
        verify(planRepository, times(1)).saveAll(any());
        verify(planRepository, times(1)).findActivePlan(any());
    }

    @Test
    void getPlanByMemberId_Success() {
        // Given
        PlanDto requestPlanDto = PlanMapper.INSTANCE.toDto(createPlan());

        Member requestMember = mock(Member.class);
        Member targetMember = mock(Member.class);
        when(memberRepository.findById(requestPlanDto.getMemberId())).thenReturn(Optional.of(targetMember));

        // securityUtil.checkPermission 내부
        List<Relation> relationList = new ArrayList<>();
        relationList.add(new Relation(requestMember, targetMember));
        when(relationRepository.findByMember(requestMember)).thenReturn(Optional.of(relationList));

        List<Plan> planList = mock(ArrayList.class);
        when(planRepository.findByMember(targetMember)).thenReturn(Optional.of(planList));

        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getName()).thenReturn("username");
        when(authentication.getPrincipal()).thenReturn(mock(CustomUserDetails.class));
        when(SecurityUtil.getCurrentMember()).thenReturn(Optional.of(requestMember));

        // When
//        List<PlanDto> planDtoList = planService.getPlanByMemberId(requestPlanDto);

        // THen
//        System.out.println("planDtoList = " + planDtoList);
        verify(memberRepository, times(1)).findById(requestPlanDto.getMemberId());
        verify(relationRepository, times(1)).findByMember(requestMember);
        verify(planRepository, times(1)).saveAll(any());
        verify(planRepository, times(1)).findActivePlan(any());
    }

    @Test
    void deletePlanById_Success() {

        // Given
        Long planId = 1L;

        Member requestMember = mock(Member.class);
        Member targetMember = mock(Member.class);

        Plan plan = mock(Plan.class);
        plan.setMember(targetMember);
        when(planRepository.findById(planId)).thenReturn(Optional.of(plan));

        // securityUtil.checkPermission 내부
        List<Relation> relationList = new ArrayList<>();
        relationList.add(new Relation(requestMember, targetMember));
        when(relationRepository.findByMember(requestMember)).thenReturn(Optional.of(relationList));

        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getName()).thenReturn("username");
        when(authentication.getPrincipal()).thenReturn(mock(CustomUserDetails.class));
        when(SecurityUtil.getCurrentMember()).thenReturn(Optional.of(requestMember));

        // When
        planService.deletePlanById(planId);

        // THen
        verify(planRepository, times(1)).findById(planId);
        verify(relationRepository, times(1)).findByMember(requestMember);
        verify(planRepository, times(1)).delete(plan);
    }
}