package es.flc.ie.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Model class to store the especial form for the block & unblock Incompatilities Engine REST Services
 *
 * @author VASS
 */
@XmlRootElement(name = "ieForm")
@JsonInclude(Include.NON_NULL)
public class IEForm {

    private Long centerID;
    private Map<Long, List<Long>> articleWithSessionsMap;
    private String orderCode;

    private String parentOrderCode;

    public IEForm() {
        this.articleWithSessionsMap = new HashMap<>();
    }

    public IEForm(Long centerID, Map<Long, List<Long>> articleWithSessionsMap) {
        this.centerID = centerID;
        this.articleWithSessionsMap = articleWithSessionsMap;
    }

    public IEForm(Long centerID, String orderCode) {
        this.centerID = centerID;
        this.orderCode = orderCode;
    }

    public Long getCenterID() {
        return centerID;
    }

    public void setCenterID(Long centerID) {
        this.centerID = centerID;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public Map<Long, List<Long>> getArticleWithSessionsMap() {
        return articleWithSessionsMap;
    }

    public String getParentOrderCode() {
        return parentOrderCode;
    }

    public void setParentOrderCode(String parentOrderCode) {
        this.parentOrderCode = parentOrderCode;
    }
}
