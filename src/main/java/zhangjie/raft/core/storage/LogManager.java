package zhangjie.raft.core.storage;

import zhangjie.raft.core.entity.LogId;

/**
 * @Author zhangjie
 * @Date 2020/5/31 22:58
 **/
public interface LogManager {

    /**
     * Return the id the last log.
     *
     * @param isFlush whether to flush all pending task.
     */
    LogId getLastLogId(final boolean isFlush);

}
