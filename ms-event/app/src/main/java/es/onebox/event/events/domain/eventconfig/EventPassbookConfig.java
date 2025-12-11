package es.onebox.event.events.domain.eventconfig;

import java.io.Serializable;

import java.util.Map;

public class EventPassbookConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    private String individualPassbookTemplate;
    private String groupPassbookTemplate;
    private String individualInvitationPassbookTemplate;
    private String groupInvitationPassbookTemplate;
    private String sessionPackPassbookTemplate;
    private Map<String, String> stripImage;
    private Map<String, String> backgroundImage;
    private Map<String, String> thumbnailImage;
    private Map<String, String> title;
    private Map<String, String> additionalData1;
    private Map<String, String> additionalData2;
    private Map<String, String> additionalData3;

    public Map<String, String> getStripImage() {
        return stripImage;
    }

    public void setStripImage(Map<String, String> stripImage) {
        this.stripImage = stripImage;
    }

    public Map<String, String> getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(Map<String, String> backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public Map<String, String> getThumbnailImage() {
        return thumbnailImage;
    }

    public void setThumbnailImage(Map<String, String> thumbnailImage) {
        this.thumbnailImage = thumbnailImage;
    }

    public Map<String, String> getAdditionalData1() {
        return additionalData1;
    }

    public void setAdditionalData1(Map<String, String> additionalData1) {
        this.additionalData1 = additionalData1;
    }

    public Map<String, String> getAdditionalData2() {
        return additionalData2;
    }

    public void setAdditionalData2(Map<String, String> additionalData2) {
        this.additionalData2 = additionalData2;
    }

    public Map<String, String> getAdditionalData3() {
        return additionalData3;
    }

    public void setAdditionalData3(Map<String, String> additionalData3) {
        this.additionalData3 = additionalData3;
    }

    public Map<String, String> getTitle() {
        return title;
    }

    public void setTitle(Map<String, String> title) {
        this.title = title;
    }

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
