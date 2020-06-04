package zhangjie.raft.classloader;

import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

/**
 * @author zhangjie
 * @date 2020/6/3 19:07
 */
public class ClassLoaderTest {

    /**
     * 加载当前类加载器以及父类加载器所在路径的资源文件
     * 将遇到的第一个资源文件直接返回！！！
     *  比如当前工程类路径有META-INF/services文件,引入的第三方jar包也有这个文件
     * 返回的是当前工程下的这个资源文件
     */
    @Test
    public void testGetResource(){
        ClassLoader classLoader = ClassLoaderTest.class.getClassLoader();
        //不能以"/"作为资源的起始"/META-INF/services"
        URL resource = classLoader.getResource("META-INF/services");
        System.out.println(resource);
    }

    /**
     * 加载当前类加载器以及父类加载器所在路径的资源文件
     * 将遇到的所有资源文件全部返回！
     * 比如当前工程类路径有META-INF/services文件,引入的第三方jar包也有这个文件
     *  则将这些文件全部返回
     * @throws IOException
     */
    @Test
    public void testGetResources() throws IOException {
        ClassLoader classLoader = ClassLoaderTest.class.getClassLoader();
        Enumeration<URL> enumeration = classLoader.getResources("META-INF/services");
        // 打印出所有同名的资源文件
        while (enumeration.hasMoreElements()) {
            URL url1 = enumeration.nextElement();
            System.out.println("file=" + url1.getFile());
        }
    }
}
