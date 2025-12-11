package es.onebox.event.sessions.amqp.sessionclone;

import es.onebox.core.utils.common.CommonUtils;
import es.onebox.event.sessions.dto.CloneSessionDTO;
import es.onebox.event.sessions.dto.SeatDeleteStatus;
import es.onebox.message.broker.producer.queue.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class SessionCloneService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SessionCloneService.class);

    @Autowired
    @Qualifier("sessionCloneProducer")
    private DefaultProducer sessionCloneProducer;

    public void cloneSession(Long sourceSessionId, Long targetSessionId, CloneSessionDTO cloneData) {
        SessionCloneMessage sessionCloneMessage = new SessionCloneMessage();

        sessionCloneMessage.setIdSesionOrigen(sourceSessionId.intValue());
        sessionCloneMessage.setIdSesionDestino(targetSessionId.intValue());
        if (cloneData.getTargetBlockingReasonId() != null) {
            sessionCloneMessage.setIdEstado(SeatDeleteStatus.LOCKED.getId());
            sessionCloneMessage.setIdRazonBloqueo(cloneData.getTargetBlockingReasonId().intValue());
        } else if (CommonUtils.isTrue(cloneData.getTargetFreeStatus())) {
            sessionCloneMessage.setIdEstado(SeatDeleteStatus.FREE.getId());
            sessionCloneMessage.setIdRazonBloqueo(0);
        }

        sendMessage(sessionCloneMessage);
    }

    private void sendMessage(SessionCloneMessage sessionCloneMessage) {
        try {
            sessionCloneProducer.sendMessage(sessionCloneMessage);
        } catch (Exception e) {
            LOGGER.warn("[AMQP CLIENT] Session clone could not be send, "
                    + "sessionIdOrigin: " + sessionCloneMessage.getIdSesionOrigen()
                    + ", sessionIdFinal: " + sessionCloneMessage.getIdSesionDestino(), e);
        }
    }
}
