package zhangjie.raft.spi;

/**
 * @author zhangjie
 * @date 2020/6/2 18:38
 */
public class JavaSerializer implements ObjectSerializer {
    @Override
    public void serialize(Object obj) {
        System.out.println("JavaSerializer 序列化");
    }
}
