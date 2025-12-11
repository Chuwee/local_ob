package es.onebox.event.common;

public interface Pageable {

    Long getLimit();

    void setLimit(Long limit);

    Long getOffset();

    void setOffset(Long offset);
}
