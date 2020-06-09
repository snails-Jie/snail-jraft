package zhangjie.raft.serializable;

/**
 * @Author zhangjie
 * @Date 2020/6/8 17:59
 **/
public class Employee implements java.io.Serializable {
    public String name;
    public String address;
    //该字段不可序列化
    public transient int SSN;
    public int number;

    public void mailCheck() {
        System.out.println("Mailing a check to " + name + " " + address);
    }
}
