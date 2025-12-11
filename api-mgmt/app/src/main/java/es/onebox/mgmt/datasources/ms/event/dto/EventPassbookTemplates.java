package es.onebox.mgmt.datasources.ms.event.dto;

import java.io.Serializable;

public class EventPassbookTemplates implements Serializable {

    private String individualPassbookTemplate;
    private String groupPassbookTemplate;
    private String individualInvitationPassbookTemplate;
    private String groupInvitationPassbookTemplate;
    private String sessionPackPassbookTemplate;

    public String getIndividualPassbookTemplate() {
        return individualPassbookTemplate;
    }

    public void setIndividualPassbookTemplate(String individualPassbookTemplate) {
        this.individualPassbookTemplate = individualPassbookTemplate;
    }

    public String getGroupPassbookTemplate() {
        return groupPassbookTemplate;
    }

    public void setGroupPassbookTemplate(String groupPassbookTemplate) {
        this.groupPassbookTemplate = groupPassbookTemplate;
    }

    public String getIndividualInvitationPassbookTemplate() {
        return individualInvitationPassbookTemplate;
    }

    public void setIndividualInvitationPassbookTemplate(String individualInvitationPassbookTemplate) {
        this.individualInvitationPassbookTemplate = individualInvitationPassbookTemplate;
    }

    public String getGroupInvitationPassbookTemplate() {
        return groupInvitationPassbookTemplate;
    }

    public void setGroupInvitationPassbookTemplate(String groupInvitationPassbookTemplate) {
        this.groupInvitationPassbookTemplate = groupInvitationPassbookTemplate;
    }

    public String getSessionPackPassbookTemplate() {
        return sessionPackPassbookTemplate;
    }

    public void setSessionPackPassbookTemplate(String sessionPackPassbookTemplate) {
        this.sessionPackPassbookTemplate = sessionPackPassbookTemplate;
    }
}
