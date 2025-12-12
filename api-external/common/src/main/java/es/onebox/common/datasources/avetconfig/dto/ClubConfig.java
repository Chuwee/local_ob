package es.onebox.common.datasources.avetconfig.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Created by mmolinero on 28/05/18.
 */
public class ClubConfig extends BaseClubConfig {

    private String clubCode;
    private Integer entityId;

    private WSConnectionVersion wsConnectionVersion = WSConnectionVersion.ONE_DOT_X;
    private WSConnectionVersion wsSubscriberOperationsConnectionVersion = WSConnectionVersion.TWO_DOT_FOUR;
    private Boolean enableAvetCartConciliation;

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public String getClubCode() {
        return clubCode;
    }

    public void setClubCode(String clubCode) {
        this.clubCode = clubCode;
    }


    @Override
    public WSConnectionVersion getWsConnectionVersion() {
        return wsConnectionVersion;
    }

    @Override
    public void setWsConnectionVersion(WSConnectionVersion wsConnectionVersion) {
        this.wsConnectionVersion = wsConnectionVersion;
    }

    @Override
    public WSConnectionVersion getWsSubscriberOperationsConnectionVersion() {
        return wsSubscriberOperationsConnectionVersion;
    }

    @Override
    public void setWsSubscriberOperationsConnectionVersion(WSConnectionVersion wsSubscriberOperationsConnectionVersion) {
        this.wsSubscriberOperationsConnectionVersion = wsSubscriberOperationsConnectionVersion;
    }

    public Boolean getEnableAvetCartConciliation() {
        return enableAvetCartConciliation;
    }

    public void setEnableAvetCartConciliation(Boolean enableAvetCartConciliation) {
        this.enableAvetCartConciliation = enableAvetCartConciliation;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

}

