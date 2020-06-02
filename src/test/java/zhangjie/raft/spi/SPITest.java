package zhangjie.raft.spi;

import org.junit.Test;

import java.util.Optional;
import java.util.ServiceLoader;
import java.util.stream.StreamSupport;

/**
 * @author zhangjie
 * @date 2020/6/2 18:41
 */
public class SPITest {
    @Test
    public void serializerTest() {
        ClassLoader classLoader =  ClassLoader.getSystemClassLoader();
        ServiceLoader<ObjectSerializer> serializers = ServiceLoader.load(ObjectSerializer.class);
        final Optional<ObjectSerializer> serializer = StreamSupport.stream(serializers.spliterator(), false)
                .findFirst();
        ObjectSerializer objectSerializer = serializer.orElse(new JavaSerializer());
        objectSerializer.serialize(null);
    }


}
