package com.itfenbao.main.web;

import org.pf4j.PluginManager;
import org.pf4j.update.UpdateManager;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/plugins")
public class PluginMgrController {

    @Resource
    @Lazy
    PluginManager pluginManager;

    @Resource
    @Lazy
    UpdateManager updateManager;

    @GetMapping("/stop")
    public String stop(String id) {
        pluginManager.stopPlugin(id);
        return "stop plugin " + id;
    }

    @GetMapping("/start")
    public String start(String id) {
        pluginManager.startPlugin(id);
        return "start plugin " + id;
    }

    @GetMapping("/check_update")
    public String checkUpdate(String id) {
        return "hasUpdate" + updateManager.hasPluginUpdate(id);
    }
}
