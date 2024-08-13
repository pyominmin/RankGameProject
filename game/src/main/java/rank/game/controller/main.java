package rank.game.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import rank.game.dto.MemberDTO;
import rank.game.service.MemberService;


@Slf4j
@Controller
@RequiredArgsConstructor
public class main {


    @GetMapping("/")
    public String main(HttpSession session, Model model) {
        boolean isLogin = session.getAttribute("loginEmail") != null;
        model.addAttribute("isLogin", isLogin);

        if (isLogin) {
            // 세션에서 관리자 여부와 매니저 여부를 가져옵니다.
            Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
            Boolean isManager = (Boolean) session.getAttribute("isManager");

            // 기본값 설정
            if (isAdmin == null) isAdmin = false;
            if (isManager == null) isManager = false;

            model.addAttribute("isAdmin", isAdmin);
            model.addAttribute("isManager", isManager);
        } else {
            // 로그인되지 않은 경우, 기본값을 설정
            model.addAttribute("isAdmin", false);
            model.addAttribute("isManager", false);
        }

        return "index";
    }
}
