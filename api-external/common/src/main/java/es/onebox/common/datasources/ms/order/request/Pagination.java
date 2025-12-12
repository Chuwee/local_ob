package es.onebox.common.datasources.ms.order.request;

import es.onebox.common.datasources.ms.order.exception.InvalidPageException;
import es.onebox.common.datasources.ms.order.exception.InvalidPageSizeException;
import es.onebox.common.datasources.ms.order.exception.PaginationSizeTooLargeException;

import java.io.Serializable;

public class Pagination implements Serializable {
    private static final long serialVersionUID = -2684371062403126501L;

    public static final Integer MAX_RESULT = 500;
    public static final Integer DEFAULT_RESULT = 50;
    public static final Integer MIN_RESULT = 1;
    private Integer pageSize;
    private Integer page;

    public Pagination() {
        this.pageSize = DEFAULT_RESULT;
        this.page = 0;
    }
    public Pagination(Integer page) {
        this.setPage(page);
        this.pageSize = DEFAULT_RESULT;
    }

    public Pagination(Integer page, Integer pageSize) {
        this.setPage(page);
        this.setPageSize(pageSize);
    }

    public Integer getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(Integer pageSize) {
        if (pageSize == null) {
            this.pageSize = DEFAULT_RESULT;
        } else {
            if (pageSize > MAX_RESULT) {
                throw new PaginationSizeTooLargeException("Page size: " + pageSize);
            }

            if (pageSize < MIN_RESULT) {
                throw new InvalidPageSizeException("Invalid Page size: " + pageSize);
            }

            this.pageSize = pageSize;
        }

    }

    public Integer getPage() {
        return this.page;
    }

    public void setPage(Integer page) {
        if (page == null) {
            this.page = 0;
        } else {
            if (page < 0) {
                throw new InvalidPageException("Page: " + page);
            }

            this.page = page;
        }

    }

}
