package in.buzzzz.repositories;

import in.buzzzz.domain.Chat;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author jitendra on 26/9/15.
 */
public interface ChatRepository extends MongoRepository<Chat, String> {
}
