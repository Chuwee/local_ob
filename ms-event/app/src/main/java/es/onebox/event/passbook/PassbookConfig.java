package es.onebox.event.passbook;


import es.onebox.couchbase.annotations.Id;

import java.io.Serializable;
import java.util.List;

/**
 * Created by cgalindo on 15/01/2018.
 */
public class PassbookConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;
    private String name;

    private boolean external;
    private boolean needObfuscate;
    private boolean textLogoNeeded;
    private boolean activity;
    private boolean accessNeeded;
    private boolean spaceNeeded;
    private boolean infoNeeded;
    private boolean updateNeeded;
    private boolean accessTimeNeeded;
    private List<Field> secondaryFields;
    private List<Field> auxiliaryFields;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isExternal() {
        return external;
    }

    public void setExternal(boolean external) {
        this.external = external;
    }

    public boolean isNeedObfuscate() {
        return needObfuscate;
    }

    public void setNeedObfuscate(boolean needObfuscate) {
        this.needObfuscate = needObfuscate;
    }

    public boolean isTextLogoNeeded() {
        return textLogoNeeded;
    }

    public void setTextLogoNeeded(boolean textLogoNeeded) {
        this.textLogoNeeded = textLogoNeeded;
    }

    public boolean isActivity() {
        return activity;
    }

    public void setActivity(boolean activity) {
        this.activity = activity;
    }

    public boolean isAccessNeeded() {
        return accessNeeded;
    }

    public void setAccessNeeded(boolean accessNeeded) {
        this.accessNeeded = accessNeeded;
    }

    public boolean isAccessTimeNeeded() {
        return accessTimeNeeded;
    }

    public void setAccessTimeNeeded(boolean accessTimeNeeded) {
        this.accessTimeNeeded = accessTimeNeeded;
    }

    public List<Field> getSecondaryFields() {
        return secondaryFields;
    }

    public void setSecondaryFields(List<Field> secondaryFields) {
        this.secondaryFields = secondaryFields;
    }

    public List<Field> getAuxiliaryFields() {
        return auxiliaryFields;
    }

    public void setAuxiliaryFields(List<Field> auxiliaryFields) {
        this.auxiliaryFields = auxiliaryFields;
    }

    public boolean isSpaceNeeded() {
        return spaceNeeded;
    }

    public void setSpaceNeeded(boolean spaceNeeded) {
        this.spaceNeeded = spaceNeeded;
    }

    public boolean isInfoNeeded() {
        return infoNeeded;
    }

    public void setInfoNeeded(boolean infoNeeded) {
        this.infoNeeded = infoNeeded;
    }

    public boolean isUpdateNeeded() {
        return updateNeeded;
    }

    public void setUpdateNeeded(boolean updateNeeded) {
        this.updateNeeded = updateNeeded;
    }
}
