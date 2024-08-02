package rank.game.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import rank.game.entity.MemberEntity;
import rank.game.repository.MemberRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    public CustomUserDetailsService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        MemberEntity member = memberRepository.findByMemberEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("No member found with email: " + email));

        return User.builder()
                .username(member.getMemberEmail())
                .password(member.getMemberPassword())
                .roles(member.getRole())// 권한 설정
                .build();
    }
}

