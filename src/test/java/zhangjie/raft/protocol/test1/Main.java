package zhangjie.raft.protocol.test1;

import com.google.protobuf.InvalidProtocolBufferException;

/**
 * @Author zhangjie
 * @Date 2020/6/8 21:40
 **/
public class Main {
    public static void main(String[] args) {

        //  序列化
        // 创建Person的Builder
        PersonProtobuf.Person.Builder personBuilder =
                PersonProtobuf.Person.newBuilder();
        // 设置Person的属性
        personBuilder.setAge(18);
        personBuilder.setName("张三丰");
        // 创建Person
        PersonProtobuf.Person zhangsanfeng = personBuilder.build();
        // 序列化，byte[]可以被写到磁盘文件，或者通过网络发送出去。
        byte[] data = zhangsanfeng.toByteArray();
        System.out.println("serialization end.");


        // 反序列化，byte[]可以读文件或者读取网络数据构建。
        System.out.println("deserialization begin.");
        try {
            PersonProtobuf.Person person = PersonProtobuf.Person.parseFrom(data);
            System.out.println(person.getAge());
            System.out.println(person.getName());
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }

    }
}
