package cool.et.framework.foundation.domain;

import static cool.et.framework.foundation.domain.ChangingStatus.*;

public class Entity<T> extends IdentifiedDomainObject<T> {

    private static final long serialVersionUID = 1L;

    protected ChangingStatus changingStatus = NEW;

    protected Entity() {
        super();
    }

    public ChangingStatus changingStatus() {
        return changingStatus;
    }

    /**
     * <blockquote><pre>
     * theSkill.setLevel(level)
     *      .setDuration(duration)
     *      .setLastUpdatedBy(userId)
     *      .setLastUpdatedAt(LocalDateTime.now())
     *      .toUpdate(); //设置修改状态
     * </pre></blockquote>
     */
    public void toUpdate() {
        this.changingStatus = UPDATED;
    }

    public void toDelete() {
        this.changingStatus = DELETED;
    }

    public void toNew() {
        this.changingStatus = NEW;
    }

    public void toUnChang() {
        this.changingStatus = UNCHANGED;
    }

}
