package wk.doraemon.io;

import java.io.*;
import java.util.List;

/**
 * @author: Wang Ke
 * @date: 2017/10/13 18:17
 */
public class TextWriter {

    File file = null;
    FileOutputStream fos = null;
    OutputStreamWriter osw = null;
    BufferedWriter bw = null;
    boolean append = false;

    private static final String SEP = System.lineSeparator();

    public TextWriter(String path, boolean append) {

        file = new File(path).getAbsoluteFile();
        this.append = append;
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public TextWriter(File f, boolean append){
        this.append = append;
        file = f.getAbsoluteFile();
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public TextWriter init(){
        try {
            fos = new FileOutputStream(file,append);
            osw = new OutputStreamWriter(fos,"GBK");
            bw = new BufferedWriter(osw);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return this;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return this;
        }
        return this;

    }

    public void writeLine(String s){
        try {
            bw.write(s+SEP);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void writeLines(List<String> ss) {
        for (String s : ss) {
            writeLine(s);
        }
    }

    public void close(){
        try {
            if(bw!=null) bw.close();
            if(osw!=null) osw.close();
            if(fos!=null) fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
