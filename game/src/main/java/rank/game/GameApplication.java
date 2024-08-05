package rank.game;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.transaction.annotation.Transactional;
import rank.game.entity.MemberEntity;
import rank.game.repository.MemberRepository;
import rank.game.service.MemberService;

import java.util.stream.IntStream;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@RequiredArgsConstructor
public class GameApplication implements CommandLineRunner {

	private final MemberService memberService;
	private final MemberRepository memberRepository;

	public static void main(String[] args) {
		SpringApplication.run(GameApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		memberService.createAdminAccount();
		//memberService.createTestUsers();
	}


}
