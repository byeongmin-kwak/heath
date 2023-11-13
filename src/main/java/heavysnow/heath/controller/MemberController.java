package heavysnow.heath.controller;

import heavysnow.heath.common.LoginMemberHolder;
import heavysnow.heath.dto.*;
import heavysnow.heath.dto.postdto.PostListResponse;
import heavysnow.heath.exception.UnauthorizedException;
import heavysnow.heath.service.GoalService;
import heavysnow.heath.service.MemberService;
import heavysnow.heath.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final GoalService goalService;
    private final MemberService memberService;
    private final PostService postService;

    /**
     * 마이페이지에서 회원 정보와 목표를 가져오기 위한 요청
     * @param memberId: 조회할 멤버 아이디
     * @return: 회원 정보와 목표들을 반환
     */
    @GetMapping("/{memberId}")
    @ResponseStatus(HttpStatus.OK)
    public MemberResponseDto getMember(@PathVariable("memberId") Long memberId) {
        MemberResponseDto response = memberService.findMemberWithGoals(memberId);
        return response;
    }


    /**
     * 마이페이지에서 해당 년도 해당 월의 운동 정보 가져오기 위한 요청
     * @param memberId: 조회할 멤버 아이디
     * @param year: 조회할 년도
     * @param month: 조회할 월
     * @return: 해당 년도 해당 월의 운동 정보를 리스트 형태로 반환
     */
    @GetMapping("/{memberId}/dates")
    @ResponseStatus(HttpStatus.OK)
    public PostDatesResponseDto getExerciseDates(@PathVariable("memberId") Long memberId, @RequestParam("year") int year, @RequestParam("month") int month) {
        PostDatesResponseDto response = postService.getPostDates(memberId, year, month);
        return response;
    }

    /**
     * 마이페이지에서 회원 운동 인증글(포스트) 가져오기 위한 요청
     * @param memberId: 조회할 멤버 아이디
     * @param page: 페이지 번호
     * @return: 인증글을 9개씩 페이징하여 반환
     */
    @GetMapping("/{memberId}/posts")
    @ResponseStatus(HttpStatus.OK)
    public PostListResponse getMemberPosts(@PathVariable("memberId") Long memberId, @RequestParam(value = "page", defaultValue = "0") int page) {
        PostListResponse response = postService.getPostListByMember(memberId, page);
        return response;
    }


    /**
     * 특정 회원을 탈퇴시키기 위한 요청
     * @param memberId: 삭제할 멤버 아이디
     * @param request: 로그인 정보
     */
    @DeleteMapping("/{memberId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteMember(@PathVariable("memberId") Long memberId, HttpServletRequest request) {
        // 인증 토큰 확인
        Optional<Long> loginMemberOptional = LoginMemberHolder.findLoginMemberId(request.getHeader("accessToken"));
        Long loginMemberId = loginMemberOptional.orElseThrow(UnauthorizedException::new);

        // 회원 탈퇴
        memberService.deleteMember(memberId, loginMemberId);
    }

    /**
     * 특정 회원의 정보를 수정하기 위한 요청
     * @param memberId: 수정할 회원 아이디
     * @param memberDto: 수정할 회원 정보
     * @param request: 로그인 정보
     */
    @PatchMapping("/{memberId}")
    @ResponseStatus(HttpStatus.OK)
    public void updateMember(@PathVariable("memberId") Long memberId, @RequestBody MemberDto memberDto,
                                             HttpServletRequest request) {
        Optional<Long> loginMemberIdOptional = LoginMemberHolder.findLoginMemberId(request.getHeader("accessToken"));
        Long loginMemberId = loginMemberIdOptional.orElseThrow(UnauthorizedException::new);

        memberService.editMember(loginMemberId, memberId, memberDto);
    }

    /**
     * 특정 회원의 새로운 목표를 생성하기 위한 요청
     * @param memberId: 멤버 아이디
     * @param goalCreationDto: 목표 내용
     * @param request: 로그인 정보
     * @return: 생성된 목표의 아이디
     */
    @PostMapping("/{memberId}/goals")
    @ResponseStatus(HttpStatus.OK)
    public GoalIdResponseDto addGoal(@PathVariable("memberId") Long memberId, @RequestBody GoalCreationDto goalCreationDto,
                                                     HttpServletRequest request) {
        Optional<Long> loginMemberIdOptional = LoginMemberHolder.findLoginMemberId(request.getHeader("accessToken"));
        Long loginMemberId = loginMemberIdOptional.orElseThrow(UnauthorizedException::new);

        GoalIdResponseDto response = goalService.createGoalForMember(loginMemberId, memberId, goalCreationDto);
        return response;
    }

    /**
     * 회원 목표를 성공 / 미성공으로 변경하기 위한 요청
     * @param memberId: 수정할 목표가 속한 멤버 아이디
     * @param goalId: 수정할 목표 아이디
     * @param goalUpdateDto: 달성 여부
     * @param request: 로그인 정보
     */
    @PatchMapping("/{memberId}/goals/{goalId}")
    @ResponseStatus(HttpStatus.OK)
    public void updateGoal(@PathVariable("memberId") Long memberId, @PathVariable("goalId") Long goalId, @RequestBody GoalUpdateDto goalUpdateDto, HttpServletRequest request) {
        // 인증 토큰 확인
        Optional<Long> loginMemberOptional = LoginMemberHolder.findLoginMemberId(request.getHeader("accessToken"));
        Long loginMemberId = loginMemberOptional.orElseThrow(UnauthorizedException::new);

        goalService.updateGoalForMember(loginMemberId, memberId, goalId, goalUpdateDto);
    }

    /**
     * 특정 회원의 특정 목표를 삭제하기 위한 요청
     * @param memberId: 삭제할 목표가 속한 멤버 아이디
     * @param goalId: 삭제할 목표 아이디
     * @param request: 로그인 정보
     */
    @DeleteMapping("/{memberId}/goals/{goalId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteGoal(@PathVariable("memberId") Long memberId, @PathVariable("goalId") Long goalId,
                                           HttpServletRequest request) {
        Optional<Long> loginMemberIdOptional = LoginMemberHolder.findLoginMemberId(request.getHeader("accessToken"));
        Long loginMemberId = loginMemberIdOptional.orElseThrow(UnauthorizedException::new);

        goalService.deleteGoalForMember(loginMemberId, memberId, goalId);
    }
}