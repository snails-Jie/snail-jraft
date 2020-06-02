package zhangjie.raft.spi;

/**
 * @author zhangjie
 * @date 2020/6/2 18:36
 */
public interface ObjectSerializer {
    void serialize(Object obj);
}
