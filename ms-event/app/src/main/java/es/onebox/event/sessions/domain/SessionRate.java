package es.onebox.event.sessions.domain;

public class SessionRate {

    private Long sessionId;
    private Integer rateId;
    private String rateName;
    private Boolean defaultRate;
    private Byte restrictiveAccess;
    private Integer position;

    public SessionRate() {
    }

    public SessionRate(Long sessionId, Integer rateId, Boolean defaultRate) {
        this.sessionId = sessionId;
        this.rateId = rateId;
        this.defaultRate = defaultRate;
    }

    public SessionRate(Long sessionId, Integer rateId, String rateName, Boolean defaultRate) {
        this.sessionId = sessionId;
        this.rateId = rateId;
        this.rateName = rateName;
        this.defaultRate = defaultRate;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Integer getRateId() {
        return rateId;
    }

    public void setRateId(Integer rateId) {
        this.rateId = rateId;
    }

    public String getRateName() {
        return rateName;
    }

    public void setRateName(String rateName) {
        this.rateName = rateName;
    }

    public Boolean getDefaultRate() {
        return defaultRate;
    }

    public void setDefaultRate(Boolean defaultRate) {
        this.defaultRate = defaultRate;
    }

    public Byte getRestrictiveAccess() {
        return restrictiveAccess;
    }

    public void setRestrictiveAccess(Byte restrictiveAccess) {
        this.restrictiveAccess = restrictiveAccess;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }
}
