package heavysnow.heath.service;

import heavysnow.heath.domain.Member;
import heavysnow.heath.dto.MemberDto;
import heavysnow.heath.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public Member createUser(MemberDto dto){
        return memberRepository.save(dto.toEntity());
    }

    @Transactional
    public Member editMember(String userName, String nickName, String userStatusMessage, String profileImgUrl) {
        Member entity = memberRepository.findByUsername(userName).orElse(null);
        if (entity == null){
            System.out.println("회원정보가 없습니다.");
        }
        entity.update(nickName, userStatusMessage, profileImgUrl);
        return memberRepository.findByUsername("userName").orElse(null);
    }
}