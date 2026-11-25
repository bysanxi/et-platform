package cool.et.framework.foundation.dto;

import cool.et.framework.commons.entity.Result;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Response with batch record to return,
 * usually use in conditional query
 * <p/>
 * Created by Danny.Lee on 2017/11/1.
 */
@Getter
@Setter
public class MultiResult<T> extends Result {

    private static final long serialVersionUID = 1L;

    private Collection<T> data;

    public List<T> getData() {
        if (null == data) {
            return Collections.emptyList();
        }
        if (data instanceof List) {
            return (List<T>) data;
        }
        return new ArrayList<>(data);
    }

    public boolean isEmpty() {
        return data == null || data.isEmpty();
    }

    public boolean isNotEmpty() {
        return !isEmpty();
    }

    public static MultiResult<Void> buildSuccess() {
        MultiResult<Void> response = new MultiResult<>();
        response.setSuccessful(true);
        response.setCode(SUCCESS_CODE);
        return response;
    }

    public static MultiResult<Void> buildFailure(int errCode, String errMessage) {
        MultiResult<Void> response = new MultiResult<>();
        response.setSuccessful(false);
        response.setCode(FAIL_CODE);
        response.setCode(errCode);
        response.setMessage(errMessage);
        return response;
    }

    public static <T> MultiResult<T> of(Collection<T> data) {
        MultiResult<T> response = new MultiResult<>();
        response.setSuccessful(true);
        response.setCode(SUCCESS_CODE);
        response.setData(data);
        return response;
    }

}