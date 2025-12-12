package es.onebox.ms.notification.ie.utils;

/**
 * Created by joandf on 18/03/2015.
 */
public class EndPointHolder {

    private Integer entityId;
    private Integer endPointType;
    private String endPointUrl;

    public EndPointHolder(Integer entityId, Integer endPointType, String endPointUrl) {
        this.entityId = entityId;
        this.endPointType = endPointType;
        this.endPointUrl = endPointUrl;
    }

    public String getEndPointUrl() {
        return endPointUrl;
    }

    public void setEndPointUrl(String endPointUrl) {
        this.endPointUrl = endPointUrl;
    }

    public Integer getEndPointType() {
        return endPointType;
    }

    public void setEndPointType(Integer endPointType) {
        this.endPointType = endPointType;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public static String generateHashCode(Integer entityId, Integer endPointType) {
        return entityId + "_" + endPointType;
    }
}
