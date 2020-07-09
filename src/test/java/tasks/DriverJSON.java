package tasks;/*
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import wk.doraemon.io.TextReader;
import wk.doraemon.io.TextWriter;

import java.util.List;

public class DriverJSON {

    public static void main(String[] args) {


        TextReader reader = new TextReader("/Users/wkgreat/codes/amh/ymm-old-driver-spark/outputData/鲁H46728_20191217-1221北斗轨迹数据.txt");
        reader.init();
        List<String> lines = reader.readlines();
        String json = String.join("",lines);
        System.out.println(json);
        JSONArray array = JSON.parseArray(json);

        TextWriter writer = new TextWriter("/Users/wkgreat/codes/amh/ymm-old-driver-spark/outputData/鲁H46728_20191217.csv",false).init();
        String[] header = new String[] {
                "lon",
                "lat",
                "positionTime",
                "speed",
                "location",
                "createTime",
                "telephone",
                "address",
                "type",
                "dataResource"
        };
        writer.writeLine(String.join(",",header));
        for(int i=0; i<array.size(); i++) {
            JSONObject p = array.getJSONObject(i);
            String line = "" +
            p.getString("lon"                ) +","+
            p.getString("lat"                )+","+
            p.getString("positionTime"       )+","+
            p.getString("speed"              )+","+
            p.getString("location"           )+","+
            p.getString("createTime"         )+","+
            p.getString("telephone"          )+","+
            p.getString("address"            )+","+
            p.getString("type"               )+","+
            p.getString("dataResource"       );
            writer.writeLine(line);
        }
        writer.close();
        reader.close();

    }

}
