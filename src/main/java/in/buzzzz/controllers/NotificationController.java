package in.buzzzz.controllers;

import in.buzzzz.services.NotificationService;
import in.buzzzz.utils.ObjectUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jitendra on 26/9/15.
 */
@RestController
@RequestMapping(value = "/notification")
public class NotificationController {

    @Autowired
    NotificationService notificationService;

    @RequestMapping("/markRead")
    public Map markRead(String notificationId) {
        Map<String, Object> responseMap = new HashMap<String, Object>();
        if (ObjectUtils.isEmptyObject(notificationId) || !ObjectId.isValid(notificationId)) {
            responseMap.put("data", false);
            responseMap.put("status", 1);
            responseMap.put("error", "notificationId must not be null.");
        } else {
            responseMap.put("data", notificationService.markAsRead(notificationId));
            responseMap.put("status", 1);
            responseMap.put("message", "Success");
        }
        return responseMap;
    }
}
