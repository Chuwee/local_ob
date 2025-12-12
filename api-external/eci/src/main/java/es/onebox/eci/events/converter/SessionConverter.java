package es.onebox.eci.events.converter;

import es.onebox.common.datasources.catalog.dto.session.ChannelSession;
import es.onebox.common.datasources.catalog.dto.common.Venue;
import es.onebox.eci.events.dto.Location;
import es.onebox.eci.events.dto.Session;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SessionConverter {
    private SessionConverter() {
    }

    public static List<Session> convert(List<ChannelSession> sessions) {
        if (sessions == null) {
            return Collections.emptyList();
        }

        return sessions
                .stream()
                .map(SessionConverter::convert)
                .collect(Collectors.toList());
    }

    public static Session convert(ChannelSession channelSession) {
        Session session = new Session();

        session.setIdentifier(String.valueOf(channelSession.getId()));
        session.setName(channelSession.getName());
        session.setStart(channelSession.getDate().getStart());
        session.setLocation(getLocation(channelSession.getVenue()));

        return session;
    }

    private static Location getLocation(Venue venue) {
        Location location = new Location();
        location.setIdentifier(String.valueOf(venue.getId()));
        return location;
    }
}
