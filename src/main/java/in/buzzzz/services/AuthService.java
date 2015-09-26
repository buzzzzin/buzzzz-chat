package in.buzzzz.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static in.buzzzz.utils.ObjectUtils.isNotEmptyMap;
import static in.buzzzz.utils.ObjectUtils.isNotEmptyObject;

/**
 * @author jitendra on 26/9/15.
 */
@Service
public class AuthService {

    Logger logger = Logger.getLogger(getClass().getName());

    @Value("${api.server.domain}")
    String restApiDomain;
    @Value("${api.endpoint.token-valid}")
    String tokenValidEndpoint;
    @Value("${api.header.auth-token}")
    String authTokenHeader;
    @Value("${api.header.app-key}")
    String appKey;
    @Value("${api.header.app-secret-value}")
    String appKeyValue;
    @Autowired
    HttpBuilder builder;
    @Autowired
    ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        Assert.notNull(restApiDomain);
        Assert.notNull(tokenValidEndpoint);
        Assert.notNull(authTokenHeader);
    }

    public boolean isValidToken(String token) {
        Map<String, String> httpHeaders = new HashMap<String, String>();
        String tokenValidUrl = restApiDomain.concat(tokenValidEndpoint);
        logger.info(String.format("Token validation URL prepared -- %s --", tokenValidUrl));
        httpHeaders.put(authTokenHeader, token);
        httpHeaders.put(appKey, appKeyValue);
        String jsonString = builder.httpPostWithHeaders(tokenValidUrl, httpHeaders);
        logger.info(String.format("Token validation Response received -- %s --", jsonString));
        if (isNotEmptyObject(jsonString)) {
            Map<String, Object> responseObject = null;
            try {
                responseObject = objectMapper.readValue(jsonString, HashMap.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            logger.info(String.format("Response map prepared -- %s --", responseObject));
            if (isNotEmptyMap(responseObject) && responseObject.containsKey("data")) {
                return (Boolean) responseObject.get("data");
            }
        }
        return false;
    }
}
