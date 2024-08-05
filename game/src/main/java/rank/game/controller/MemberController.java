package rank.game.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import rank.game.dto.MemberDTO;
import rank.game.entity.MemberEntity;
import rank.game.service.MemberService;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/signup")
    public String goToSignUp(HttpSession session, Model model) {
        boolean isLogin = session.getAttribute("loginEmail") != null;
        model.addAttribute("isLogin", isLogin);
        return "html/signup";
    }

    @PostMapping("/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmail(@RequestParam String memberEmail) {
        boolean exists = memberService.emailExists(memberEmail);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/check-nickname")
    public ResponseEntity<Map<String, Boolean>> checkNickname(@RequestParam String nickname) {
        boolean exists = memberService.nicknameExists(nickname);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/getSignup")
    public String save(@ModelAttribute MemberDTO memberDTO, Model model, HttpSession session) {
        log.info("memberDTO={}", memberDTO);
        boolean isLogin = session.getAttribute("loginEmail") != null;
        model.addAttribute("isLogin", isLogin);

        if (memberService.emailExists(memberDTO.getMemberEmail())) {
            model.addAttribute("message", "이메일이 이미 사용 중입니다.");
            model.addAttribute("searchUrl", "/member/signup");
            return "html/message";
        }

        if (memberService.nicknameExists(memberDTO.getNickname())) {
            model.addAttribute("message", "닉네임이 이미 사용 중입니다.");
            model.addAttribute("searchUrl", "/member/signup");
            return "html/message";
        }

        try {
            memberService.save(memberDTO, memberDTO.getMemberPassword());
            model.addAttribute("message", "회원가입 완료되었습니다.");
            model.addAttribute("searchUrl", "/");
            return "html/message";
        } catch (Exception e) {
            log.error("Error while saving member", e);
            model.addAttribute("message", "회원가입 중 오류가 발생했습니다.");
            model.addAttribute("searchUrl", "/member/signup");
            return "html/message";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, Model model) {
        session.invalidate();
        model.addAttribute("isLogin", false);
        model.addAttribute("isAdmin", false);
        model.addAttribute("message", "로그아웃되었습니다.");
        model.addAttribute("searchUrl", "/");
        return "html/message";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute MemberDTO memberDTO, HttpSession session, Model model) {
        MemberDTO loginResult = memberService.login(memberDTO);

        if (loginResult != null) {
            session.setAttribute("loginEmail", loginResult.getMemberEmail());
            session.setAttribute("nickname", loginResult.getNickname());
            session.setAttribute("memberNum", loginResult.getNum());
            session.setAttribute("isAdmin", loginResult.isAdmin());
            session.setAttribute("isManager", loginResult.isManager());

            log.info("로그인 성공: {}", loginResult.getMemberEmail());
            log.info("관리자 여부: {}", loginResult.isAdmin() || loginResult.isManager());

            model.addAttribute("isLogin", true);
            model.addAttribute("isAdmin", loginResult.isAdmin());
            model.addAttribute("isManager", loginResult.isManager());

            // 현재 인증 정보를 가져옵니다.
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null) {
                log.info("Authentication object is not null");
                log.info("Authorities: {}", authentication.getAuthorities());
            } else {
                log.info("Authentication object is null");
            }

            // 사용자 인증 정보를 수동으로 설정합니다.
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(loginResult.getRole()));

            Authentication auth = new UsernamePasswordAuthenticationToken(loginResult.getMemberEmail(), null, authorities);
            SecurityContextHolder.getContext().setAuthentication(auth);

            // 인증된 사용자가 ADMIN 또는 MANAGER 권한을 가지고 있는지 확인합니다.
            if (loginResult.isAdmin()) {
                return "redirect:/member/admin"; // 관리자 페이지로 이동
            } else if (loginResult.isManager()) {
                return "redirect:/member/manager"; // 매니저 페이지로 이동
            } else {
                return "redirect:/"; // 일반 사용자 메인 페이지로 리다이렉트
            }
        } else {
            model.addAttribute("isLogin", false);
            return "index"; // 로그인 실패 시 로그인 페이지로 리다이렉트
        }
    }

    @GetMapping("/manager")
    public String getManagerPage(Model model, HttpSession session) {
        boolean isLogin = session.getAttribute("loginEmail") != null;
        model.addAttribute("isLogin", isLogin);

        return "html/manager";
    }

    @GetMapping("/admin")
    public String getMembersPage(Model model, HttpSession session) {
        boolean isLogin = session.getAttribute("loginEmail") != null;
        model.addAttribute("isLogin", isLogin);

        return "html/admin";
    }

    @GetMapping("/members/page")
    @ResponseBody
    public Page<MemberDTO> getMembers(@RequestParam("page") int page, @RequestParam("size") int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return memberService.findAllMembers(pageRequest);
    }

    // 권한 변경
    @PutMapping("/members/{memberId}/role")
    public ResponseEntity<?> changeMemberRole(@PathVariable Long memberId, @RequestBody Map<String, String> request) {
        String newRole = request.get("role");

        boolean success = memberService.changeMemberRole(memberId, newRole);
        if (success) {
            return ResponseEntity.ok().body(Map.of("success", true));
        } else {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "권한 변경 실패"));
        }
    }
}