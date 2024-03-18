package spring.jpa.jpaselftaught;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import spring.jpa.jpaselftaught.entity.Member;
import spring.jpa.jpaselftaught.repository.MemberRepository;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@SpringBootTest
@Transactional
public class MemberTest {

    @Autowired
    MemberRepository memberRepository;

    @Test
    void 등록() {
        Member member = getMember();
        Member savedMem = memberRepository.saveAndFlush(member);

        Assertions.assertThat(member.getId())
                  .isEqualTo(savedMem.getId());
    }

    @Test
    void 수정() {
        Member member = getMember();
        memberRepository.saveAndFlush(member);  // 최소 한 개의 데이터 보장
        List<Member> members = memberRepository.findAll();

        Member foundMem1 = members.get(0);
        foundMem1.setAge(1234);

        Member foundMem2 = memberRepository.findById(foundMem1.getId()).get();

        Assertions.assertThat(foundMem1)
                  .isEqualTo(foundMem2);

        Assertions.assertThat(foundMem2.getAge())
                  .isEqualTo(1234);
    }

    private Member getMember() {
        return Member.builder()
                .id(UUID.randomUUID().toString())
                .age(new Random().nextInt())
                .build();
    }
}
