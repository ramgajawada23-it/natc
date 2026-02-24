package com.example.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Home Controller
 * Handles main navigation and home page
 */
@Controller
@Slf4j
public class HomeController {

    /**
     * Home page - Dashboard
     */
    @GetMapping("/")
    public String home() {
        log.debug("Accessing home page");
        return "index";
    }
}
