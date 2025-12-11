package es.onebox.mgmt.channels.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

public class MemberDatesFilter implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private List<AccessUsersDatesFilter> access;
    @JsonProperty("default_access")
    private ZonedDateTime defaultAccess;

    public List<AccessUsersDatesFilter> getAccess() { return access; }

    public void setAccess(List<AccessUsersDatesFilter> access) { this.access = access; }

    public ZonedDateTime getDefaultAccess() { return defaultAccess; }

    public void setDefaultAccess(ZonedDateTime defaultAccess) { this.defaultAccess = defaultAccess; }
}
