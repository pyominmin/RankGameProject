package rank.game.entity;
// 테이블 역할

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import rank.game.dto.MemberDTO;

@Entity
@Setter
@Getter
@ToString
@Table(name = "membership")
//@SequenceGenerator(
//        name = "PLTOO_SEQ_GENERATOR",
//        sequenceName = "PLTOO_SEQ", //매핑할 데이터베이스 시퀀스 이름
//        initialValue = 1, allocationSize = 5)//낮은 값으로 설정. 시퀀스 번호가 낭비 방지
public class MemberEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "num", nullable = false)
    private Long num;

    @Column(name = "email", length = 50, unique = true)
    private String memberEmail;

    @Column(name = "pwd", length = 255, nullable = false)
    private String memberPassword;

    @Column(name = "name", length = 10, nullable = false)
    private String memberName;

    @Column(name = "nickname", length = 8)
    private String nickname;


    // Additional methods for password verification and equality checks
    public boolean isCorrectPassword(String inputPassword) {
        // Placeholder method, needs real implementation based on your password management logic
        return this.memberPassword.equals(inputPassword);
    }

//    public byte[] getSalt() {
//        // Placeholder method, adjust as per actual salt management
//        return new byte[16]; // Example, return actual salt used in password hashing
//    }

    public String getEncryptedPassword() {
        return this.memberPassword;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MemberEntity) {
            MemberEntity other = (MemberEntity) obj;
            return this.num != null && this.num.equals(other.getNum());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return 31 * (num != null ? num.hashCode() : 0);
    }

    public static MemberEntity toMemberEntity(MemberDTO memberDTO) {
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setNum(memberDTO.getNum());
        memberEntity.setMemberEmail(memberDTO.getMemberEmail());
        memberEntity.setMemberPassword(memberDTO.getMemberPassword());
        memberEntity.setMemberName(memberDTO.getMemberName());
        memberEntity.setNickname(memberDTO.getNickname());
        return memberEntity;
    }

}

