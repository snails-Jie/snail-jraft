package zhangjie.raft.core.option;

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

    public int getElectionTimeoutMs() {
        return electionTimeoutMs;
    }

    public void setElectionTimeoutMs(int electionTimeoutMs) {
        this.electionTimeoutMs = electionTimeoutMs;
    }
}
