package rank.game.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    @GetMapping
    public String redirectToAdminMain() {
        return "redirect:/admin/main"; // /admin 경로로 요청 시 /admin/main으로 리다이렉트
    }

    @GetMapping("/main")
    public String adminPage(Model model) {
        // 현재 인증 정보를 가져옵니다.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 인증된 사용자가 ADMIN 권한을 가지고 있는지 확인합니다.
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        log.info("Current Authentication: {}", authentication); // 현재 인증 정보를 로그에 출력합니다.
        log.info("Authenticated User: {}", authentication.getName()); // 인증된 사용자의 이름을 로그에 출력합니다.
        log.info("Is Admin: {}", isAdmin); // 사용자가 ADMIN 권한을 가지고 있는지 여부를 로그에 출력합니다.

        if (isAdmin) {
            model.addAttribute("isLogin", true);
            model.addAttribute("isAdmin", true);

            log.info("Admin access granted. Returning admin page."); // ADMIN 접근 권한이 허용되었음을 로그에 출력합니다.
            return "html/admin"; // 관리자 페이지 템플릿 이름
        } else {
            log.warn("Admin access denied. Redirecting to main page."); // ADMIN 접근 권한이 거부되었음을 로그에 출력합니다.
            return "redirect:/"; // 관리자 권한이 없으면 메인 페이지로 리다이렉트
        }
    }
}