package zhangjie.raft.core.option;

import zhangjie.raft.core.conf.Configuration;

/**
 * @Author zhangjie
 * @Date 2020/5/24 22:57
 **/
public class NodeOptions {
    /**
     *  如果一个跟随者没有收到任何来自领导者的消息，那么它将成为候选人
     *  默认值：1000 (1s)
     */
    private int electionTimeoutMs  = 1000;

    /**
     * 如果节点从空环境开始(LogStorage和SnapshotStorage均为空)，它将使用intial_conf作为group的配置，否则它将从现有环境
     * 默认值：一个空组
     */
    private Configuration initilaConf = new Configuration();

    public int getElectionTimeoutMs() {
        return electionTimeoutMs;
    }

    public void setElectionTimeoutMs(int electionTimeoutMs) {
        this.electionTimeoutMs = electionTimeoutMs;
    }

    public Configuration getInitilaConf() {
        return initilaConf;
    }

    public void setInitilaConf(Configuration initilaConf) {
        this.initilaConf = initilaConf;
    }
}
