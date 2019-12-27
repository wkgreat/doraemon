package wk.doraemon.io;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

/**
 * @author: Wang Ke
 * @date: 2017/10/13 17:12
 */
public class TextReader {

    String path;
    String code = null;
    List<String> allRows = null;
    FileInputStream fis = null;
    InputStreamReader isr = null;
    BufferedReader br = null;

    public TextReader(String path){
        this.path=path;
    }

    public TextReader(String path, String code){
        this.path=path;
        this.code=code;
    }

    public TextReader init(){
        try {
            fis = new FileInputStream(this.path);
            if(this.code==null) {
                isr = new InputStreamReader(fis);
            } else {
                isr = new InputStreamReader(fis,code);
            }
            br = new BufferedReader(isr);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            this.close();
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

        return this;
    }

    public String readLine() {
        try {
            return br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 读取所有的行
     * */
    public List<String> readlines(){
        List<String> rowStrs = new ArrayList<String>();
        String s=null;
        try {
            while((s=br.readLine())!=null)
            {
                rowStrs.add(s);
                //System.out.println(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.allRows = rowStrs;

        return rowStrs;
    }


    /**
     * 获取第一行(如CSV表头)
     * */
    public List<String> getHeader(String sep){
        if(null!=allRows){
            return Arrays.asList(allRows.get(0).split(sep));
        } else
        {
            return null;
        }
    }

    /**
     * 获取CSV文件数据
     * #号开头的行不读（默认为注释行）
     * */
    public List<List<String>> getRecords(String sep){
        List<List<String>> records = new ArrayList<List<String>>();
        ListIterator iter = allRows.listIterator(1);
        String s;
        while(iter.hasNext()){
            s = (String) iter.next();
            if(s.startsWith("#")) continue; //wk: comment in csv execute with #
            List<String> record = Arrays.asList(s.split(sep));
            records.add(record);
        }

        return records;
    }



    public void close(){

        try {
            if(br!=null) br.close();
            if(isr!=null) isr.close();
            if(fis!=null) fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        TextReader reader = new TextReader("d:/wk/20170930_taixs.csv","GBK");
        reader.init();
        reader.readlines();
        System.out.println(reader.getRecords(","));
    }

}
