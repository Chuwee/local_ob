package es.onebox.event.common;

import java.util.Objects;

public class CommonIdResponse {

    private Integer id;

    public CommonIdResponse(Integer id) {
        this.id = id;
    }

    public CommonIdResponse() {
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommonIdResponse that = (CommonIdResponse) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "CommonIdResponse{" +
                "id=" + id +
                '}';
    }
}
