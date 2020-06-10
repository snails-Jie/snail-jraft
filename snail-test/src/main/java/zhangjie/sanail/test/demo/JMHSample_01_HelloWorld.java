package zhangjie.sanail.test.demo;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 基准测试 HashMap是否指定size的性能区别
 * 这两个基准测试方法执行流程是：每个方法执行前都进行5次预热执行，每隔1秒进行一次预热操作，
 * 预热执行结束之后进行5次实际测量执行，每隔1秒进行一次实际执行，我们此次基准测试测量的是平均响应时长，单位是us
 *
 * 1. JVM 的 JIT 机制的存在，如果某个函数被调用多次之后，JVM 会尝试将其编译成为机器码从而提高执行速度
 * 2. @Warmup用来配置预热的内容，可用于类或者方法上
 *   2.1 warmup参数介绍
 *      （1）iterations：预热的次数
 *      （2）time：每次预热的时间
 *      （3）timeUnit：时间单位，默认是s。
 * 3. @Measurement 用来控制实际执行的内容，配置的选项本warmup一样
 * @Author zhangjie
 * @Date 2020/6/10 7:24
 **/
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
public class JMHSample_01_HelloWorld {

    static class Demo {
        int id;
        String name;
        public Demo(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    static List<Demo> demoList;
    static {
        demoList = new ArrayList();
        for (int i = 0; i < 10000; i ++) {
            demoList.add(new Demo(i, "test"));
        }
    }


    /**
     * 1. @Benchmark标签是用来标记测试方法的
     * 2. 被@Benchmark标记的方法必须是public的
     * 3. @BenchmarkMode主要是表示测量的纬度
     *   3.1 Mode.Throughput 吞吐量纬度
     *   3.2 Mode.AverageTime 平均时间
     *   3.3 Mode.SampleTime 抽样检测
     *   3.4 Mode.SingleShotTime 检测一次调用
     *   3.5 Mode.All 运用所有的检测模式
     * 4. @OutputTimeUnit代表测量的单位
     */
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void testHashMapWithoutSize() {
        Map map = new HashMap();
        for (Demo demo : demoList) {
            map.put(demo.id, demo.name);
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void testHashMap() {
        Map map = new HashMap((int)(demoList.size() / 0.75f) + 1);
        for (Demo demo : demoList) {
            map.put(demo.id, demo.name);
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_01_HelloWorld.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
