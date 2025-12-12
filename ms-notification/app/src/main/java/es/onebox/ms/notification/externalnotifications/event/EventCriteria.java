package es.onebox.ms.notification.externalnotifications.event;

import java.io.Serializable;
import java.util.List;

public class EventCriteria implements Serializable{


    public static final Integer READY_EVENT = 3;

    private Integer state;
    private List<CommunicationElement> communicationEvent;
    private List<CommunicationElement> communicationOthers;
    private List<CommunicationElement> communicationSubtitle1;
    private List<CommunicationElement> communicationSubtitle2;

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public List<CommunicationElement> getCommunicationEvent() {
        return communicationEvent;
    }

    public void setCommunicationEvent(List<CommunicationElement> communicationEvent) {
        this.communicationEvent = communicationEvent;
    }

    public List<CommunicationElement> getCommunicationOthers() {
        return communicationOthers;
    }

    public void setCommunicationOthers(List<CommunicationElement> communicationOthers) {
        this.communicationOthers = communicationOthers;
    }

    public List<CommunicationElement> getCommunicationSubtitle1() {
        return communicationSubtitle1;
    }

    public void setCommunicationSubtitle1(List<CommunicationElement> communicationSubtitle1) {
        this.communicationSubtitle1 = communicationSubtitle1;
    }

    public List<CommunicationElement> getCommunicationSubtitle2() {
        return communicationSubtitle2;
    }

    public void setCommunicationSubtitle2(List<CommunicationElement> communicationSubtitle2) {
        this.communicationSubtitle2 = communicationSubtitle2;
    }
}
