package rank.game.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import rank.game.entity.MemberEntity;
import rank.game.repository.MemberRepository;
import rank.game.service.MemberService;
import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/profile")
public class ProfileController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;

    @GetMapping("/myPage")
    public String myPage(HttpSession session, Model model) {
        boolean isLogin = session.getAttribute("loginEmail") != null;
        model.addAttribute("isLogin", isLogin);

        String memberEmail = (String) session.getAttribute("loginEmail");
        if (memberEmail != null) {
            // 서비스 계층을 통해 이메일로 회원 정보 조회
            Optional<MemberEntity> memberInfo = memberRepository.findByMemberEmail(memberEmail);

            log.info("memberInfo: {}", memberInfo);
            log.info("memberEmail: {}", memberEmail);


            // 모델에 이름과 전화번호 추가하기
            model.addAttribute("memberEmail", memberEmail);
            model.addAttribute("memberName", memberInfo.get().getMemberName());
            model.addAttribute("memberNickname", memberInfo.get().getNickname());

        } else {
            // 에러 처리나 기본값 설정 필요
            model.addAttribute("error", "회원 정보를 찾을 수 없습니다.");
        }

        return "html/profile";
    }

    @PostMapping("/change")
    public String updateMember(HttpSession session,
                               @RequestParam(required = false) String memberNickname,
                               @RequestParam(required = false) String memberPassword,
                               Model model) {
        String memberEmail = (String) session.getAttribute("loginEmail");

        // 로그 출력
        log.info("memberEmail: {}", memberEmail);
        log.info("memberNickname: {}", memberNickname);
        log.info("memberPassword: {}", memberPassword);

        if (memberEmail != null) {
            try {
                // 회원 정보 업데이트
                memberService.updateMemberInfo(memberEmail, memberNickname, memberPassword);
                model.addAttribute("message", "회원 정보가 성공적으로 업데이트되었습니다.");
                model.addAttribute("searchUrl", "/");

                // 업데이트된 회원 정보를 다시 조회합니다.
                Optional<MemberEntity> updatedMemberInfo = memberRepository.findByMemberEmail(memberEmail);

                if (updatedMemberInfo.isPresent()) {
                    // 업데이트된 정보를 모델에 추가합니다.
                    model.addAttribute("memberEmail", memberEmail);
                    model.addAttribute("memberName", updatedMemberInfo.get().getMemberName());
                    model.addAttribute("memberNickname", updatedMemberInfo.get().getNickname());
                } else {
                    model.addAttribute("error", "업데이트된 회원 정보를 찾을 수 없습니다.");
                }

            } catch (IllegalArgumentException e) {
                model.addAttribute("error", e.getMessage());
                log.error("Error updating member info: {}", e.getMessage());
            } catch (Exception e) {
                model.addAttribute("error", "회원 정보를 업데이트하는 중 오류가 발생했습니다.");
                log.error("Unexpected error: ", e);
            }
        } else {
            model.addAttribute("error", "로그인 상태를 확인할 수 없습니다.");
        }

        return "html/message"; // 업데이트된 정보를 포함하여 뷰로 이동
    }

    @PostMapping("/delete")
    public String memberDelete(HttpSession session, Model model) {
        String memberEmail = (String) session.getAttribute("loginEmail");

        if (memberEmail != null) {
            try {
                // 회원 탈퇴 처리
                memberService.deleteMember(memberEmail);
                session.invalidate(); // 세션 무효화

                model.addAttribute("message", "회원 탈퇴가 성공적으로 완료되었습니다.");
                model.addAttribute("searchUrl", "/");

            } catch (IllegalArgumentException e) {
                model.addAttribute("error", e.getMessage());
                log.error("Error deleting member: {}", e.getMessage());
            } catch (Exception e) {
                model.addAttribute("error", "회원 탈퇴 중 오류가 발생했습니다.");
                log.error("Unexpected error: ", e);
            }
        } else {
            model.addAttribute("error", "로그인 상태를 확인할 수 없습니다.");
        }

        return "html/message";
    }
}
