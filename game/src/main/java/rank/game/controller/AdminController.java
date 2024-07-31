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

    @GetMapping("/main")
    public String adminPage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        log.info("Current Authentication: {}", authentication);
        log.info("Authenticated User: {}", authentication.getName());
        log.info("Is Admin: {}", isAdmin);

        if (isAdmin) {
            model.addAttribute("isLogin", true);
            model.addAttribute("isAdmin", true);

            log.info("Admin access granted. Returning admin page.");
            return "html/admin"; // 관리자 페이지 템플릿 이름
        } else {
            log.warn("Admin access denied. Redirecting to main page.");
            return "redirect:/"; // 관리자 권한이 없으면 메인 페이지로 리다이렉트
        }
    }
}
