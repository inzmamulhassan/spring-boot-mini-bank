package com.example.accountService.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/account")
public class HomeController {
       @GetMapping
        public String home(@RequestHeader("loggedInUser") String loggedInUser) {
           System.out.println("Logged in user: " + loggedInUser);
            return "Welcome to Account Service";
        }
}
