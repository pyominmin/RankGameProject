package rank.game.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rank.game.dto.MemberDTO;
import rank.game.entity.MemberEntity;
import rank.game.repository.MemberRepository;
import rank.game.repository.VoteRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final VoteRepository voteRepository;


    // 회원가입
    public void save(MemberDTO memberDTO, String memberPassword) {
        // 모든 회원은 기본적으로 "USER" 권한을 가짐
        memberDTO.setRole("ROLE_USER");
        memberDTO.setMemberPassword(passwordEncoder.encode(memberPassword));

        // 특정 아이디와 비밀번호를 가진 회원에게 "ADMIN" 권한 부여
        if ("admin@admin.com".equals(memberDTO.getMemberEmail()) && "123456789".equals(memberPassword)) {
            memberDTO.setRole("ROLE_ADMIN");
        }

        MemberEntity memberEntity = MemberEntity.toMemberEntity(memberDTO);
        memberRepository.save(memberEntity);
    }

    // 어드민 계정 생성
    public void createAdminAccount() {
        if (!memberRepository.existsByMemberEmail("admin@admin.com")) {
            MemberDTO adminDTO = new MemberDTO();
            adminDTO.setMemberEmail("admin@admin.com");
            adminDTO.setMemberPassword("123456789");
            adminDTO.setMemberName("Admin");
            adminDTO.setNickname("Admin");
            adminDTO.setRole("ROLE_ADMIN");
            save(adminDTO, "123456789");
            log.info("Admin account created: admin@admin.com");
        }
    }

    // 이메일 중복 확인
    public boolean emailExists(String memberEmail) {
        return memberRepository.existsByMemberEmail(memberEmail);
    }

    // 닉네임 중복 확인
    public boolean nicknameExists(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }

    // 로그인
    public MemberDTO login(MemberDTO memberDTO) {
        Optional<MemberEntity> byMemberEmail = memberRepository.findByMemberEmail(memberDTO.getMemberEmail());
        if (byMemberEmail.isPresent()) {
            MemberEntity memberEntity = byMemberEmail.get();
            String encodedPassword = memberEntity.getMemberPassword();
            if (passwordEncoder.matches(memberDTO.getMemberPassword(), encodedPassword)) {
                return MemberDTO.fromEntity(memberEntity);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @Transactional
    public void updateMemberInfo(String memberEmail, String memberNickname, String memberPassword) {
        // 이메일을 기준으로 회원 정보 조회
        MemberEntity member = memberRepository.findByMemberEmail(memberEmail)
                .orElseThrow(() -> new IllegalArgumentException("No member found with email: " + memberEmail));

        String oldNickname = member.getNickname();

        // 닉네임 업데이트
        if (memberNickname != null && !memberNickname.isEmpty()) {
            // 닉네임 중복 확인
            if (memberRepository.existsByNickname(memberNickname)) {
                throw new IllegalArgumentException("중복된 닉네임입니다.");
            }
            member.setNickname(memberNickname);

            // 투표 엔티티의 닉네임 업데이트
            voteRepository.updateNickname(oldNickname, memberNickname);
        }

        // 비밀번호 업데이트
        if (memberPassword != null && !memberPassword.isEmpty()) {
            member.setMemberPassword(passwordEncoder.encode(memberPassword));
        }

        // 업데이트된 회원 정보 저장
        memberRepository.save(member);
    }

    //회원탈퇴
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

    public Page<MemberDTO> findAllMembers(Pageable pageable) {
        Page<MemberEntity> memberEntities = memberRepository.findAll(pageable);
        return memberEntities.map(MemberDTO::fromEntity);
    }

    // 권한 변경
    public boolean changeMemberRole(Long memberId, String newRole) {
        Optional<MemberEntity> memberOptional = memberRepository.findById(memberId);
        if (memberOptional.isPresent()) {
            MemberEntity member = memberOptional.get();
            member.setRole(newRole);
            memberRepository.save(member);
            return true;
        }
        return false;
    }
}

    // 일반 회원 100명 생성
//    public void createTestUsers() {
//        List<MemberDTO> users = new ArrayList<>();
//        for (int i = 1; i <= 100; i++) {
//            MemberDTO userDTO = new MemberDTO();
//            userDTO.setMemberEmail("user" + i + "@example.com");
//            userDTO.setMemberPassword("123456789");
//            userDTO.setMemberName("User" + i);
//            userDTO.setNickname("user" + i);
//            userDTO.setRole("ROLE_USER");
//            save(userDTO, "123456789");
//            log.info("100 test users created");
//        }
//    }
