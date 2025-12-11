package es.onebox.event.events.dao;

import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.core.Key;
import es.onebox.couchbase.dao.AbstractCouchDao;
import es.onebox.event.events.domain.TierConfig;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@CouchRepository(prefixKey = TierConfigCouchDao.TIER_CONFIG, bucket = TierConfigCouchDao.ONEBOX_OPERATIVE)
public class TierConfigCouchDao extends AbstractCouchDao<TierConfig> {

    public static final String ONEBOX_OPERATIVE = "onebox-operative";
    public static final String TIER_CONFIG = "tierConfig";

    public TierConfig getOrInitTierConfig(Long tierIdId) {
        TierConfig priceTypeConfig = super.get(tierIdId.toString());
        if (priceTypeConfig == null) {
            priceTypeConfig = new TierConfig();
            priceTypeConfig.setTierId(tierIdId);
        }
        return priceTypeConfig;
    }

    public List<TierConfig> bulkGet(List<Long> tierIds) {
        return bulkGet(getTierKeys(tierIds));
    }

    public boolean exists(Long tierIdId) {
        return super.exists(tierIdId.toString());
    }

    public void remove(Long tierIdId) {
        super.remove(tierIdId.toString());
    }

    private static List<Key> getTierKeys(List<Long> tierIds) {
        return tierIds.stream().map(id -> {
            Key key = new Key();
            key.setKey(new String[]{String.valueOf(id)});
            return key;
        }).collect(Collectors.toList());
    }

}
