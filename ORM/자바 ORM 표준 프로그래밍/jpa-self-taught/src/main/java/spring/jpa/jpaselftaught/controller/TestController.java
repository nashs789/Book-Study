package spring.jpa.jpaselftaught.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import spring.jpa.jpaselftaught.service.TestService;

@Controller
@AllArgsConstructor
public class TestController {

    private final TestService testService;

    @PostMapping("test")
    public String test() {
        testService.test();

        return "ok";
    }
}
