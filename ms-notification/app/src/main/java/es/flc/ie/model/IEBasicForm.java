package es.flc.ie.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "ieBasicForm")
@JsonInclude(Include.NON_NULL)
public class IEBasicForm {

    private Long centerID;
    private Long activityID;
    private List<Long> sessionIDs;

    public IEBasicForm() {
        sessionIDs = new ArrayList<Long>();
    }

    public IEBasicForm(Long centerID, Long activityID, List<Long> sessionIDs) {
        this.centerID = centerID;
        this.activityID = activityID;
        this.sessionIDs = sessionIDs;
    }

    public Long getCenterID() {
        return centerID;
    }

    public void setCenterID(Long centerID) {
        this.centerID = centerID;
    }

    public Long getActivityID() {
        return activityID;
    }

    public void setActivityID(Long activityID) {
        this.activityID = activityID;
    }

    public List<Long> getSessionIDs() {
        return sessionIDs;
    }
}
