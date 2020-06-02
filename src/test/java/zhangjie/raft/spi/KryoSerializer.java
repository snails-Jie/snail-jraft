package zhangjie.raft.spi;

/**
 * @author zhangjie
 * @date 2020/6/2 18:37
 */
public class KryoSerializer implements ObjectSerializer {

    @Override
    public void serialize(Object obj) {
        System.out.println("KryoSerializer 序列化");
    }


}
