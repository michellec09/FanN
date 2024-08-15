package edu.tec.azuay.faan.controller.secondary;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Hidden
@Controller
public class ViewHomeController {

    @RequestMapping(value = {"/", "/index", "/home"}, method = RequestMethod.GET)
    public String index() {
        return "index";
    }

    @RequestMapping(value = "/{path:(?!faan-websocket).*}", method = RequestMethod.GET)
    public String catchAll() {
        return "index";
    }
}
