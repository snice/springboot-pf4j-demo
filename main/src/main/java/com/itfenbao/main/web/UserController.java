package com.itfenbao.main.web;

import cn.hutool.core.date.DateUtil;
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

    public UserController() {
        System.out.println("UserController");
    }

    @Resource
    UserRepository repository;

    @GetMapping({"", "/"})
    public ResponseEntity index() {
        repository.findById(1L);
        return ResponseEntity.ok("user test" + DateUtil.now());
    }

    @GetMapping("/list")
    public String list(Model model) {
        return "list";
    }

    @GetMapping("/list1")
    public String list1(Model model) {
        return "list1";
    }

}
