package io.github.tbarissatir.profilelink.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/p/{id}")
    public String profilePage() {
        return "forward:/profile.html";
    }
}