package cool.et.framework.foundation.domain;

import java.io.Serializable;

public class IdentifiedDomainObject<T> extends AssertionConcern implements Serializable {

    private static final long serialVersionUID = 1L;

    private T id;

    protected IdentifiedDomainObject() {
        super();
    }

    public T getId() {
        return id;
    }

    protected void setId(T id) {
        this.id = id;
    }
}

