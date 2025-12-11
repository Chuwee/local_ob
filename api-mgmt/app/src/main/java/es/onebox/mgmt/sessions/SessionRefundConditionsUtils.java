package es.onebox.mgmt.sessions;

import es.onebox.mgmt.datasources.ms.event.dto.session.SessionConditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SessionRefundConditionsUtils {

    private SessionRefundConditionsUtils() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static List<Long> getSessionIds(final Map<Long, SessionConditions> sessionsConditionsMap){
        return Optional.ofNullable(sessionsConditionsMap)
                .map(Map::keySet)
                .map(ks -> new ArrayList(ks))
                .orElse(null);
    }
}
