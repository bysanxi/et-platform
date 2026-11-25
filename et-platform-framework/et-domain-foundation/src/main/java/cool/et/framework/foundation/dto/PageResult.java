package cool.et.framework.foundation.dto;

import cool.et.framework.commons.entity.Result;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Bai BangShun
 * @since 2025-03-13  15:35
 */
public class PageResult<T> extends Result{
    private static final long serialVersionUID = 1L;

    private long totalCount = 0;

    private long pageSize = 1;

    private long pageIndex = 1;

    private Collection<T> data;

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public long getPageSize() {
        if (pageSize < 1) {
            return 1;
        }
        return pageSize;
    }

    public void setPageSize(long pageSize) {
        if (pageSize < 1) {
            this.pageSize = 1;
        } else {
            this.pageSize = pageSize;
        }
    }

    public long getPageIndex() {
        if (pageIndex < 1) {
            return 1;
        }
        return pageIndex;
    }

    public void setPageIndex(long pageIndex) {
        if (pageIndex < 1) {
            this.pageIndex = 1;
        } else {
            this.pageIndex = pageIndex;
        }
    }

    public List<T> getData() {
        if (null == data) {
            return Collections.emptyList();
        }
        if (data instanceof List) {
            return (List<T>) data;
        }
        return new ArrayList<>(data);
    }

    public void setData(Collection<T> data) {
        this.data = data;
    }

    public long getTotalPages() {
        return this.totalCount % this.pageSize == 0 ? this.totalCount
                / this.pageSize : (this.totalCount / this.pageSize) + 1;
    }

    public boolean isEmpty() {
        return data == null || data.isEmpty();
    }

    public boolean isNotEmpty() {
        return !isEmpty();
    }

    public static PageResult buildSuccess() {
        PageResult result = new PageResult();
        result.setSuccessful(true);
        result.setCode(SUCCESS_CODE);
        return result;
    }

    public static PageResult buildFailure(int errCode, String errMessage) {
        PageResult result = new PageResult();
        result.setSuccessful(false);
        result.setCode(FAIL_CODE);
        result.setCode(errCode);
        result.setMessage(errMessage);
        return result;
    }

    public static <T> PageResult<T> of(long pageSize, long pageIndex) {
        PageResult<T> result = new PageResult<>();
        result.setSuccessful(true);
        result.setCode(SUCCESS_CODE);
        result.setData(Collections.emptyList());
        result.setTotalCount(0);
        result.setPageSize(pageSize);
        result.setPageIndex(pageIndex);
        return result;
    }

    public static <T> PageResult<T> of(Collection<T> data, long totalCount, long pageSize, long pageIndex) {
        PageResult<T> result = new PageResult<>();
        result.setSuccessful(true);
        result.setCode(SUCCESS_CODE);
        result.setData(data);
        result.setTotalCount(totalCount);
        result.setPageSize(pageSize);
        result.setPageIndex(pageIndex);
        return result;
    }
}
