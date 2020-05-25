package zhangjie.raft.core;

import io.netty.util.HashedWheelTimer;
import zhangjie.raft.core.option.NodeOptions;
import zhangjie.raft.core.util.NamedThreadFactory;
import zhangjie.raft.core.util.RepeatedTimer;
import zhangjie.raft.core.util.Utils;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @Author zhangjie
 * @Date 2020/5/24 18:46
 **/
public class NodeImpl implements Node{
    /** Timers */
    private RepeatedTimer electionTimer;

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    protected final Lock writeLock = this.readWriteLock.writeLock();
    protected final Lock readLock = this.readWriteLock.readLock();
    private volatile State state;
    private volatile long lastLeaderTimestamp;
    private NodeOptions options;


    @Override
    public boolean init(Object opts) {
        String name = "JRaft-VoteTimer-node1";
        //可以做成配置参数
        int electionTimeoutMs = 1000;
        this.electionTimer = new RepeatedTimer(name,electionTimeoutMs,
                new HashedWheelTimer(new NamedThreadFactory(name, true), 1, TimeUnit.MILLISECONDS, 2048)) {

            @Override
            protected void onTrigger() {
                handleElectionTimeout();
            }

            @Override
            protected int adjustTimeout(int timeoutMs) {
                return randomTimeout(timeoutMs);
            }
        };
        return false;
    }

    private int randomTimeout(final int timeoutMs) {
        //可以做成配置参数
        int maxElectionDelayMs = 1000;
        return ThreadLocalRandom.current().nextInt(timeoutMs, timeoutMs + maxElectionDelayMs);
    }

    /**
     * 1. 只有Follower节点才能进行选举
     * 2. 离上次leader响应时间超过超时时间，才开始选举
     * 3. 判断是否发起选举（第一个版本不考虑）
     *   3.1 通过比较节点的优先级和目标优先级是否允许发起选举
     *   3.2 如果直到下次选举超时都没有选举下一位leader，将以指数方式衰减其本地目标优先级
     * 4. 预投票
     */
    private void handleElectionTimeout()  {
        boolean doUnlock = true;
        this.writeLock.lock();
        try{
            if(this.state != State.STATE_FOLLOWER){
                return;
            }
            if(isCurrentLeaderValid()){
                return;
            }
            doUnlock = false;
            preVote();
        }finally {
            if(doUnlock){
                this.writeLock.unlock();
            }
        }
    }

    /**
     * 在写锁内执行
     * 1. 正在安装快照时不能发起预投票
     * 2. 当前节点必须包含在集群列表中
     * 3. 获取最新的LogId
     * 4. 防止currTerm出现ABA问题
     */
    private void preVote() {

    }

    private boolean isCurrentLeaderValid() {
        return Utils.monotonicMs() - this.lastLeaderTimestamp < this.options.getElectionTimeoutMs();
    }
}
