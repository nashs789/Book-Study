package spring.jpa.jpaselftaught.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import spring.jpa.jpaselftaught.entity.Test;
import spring.jpa.jpaselftaught.repository.TestRepository;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class TestService {

    private final TestRepository testRepository;

    public void test() {
        Test test = new Test();

        test.setName(UUID.randomUUID().toString());

        testRepository.save(test);

        List<Test> all = testRepository.findAll();

        for(Test test1 : all) {
            System.out.println(test1.getName());
        }
    }
}
