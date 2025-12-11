package es.onebox.event.events.postbookingquestions.dao;

import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.core.Key;
import es.onebox.couchbase.dao.AbstractCouchDao;
import es.onebox.event.events.postbookingquestions.domain.PostBookingQuestion;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@CouchRepository(prefixKey = PostBookingQuestionCouchDao.PREFIX,
        bucket = PostBookingQuestionCouchDao.ONEBOX_OPERATIVE,
        scope = PostBookingQuestionCouchDao.SCOPE,
        collection = PostBookingQuestionCouchDao.COLLECTION)
public class PostBookingQuestionCouchDao extends AbstractCouchDao<PostBookingQuestion> {

    public static final String ONEBOX_OPERATIVE = "onebox-operative";
    public static final String SCOPE = "forms";

    public static final String COLLECTION = "post-booking-question";
    public static final String PREFIX = "postBookingQuestion";

    public List<PostBookingQuestion> bulkGet(List<String> postBookingQuestionIds) {
        return bulkGet(getEventKeys(postBookingQuestionIds));
    }

    private static List<Key> getEventKeys(List<String> postBookingQuestionIds) {

        return postBookingQuestionIds.stream().map(sId -> {
            Key key = new Key();
            key.setKey(new String[]{sId});
            return key;
        }).collect(Collectors.toList());
    }
}
