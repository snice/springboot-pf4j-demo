package com.itfenbao.plugin1;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.sql.DataSource;

@RestController
public class PluginController {

    @Resource
    DataSource dataSource;

    @GetMapping("/abc")
    public Object home() {
        return "OK" + dataSource;
    }

}
