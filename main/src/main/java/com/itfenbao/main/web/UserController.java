package com.itfenbao.main.web;

import com.itfenbao.main.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

@Controller
@RequestMapping(path = "/user")
public class UserController {

    @Resource
    UserRepository repository;

    @GetMapping({"", "/"})
    public ResponseEntity index() {
        repository.findById(1L);
        return ResponseEntity.ok("user test");
    }

    @GetMapping("/list")
    public String list(Model model) {
        return "list";
    }

}
