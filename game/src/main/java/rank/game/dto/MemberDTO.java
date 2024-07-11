package rank.game.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import rank.game.entity.MemberEntity;

@Getter
@Setter
@NoArgsConstructor //기본생성자를 자동으로 만들어준다
@ToString
// DTO는 회원정보에 필요한 내용을 필드로 정의
public class MemberDTO {
    private Long num;
    private Long memberNum;
    private String memberEmail;
    private String memberPassword;
    private String memberName;
    private String nickname;
    private boolean isAdmin;


    public static MemberDTO fromEntity(MemberEntity memberEntity) {
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setMemberNum(memberEntity.getNum()); // 이 부분 수정
        memberDTO.setMemberEmail(memberEntity.getMemberEmail());
        memberDTO.setMemberPassword(memberEntity.getMemberPassword());
        memberDTO.setMemberName(memberEntity.getMemberName());
        memberDTO.setNickname(memberEntity.getNickname());

        return memberDTO;
    }


}

