package wk.doraemon.io;

import java.io.*;

/**
 * Created by TF on 2018/12/29.
 */
public class Serializer {

    public static byte[] serialize(Object obj) {

        ObjectOutputStream oos = null;
        ByteArrayOutputStream baos = null;

        try {
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            byte[] byt = baos.toByteArray();
            return byt;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

    public static Object deserialize(byte[] byt) {

        ObjectInputStream ois = null;
        ByteArrayInputStream bais = null;

        try {

            bais = new ByteArrayInputStream(byt);
            ois = new ObjectInputStream(bais);
            Object obj = ois.readObject();
            return obj;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;

    }

}
