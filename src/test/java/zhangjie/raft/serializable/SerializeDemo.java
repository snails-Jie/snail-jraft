package zhangjie.raft.serializable;

import org.junit.Test;

import java.io.*;

/**
 * @Author zhangjie
 * @Date 2020/6/8 18:17
 **/
public class SerializeDemo {

    //序列化
    @Test
    public void serializeTest() throws Exception{
        Employee e = new Employee();
        e.name = "Reyan Ali";
        e.address = "Phokka Kuan, Ambehta Peer";
        e.SSN = 11122333;
        e.number = 101;

        try {
            FileOutputStream fileOut =
                    new FileOutputStream("/tmp/employee.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(e);
            out.close();
            fileOut.close();
            System.out.printf("Serialized data is saved in /tmp/employee.ser");
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    /**
     * 反序列化
     * 1. 为了使JVM能够反序列化对象，必须能够找到该类的字节码
     *   -->找不到则会抛出ClassNotFoundException
     * 2.由于SSN字段是trasient，因此未将该值发送到输出流
     * @throws Exception
     */
    @Test
    public void deserializeTest() throws Exception{
        Employee e = null;
        try {
            FileInputStream fileIn = new FileInputStream("/tmp/employee.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            e = (Employee) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException i) {
            i.printStackTrace();
            return;
        } catch (ClassNotFoundException c) {
            System.out.println("Employee class not found");
            c.printStackTrace();
            return;
        }

        System.out.println("Deserialized Employee...");
        System.out.println("Name: " + e.name);
        System.out.println("Address: " + e.address);
        System.out.println("SSN: " + e.SSN);
        System.out.println("Number: " + e.number);
    }
}
