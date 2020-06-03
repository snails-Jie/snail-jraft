package zhangjie.raft.classloader;

import com.sun.xml.internal.ws.api.ResourceLoader;
import org.junit.Test;

import java.net.URL;

/**
 * @author zhangjie
 * @date 2020/6/3 19:07
 */
public class ClassLoaderTest {

    @Test
    public void testResource(){
        ClassLoader classLoader = ClassLoaderTest.class.getClassLoader();
        URL resource = classLoader.getResource("");
        System.out.println(resource);
    }
}
