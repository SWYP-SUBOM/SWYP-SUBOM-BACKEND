package swyp_11.ssubom.global.security.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class mainController {
    @GetMapping("/")
    @ResponseBody
    public String main() {
        return "main return";
    }
}
