package spring.jpa.jpaselftaught.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.jpa.jpaselftaught.entity.Test;

public interface TestRepository extends JpaRepository<Test, Long> {
}
