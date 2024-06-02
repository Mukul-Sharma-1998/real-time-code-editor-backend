package com.realtimeCodeEditorBackend.controller;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@Order(Ordered.LOWEST_PRECEDENCE)
public class ViewController {

    @GetMapping(value = "/editor/{uuid}")
    public String forward(@PathVariable String uuid) {
        return "forward:/index.html";
    }
}

