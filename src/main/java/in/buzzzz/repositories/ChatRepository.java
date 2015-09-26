package in.buzzzz.repositories;

import in.buzzzz.domain.Chat;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @author jitendra on 26/9/15.
 */
public interface ChatRepository extends MongoRepository<Chat, String> {
    List<Chat> findAllByChannel(String channel, Pageable pageable);
}
