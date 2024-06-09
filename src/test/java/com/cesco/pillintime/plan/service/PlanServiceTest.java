package com.cesco.pillintime.plan.service;

import com.cesco.pillintime.api.log.service.LogService;
import com.cesco.pillintime.api.medicine.dto.MedicineDto;
import com.cesco.pillintime.api.medicine.service.MedicineService;
import com.cesco.pillintime.api.member.entity.Member;
import com.cesco.pillintime.api.member.repository.MemberRepository;
import com.cesco.pillintime.api.plan.dto.RequestPlanDto;
import com.cesco.pillintime.api.plan.dto.ResponsePlanDto;
import com.cesco.pillintime.api.plan.service.PlanService;
import com.cesco.pillintime.member.service.MemberServiceTest;
import com.cesco.pillintime.api.plan.entity.Plan;
import com.cesco.pillintime.api.plan.mapper.PlanMapper;
import com.cesco.pillintime.api.plan.repository.PlanRepository;
import com.cesco.pillintime.api.relation.entity.Relation;
import com.cesco.pillintime.api.relation.repository.RelationRepository;
import com.cesco.pillintime.security.CustomUserDetails;
import com.cesco.pillintime.security.SecurityUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PlanServiceTest {

    private final RelationRepository relationRepository = mock(RelationRepository.class);
    private PlanRepository planRepository;
    private MemberRepository memberRepository;
    @Mock private MedicineService medicineService;
    private LogService logService;
    private SecurityUtil securityUtil;
    @InjectMocks
    private PlanService planService;

    @BeforeEach
    public void setUp() {
        memberRepository = mock(MemberRepository.class);
        planRepository = mock(PlanRepository.class);
        medicineService = mock(MedicineService.class);
        logService = mock(LogService.class);
        securityUtil = new SecurityUtil(relationRepository);
        planService = new PlanService(planRepository, memberRepository, medicineService, logService, securityUtil);
    }
    public static RequestPlanDto createPlan() {
        long longValue = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
        RequestPlanDto plan = new RequestPlanDto();
        plan.setMemberId(1L);
        plan.setMedicineId(String.format("%06d", longValue % 1000000));
        plan.setMedicineName(String.format("%06d", longValue % 1000000));
        plan.setCabinetIndex(1);
        plan.setWeekdayList(Arrays.asList(1, 2, 3, 4, 5, 6, 7));
        plan.setTimeList(Arrays.asList(LocalTime.of(8, 0), LocalTime.of(12,0),LocalTime.of(18,0)));
        return plan;
    }

    public MedicineDto createMedicineDto(){
        MedicineDto medicineDto = new MedicineDto();
        medicineDto.setCompanyName("a");
        medicineDto.setMedicineName("b");
        medicineDto.setMedicineCode("c");
        medicineDto.setMedicineImage("d");
        medicineDto.setMedicineEffect("e");
        medicineDto.setUseMethod("f");
        medicineDto.setUseWarning("g");
        medicineDto.setUseSideEffect("h");
        medicineDto.setDepositMethod("i");

        return medicineDto;
    }

    @Test
    void createPlan_Success() {
        // Given
        RequestPlanDto requestPlanDto = createPlan();

        Member requestMember = MemberServiceTest.createMember();
        Member targetMember = MemberServiceTest.createMember();
        when(memberRepository.findById(requestPlanDto.getMemberId())).thenReturn(Optional.of(targetMember));

        // securityUtil.checkPermission 내부
        List<Relation> relationList = new ArrayList<>();
        relationList.add(new Relation(requestMember, targetMember));
        when(relationRepository.findByMember(requestMember)).thenReturn(Optional.of(relationList));

        List<Plan> planList = new ArrayList<>();  // Mock이 아닌 실제 객체로 변경
        when(planRepository.findActivePlan(any())).thenReturn(Optional.of(planList));

        List<MedicineDto> medicineDtoList = new ArrayList<>();
        medicineDtoList.add(createMedicineDto());
        when(medicineService.getMedicineByMedicineId(any())).thenReturn(Optional.of(medicineDtoList));

        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getName()).thenReturn("username");
        when(authentication.getPrincipal()).thenReturn(mock(CustomUserDetails.class));
        when(SecurityUtil.getCurrentMember()).thenReturn(Optional.of(requestMember));

        // When
        planService.createPlan(requestPlanDto);

        // THen
        verify(memberRepository, times(1)).findById(requestPlanDto.getMemberId());
        verify(relationRepository, times(1)).findByMember(requestMember);
        verify(planRepository, times(1)).saveAll(any());
    }

    @Test
    void getPlanByMemberId_Success() {
        // Given
        RequestPlanDto requestPlanDto = createPlan();

        Member requestMember = MemberServiceTest.createMember();
        Member targetMember = MemberServiceTest.createMember();
        when(memberRepository.findById(requestPlanDto.getMemberId())).thenReturn(Optional.of(targetMember));

        // securityUtil.checkPermission 내부
        List<Relation> relationList = new ArrayList<>();
        relationList.add(new Relation(requestMember, targetMember));
        when(relationRepository.findByMember(requestMember)).thenReturn(Optional.of(relationList));

        List<Plan> planList = new ArrayList<>();
//        planList.add(PlanMapper.INSTANCE.toEntity(createPlan()));
        when(planRepository.findByMember(targetMember)).thenReturn(Optional.of(planList));

        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getName()).thenReturn("username");
        when(authentication.getPrincipal()).thenReturn(mock(CustomUserDetails.class));
        when(SecurityUtil.getCurrentMember()).thenReturn(Optional.of(requestMember));

        // When
        List<ResponsePlanDto> planDtoList = planService.getPlanByMemberId(requestPlanDto);

        // THen
        System.out.println("planDtoList = " + planDtoList);
        verify(memberRepository, times(1)).findById(requestPlanDto.getMemberId());
        verify(relationRepository, times(1)).findByMember(requestMember);
        verify(planRepository, times(1)).findByMember(targetMember);
    }

    @Test
    void deletePlanById_Success() {
        // Given
        Long planId = 1L;

        Member requestMember = MemberServiceTest.createMember();
        Member targetMember = MemberServiceTest.createMember();

        Plan plan = PlanMapper.INSTANCE.toEntity(createPlan());
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