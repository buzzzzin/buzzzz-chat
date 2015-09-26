package in.buzzzz.controllers;

import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jitendra on 26/9/15.
 */
@RestController
public class TestController {
    @Autowired
    CloseableHttpAsyncClient closeableHttpAsyncClient;

    @RequestMapping("/index")
    public Map index() {
        Map m = new HashMap();
        m.put("a", "b");
        return m;
    }
}
