package in.buzzzz.controllers;

import in.buzzzz.services.HttpBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jitendra on 26/9/15.
 */
@RestController
@RequestMapping("/api")
public class TestController {
    @Autowired
    HttpBuilder builder;

    @RequestMapping(value = "/home")
    public String index(HttpServletResponse response) {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("X-Auth-Token", "asdfsadfsadfsadfsadf");
        String jsonString = builder.httpPostWithHeaders("http://10.1.13.142:9090/auth/verification", headers);
        response.setContentType("application/json");
        return jsonString;
    }
}
