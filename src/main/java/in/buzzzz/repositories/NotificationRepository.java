package in.buzzzz.repositories;

import in.buzzzz.domain.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author jitendra on 26/9/15.
 */
public interface NotificationRepository extends MongoRepository<Notification, String> {
}