package com.itfenbao.main.web;

import com.itfenbao.main.repository.UserRepository;
import org.pf4j.PluginManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping(path = "/user")
public class UserController {

    @Resource
    UserRepository repository;

    @Resource
    PluginManager pluginManager;

    @GetMapping
    @RequestMapping(path = {"", "/"})
    public String index() {
        repository.findById(1L);
        return "user test";
    }

    @GetMapping
    @RequestMapping("/stop")
    public String stop(String id) {
        pluginManager.stopPlugin(id);
        return "stop plugin " + id;
    }

    @GetMapping
    @RequestMapping("/start")
    public String start(String id) {
        pluginManager.startPlugin(id);
        return "start plugin " + id;
    }


}
