package rank.game.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import rank.game.dto.MemberDTO;
import rank.game.entity.MemberEntity;
import rank.game.repository.MemberRepository;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;


    //회원가입
    public void save(MemberDTO memberDTO, String memberPassword) {
        memberDTO.setMemberPassword(passwordEncoder.encode(memberPassword));
        MemberEntity memberEntity = MemberEntity.toMemberEntity(memberDTO);
        memberRepository.save(memberEntity);
    }

    //이메일 중복확인
    public boolean emailExists(String memberEmail) {

        return memberRepository.existsByMemberEmail(memberEmail);
    }
    //닉네임 중복확인
    public boolean nicknameExists(String nickname) {

        return memberRepository.existsByNickname(nickname);
    }

    //로그인
    public MemberDTO login(MemberDTO memberDTO) {
        Optional<MemberEntity> byMemberEmail = memberRepository.findByMemberEmail(memberDTO.getMemberEmail());
        if (byMemberEmail.isPresent()) {
            MemberEntity memberEntity = byMemberEmail.get();
            String encodedPassword = memberEntity.getMemberPassword();
            if (passwordEncoder.matches(memberDTO.getMemberPassword(), encodedPassword)) {
                MemberDTO dto = MemberDTO.fromEntity(memberEntity);
                if ("admin@admin.com".equals(memberEntity.getMemberEmail())) {
                    dto.setAdmin(true);
                }
                return dto;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public void updateMemberInfo(String memberEmail, String memberNickname, String memberPassword) {
        // 로그 출력
        log.info("Updating member info for email: {}", memberEmail);
        log.info("New Nickname: {}", memberNickname);

        // 이메일을 기준으로 회원 정보 조회
        MemberEntity member = memberRepository.findByMemberEmail(memberEmail)
                .orElseThrow(() -> new IllegalArgumentException("No member found with email: " + memberEmail));


        // 닉네임 업데이트
        if (memberNickname != null && !memberNickname.isEmpty()) {
            member.setNickname(memberNickname);
        }

        if (memberPassword != null && !memberPassword.isEmpty()) {
            member.setMemberPassword(passwordEncoder.encode(memberPassword));
        }

        // 업데이트된 회원 정보 저장
        memberRepository.save(member);
    }

    public void deleteMember(String memberEmail) {
        Optional<MemberEntity> member = memberRepository.findByMemberEmail(memberEmail);

        if (member.isPresent()) {
            // Delete the member
            memberRepository.delete(member.get());
            log.info("Member with email {} has been deleted.", memberEmail);
        } else {
            // Throw an exception if the member is not found
            throw new IllegalArgumentException("회원 정보를 찾을 수 없습니다.");
        }
    }
}