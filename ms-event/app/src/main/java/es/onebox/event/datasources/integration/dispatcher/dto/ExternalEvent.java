package es.onebox.event.datasources.integration.dispatcher.dto;

import java.io.Serializable;
import java.util.List;

public class ExternalEvent implements Serializable {

    private String id;
    private String name;
    private String inventoryId;
    private String description;
    private List<String> externalSessionIds;
    private List<String> childGroupIds;
    private String groupId;
    private String layoutId;
    private Boolean standalone;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(String inventoryId) {
        this.inventoryId = inventoryId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getExternalSessionIds() {
        return externalSessionIds;
    }

    public void setExternalSessionIds(List<String> externalSessionIds) {
        this.externalSessionIds = externalSessionIds;
    }

    public List<String> getChildGroupIds() {
        return childGroupIds;
    }

    public void setChildGroupIds(List<String> childGroupIds) {
        this.childGroupIds = childGroupIds;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getLayoutId() {
        return layoutId;
    }

    public void setLayoutId(String layoutId) {
        this.layoutId = layoutId;
    }

    public Boolean getStandalone() {
        return standalone;
    }

    public void setStandalone(Boolean standalone) {
        this.standalone = standalone;
    }
}
