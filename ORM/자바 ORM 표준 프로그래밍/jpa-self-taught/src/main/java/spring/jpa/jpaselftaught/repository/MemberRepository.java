package spring.jpa.jpaselftaught.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.jpa.jpaselftaught.entity.Member;

public interface MemberRepository extends JpaRepository<Member, String> {
}
