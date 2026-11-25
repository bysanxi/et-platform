package cool.et.framework.foundation.domain;

public class ConcurrencySafeEntity<T> extends Entity<T> {

    private static final long serialVersionUID = 1L;

    private int concurrencyVersion;

    protected ConcurrencySafeEntity() {
        super();
    }

    public int getConcurrencyVersion() {
        return concurrencyVersion;
    }

    public void setConcurrencyVersion(int aVersion) {
        this.concurrencyVersion = aVersion;
    }

}

