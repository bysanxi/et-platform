package cool.et.framework.coordination.leadership.common;

public interface LeaderSelectorFactory {
    LeaderSelector create(String lockId, String leaderId, LeaderSelectedCallback selected, Runnable removed);
}
