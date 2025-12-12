package es.onebox.fever.converter;

import es.onebox.common.datasources.ms.event.dto.SessionDateDTO;
import es.onebox.common.datasources.webhook.dto.fever.session.SessionDateFeverDTO;

public class SessionConverter {

  public static SessionDateFeverDTO mapSessionDate(SessionDateDTO date) {

    if (date == null) {
      return null;
    }

    SessionDateFeverDTO sessionDate = new SessionDateFeverDTO();

    sessionDate.setAdmissionEnd(date.getAdmissionEnd());
    sessionDate.setBookingsEnd(date.getBookingsEnd());
    sessionDate.setEnd(date.getEnd());
    sessionDate.setSalesEnd(date.getSalesEnd());
    sessionDate.setStart(date.getStart());
    sessionDate.setSalesStart(date.getSalesStart());
    sessionDate.setAdmissionStart(date.getAdmissionStart());
    sessionDate.setBookingsStart(date.getBookingsStart());
    sessionDate.setChannelPublication(date.getChannelPublication());

    return sessionDate;
  }

}
