package com.javanc.controller.admin;

import com.javanc.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DashboardController {
    @Autowired
    UserRepository userRepository;

//    @GetMapping("admin/user")
}
