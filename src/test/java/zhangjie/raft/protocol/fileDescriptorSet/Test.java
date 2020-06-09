package zhangjie.raft.protocol.fileDescriptorSet;
import java.util.Map;
import java.util.Map.*;

/**
 * @Author zhangjie
 * @Date 2020/6/9 7:13
 **/
public class Test {

    public static void main(String[] args) throws Exception {
        ParseProto parse = new ParseProto();
        String desc = parse.genProtoDesc("options.proto");
        Map<String, Integer> extendInfo = parse.getExtendInfo(desc);
        Map<String, Object> msgInfo = parse.getMsgInfo(desc);

        System.out.println("扩展信息：");
        for(Entry<String, Integer> e : extendInfo.entrySet()) {
            System.out.println(e.getKey() + "->" + e.getValue());
        }

        System.out.println("\n协议信息：");
        for(Entry<String, Object> e : msgInfo.entrySet()) {
            System.out.println(e.getKey() + "->" + e.getValue());
        }
    }
}
