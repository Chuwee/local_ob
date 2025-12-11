package es.onebox.mgmt.datasources.ms.venue.dto.template;

public class TagWithGroup extends BaseCodeTag {

    private static final long serialVersionUID = 1L;

    private String groupCode;
    private Long groupId;

    public TagWithGroup() {
    }

    public TagWithGroup(Long id, Long groupId, String groupCode) {
        super(id);
        this.groupId = groupId;
        this.groupCode = groupCode;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }
}
