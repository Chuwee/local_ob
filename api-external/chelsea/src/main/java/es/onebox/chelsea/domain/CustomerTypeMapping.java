package es.onebox.chelsea.domain;

import java.util.List;

public class CustomerTypeMapping {

    private List<String> add;
    private List<String> remove;


    public List<String> getAdd() {
        return add;
    }

    public void setAdd(List<String> add) {
        this.add = add;
    }

    public List<String> getRemove() {
        return remove;
    }

    public void setRemove(List<String> remove) {
        this.remove = remove;
    }
}
