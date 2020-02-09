package com.sinovatio.tls.doraemon.test;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;

import java.io.IOException;

/**
 * Created by TF on 2018/12/22.
 */
public class HbaseHelper {

    private static void test() throws IOException {

        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", "sinovatio-hbase-01,sinovatio-hbase-02,sinovatio-hbase-03,sinovatio-hbase-04,sinovatio-hbase-05,sinovatio-hbase-06,sinovatio-hbase-07");
        conf.set("hbase.zookeeper.property.clientPort", "2181");
        //hbaseConf.set("hbase.mater","")

        HTable table = new HTable(conf,"tabletest");
        Put put = new Put("wangke2".getBytes());
        put.addColumn("f".getBytes(),"c1".getBytes(),"liubei".getBytes());
        table.put(put);

    }

}
