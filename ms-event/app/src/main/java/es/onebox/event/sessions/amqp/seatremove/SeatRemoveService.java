package es.onebox.event.sessions.amqp.seatremove;

import es.onebox.message.broker.producer.queue.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

public class SeatRemoveService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SeatRemoveService.class);

    @Autowired
    @Qualifier("seatRemoveProducer")
    private DefaultProducer seatRemoveProducer;

    public void removeSeats(Integer sessionId) {
        SeatRemoveMessage seatRemoveMessage = new SeatRemoveMessage();
        seatRemoveMessage.setIdSesion(sessionId);
        this.sendMessage(seatRemoveMessage);
    }

    public void removeSeats(int idSesion, boolean esAbono, Integer idEstado, Integer idRazonBloqueo,
                            List<Integer> sessionsId, Byte tipoAbono) {
        SeatRemoveMessage seatRemoveMessage = new SeatRemoveMessage();

        seatRemoveMessage.setIdSesion(idSesion);
        seatRemoveMessage.setIdEstado(idEstado == null ? 0 : idEstado);
        seatRemoveMessage.setIdRazonBloqueo(idRazonBloqueo == null ? 0 : idRazonBloqueo);
        seatRemoveMessage.setEsAbono(esAbono);
        seatRemoveMessage.setTipoAbono(tipoAbono);
        seatRemoveMessage.getIdSesiones().addAll(sessionsId);

        this.sendMessage(seatRemoveMessage);
    }

    private void sendMessage(SeatRemoveMessage seatRemoveMessage) {
        try {
            seatRemoveProducer.sendMessage(seatRemoveMessage);
        } catch (Exception e) {
            LOGGER.warn("[AMQP CLIENT] SeatRemoveService Message could not be send", e);
        }
    }

}
