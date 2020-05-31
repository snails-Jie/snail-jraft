package zhangjie.raft.core;

import io.netty.util.HashedWheelTimer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zhangjie.raft.core.conf.ConfigurationEntry;
import zhangjie.raft.core.entity.Ballot;
import zhangjie.raft.core.entity.LogId;
import zhangjie.raft.core.entity.NodeId;
import zhangjie.raft.core.entity.PeerId;
import zhangjie.raft.core.error.RaftError;
import zhangjie.raft.core.option.NodeOptions;
import zhangjie.raft.core.rpc.RaftClientService;
import zhangjie.raft.core.rpc.RpcRequests;
import zhangjie.raft.core.rpc.RpcResponseClosureAdapter;
import zhangjie.raft.core.storage.LogManager;
import zhangjie.raft.core.util.NamedThreadFactory;
import zhangjie.raft.core.util.RepeatedTimer;
import zhangjie.raft.core.util.Requires;
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
    private static final Logger LOG  = LoggerFactory.getLogger(NodeImpl.class);

    /** Timers */
    private RepeatedTimer electionTimer;

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    protected final Lock writeLock = this.readWriteLock.writeLock();
    protected final Lock readLock = this.readWriteLock.readLock();

    private volatile State state;
    private long currTerm;
    private volatile long lastLeaderTimestamp;

    private NodeOptions options;

    //集群信息
    private ConfigurationEntry conf;
    //节点信息
    private final PeerId serverId;
    //组名
    private final String groupId;
    //TODO 与上述serverId与 groupId重复
    private NodeId nodeId;

    //预选举投票箱
    private final Ballot preVoteCtx = new Ballot();
    //rpc客户端
    private RaftClientService rpcService;
    //节点统计
    private NodeMetrics metrics;
    //日志管理器
    private LogManager logManager;


    public NodeImpl(final String groupId,final PeerId serverId){
        if (groupId != null) {
            Utils.verifyGroupId(groupId);
        }
        this.groupId = groupId;
        this.serverId = serverId != null ? serverId.copy() : null;
        this.state = State.STATE_UNINITIALIZED;
        this.currTerm = 0;
    }


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

        this.conf = new ConfigurationEntry();
        this.conf.setId(new LogId());
        this.conf.setConf(this.options.getInitilaConf());


        if(!this.conf.isEmpty()){
            //conf和oldConf中peers属性包含了learners属性（不应该包含）
            Requires.requireTrue(this.conf.isValid(),"Invalid conf:%s",this.conf);
        }else{
            LOG.info("Init node {} with empty conf.", this.serverId);
        }

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
     * 预选举RPC结果回调
     */
    private class OnPreVoteRpcDone extends RpcResponseClosureAdapter<RpcRequests.RequestVoteResponse> {

        final long         startMs;
        final PeerId       peer;
        final long         term;
        //选举请求
        RpcRequests.RequestVoteRequest request;

        public OnPreVoteRpcDone(final PeerId peer, final long term) {
            super();
            this.startMs = Utils.monotonicMs();
            this.peer = peer;
            this.term = term;
        }

        //回调接口方法
        @Override
        public void run(final Status status) {
            NodeImpl.this.metrics.recordLatency("pre-vote", Utils.monotonicMs() - this.startMs);
            if (!status.isOk()) {
                LOG.warn("Node {} PreVote to {} error: {}.", getNodeId(), this.peer, status);
            } else {
                handlePreVoteResponse(this.peer, this.term, getResponse());
            }
        }
    }

    //待完成
    private void stepDown(final long term, final boolean wakeupCandidate, final Status status) {

    }

    /**
     * 预选举回调结果处理
     *  1. 节点状态必须是Follower、请求前后当前任期不能变、返回结果的任期比较大于当前任期
     *  2. 如果返回已投票（待完成）
     *       -->给相应节点投一票
     *          --> 查看自己是否已经达到选票法制
     *              --> 开始选举流程
     */
    public void handlePreVoteResponse(final PeerId peerId, final long term, final RpcRequests.RequestVoteResponse response) {
        if (this.state != State.STATE_FOLLOWER) {
            LOG.warn("Node {} received invalid PreVoteResponse from {}, state not in STATE_FOLLOWER but {}.",
                    getNodeId(), peerId, this.state);
            return;
        }
        if (term != this.currTerm) {
            LOG.warn("Node {} received invalid PreVoteResponse from {}, term={}, currTerm={}.", getNodeId(),
                    peerId, term, this.currTerm);
            return;
        }
        //如果返回结果的任期大于当前任期，则将当前leader重新选举
        if (response.getTerm() > this.currTerm) {
            LOG.warn("Node {} received invalid PreVoteResponse from {}, term {}, expect={}.", getNodeId(), peerId,
                    response.getTerm(), this.currTerm);
            stepDown(response.getTerm(), false, new Status(RaftError.EHIGHERTERMRESPONSE,
                    "Raft node receives higher term pre_vote_response."));
            return;
        }
        LOG.info("Node {} received PreVoteResponse from {}, term={}, granted={}.", getNodeId(), peerId,
                response.getTerm(), response.getGranted());

        //待完成
    }

    /**
     * 在写锁内执行
     * 1. 正在安装快照时不能发起预投票
     * 2. 当前节点必须包含在集群列表中
     * 3. 获取最新的LogId
     *   ---> 得到LastLogIndex 和 LastLogTerm（判断依据）
     * 4. 防止currTerm出现ABA问题
     * 5. 初始化投票箱
     *      --> 投票
     *         --> 达到阈值成为候选者
     * 6. 发起预投票请求
     */
    private void preVote() {
        long oldTerm;
        try{

            //节点不属于集群
            if(!this.conf.contains(this.serverId)){
                LOG.warn("Node {} can't do preVote as it is not in conf <{}>.", getNodeId(), this.conf);
                return;
            }
            oldTerm = this.currTerm;
        }finally {
            this.writeLock.unlock();
        }

        final LogId lastLogId = this.logManager.getLastLogId(true);

        boolean doUnlock = true;
        this.writeLock.lock();
        try{
            //防止ABA问题（在释放锁和加锁之间）
            if(oldTerm != this.currTerm){
                LOG.warn("Node {} raise term {} when get lastLogId.", getNodeId(), this.currTerm);
                return;
            }
            this.preVoteCtx.init(this.conf.getConf(),this.conf.isStable() ? null : this.conf.getOldConf());
             //遍历集群节点
            for(final PeerId peer : this.conf.listPeers()){
                //如果是当前节点则跳出进入下次循环
                if(peer.equals(this.serverId)){
                    continue;
                }
                //当前节点连接集群中其他节点
                if(!this.rpcService.connect(peer.getEndpoint())){
                    LOG.warn("Node {} channel init failed, address={}.", getNodeId(), peer.getEndpoint());
                    continue;
                }
                //组装预选举请求，发送请求
                final OnPreVoteRpcDone done = new OnPreVoteRpcDone(peer,this.currTerm);
                done.request = RpcRequests.RequestVoteRequest.newBuilder()
                                .setPreVote(true) // it's a pre-vote request.
                                .setGroupId(this.groupId) //
                                .setServerId(this.serverId.toString()) //
                                .setPeerId(peer.toString()) //
                                .setTerm(this.currTerm + 1) // next term
                                .setLastLogIndex(lastLogId.getIndex()) //
                                .setLastLogTerm(lastLogId.getTerm()) //
                                .build();
                this.rpcService.preVote(peer.getEndpoint(), done.request, done);
            }

            /**
             * 待完成
             * 1.给当前节点（自己）投票
             * 2.查看是否超过阈值，是则开始正式选举
             */

        }finally {
            if(doUnlock){
                this.writeLock.unlock();
            }
        }
    }

    private boolean isCurrentLeaderValid() {
        return Utils.monotonicMs() - this.lastLeaderTimestamp < this.options.getElectionTimeoutMs();
    }

    @Override
    public NodeId getNodeId() {
        if(this.nodeId == null){
            this.nodeId = new NodeId(this.groupId,this.serverId);
        }
        return this.nodeId;
    }
}
