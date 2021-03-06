package com.finaxys.utils;

import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by finaxys on 1/6/16.
 */
public class StreamConfiguration {


    private static org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(StreamConfiguration.class);

    private byte[] cfName;
    private String tableName ;
    private String sparkTableName ;

    private String hbaseConfHbase;
    private String hadoopConfHdfs;
    private String kafkaTopic ;
    private String kafkaQuorum ;


    private static StreamConfiguration instance;

    private StreamConfiguration() throws StreamLayerException {
        load();
    }

    public static final StreamConfiguration getInstance() {
        if (instance == null) {
            synchronized (StreamConfiguration.class) {
                if (instance == null) {
                    instance = new StreamConfiguration();
                    instance.load();
                }
            }
        }
        return instance;
    }

    private void load() throws StreamLayerException {

        // loading properties.txt into System Properties.
        Properties p = new Properties(System.getProperties());
        try{
            p.load(this.getClass().getClassLoader().getResourceAsStream("config.properties"));
        } catch (IOException e){
            LOGGER.info("Not able to load properties from file.");
            throw new StreamLayerException(e.getMessage()) ;
        }
        System.setProperties(p);
        // Loading properties in local variables
            this.tableName = System.getProperty("hbase.table", "trace");
            this.sparkTableName = System.getProperty("hbase.table.spark.result", "result");
            this.cfName = Bytes.toBytes(System.getProperty("hbase.cf", "cf"));
            assert cfName != null;

            this.hbaseConfHbase = System.getProperty("hbase.conf.hbase");
            this.hadoopConfHdfs = System.getProperty("hadoop.conf.hdfs");

            this.kafkaTopic = System.getProperty("kafka.topic") ;
            this.kafkaQuorum = System.getProperty("kafka.quorum") ;

    }

    public String getTableName() {
        return tableName;
    }

    public byte[] getColumnFamily() {
        return cfName;
    }

    public byte[] getCfName() {
        return cfName;
    }

    public String getHbaseConfHbase() {
        return hbaseConfHbase;
    }

    public String getHadoopConfHdfs() {
        return hadoopConfHdfs;
    }


    public String getSparkTableName() {
        return sparkTableName;
    }

    public void setSparkTableName(String sparkTableName) {
        this.sparkTableName = sparkTableName;
    }

    public String getKafkaTopic() {
        return kafkaTopic;
    }

    public void setKafkaTopic(String kafkaTopic) {
        this.kafkaTopic = kafkaTopic;
    }

    public String getKafkaQuorum() {
        return kafkaQuorum;
    }

    public void setKafkaQuorum(String kafkaQuorum) {
        this.kafkaQuorum = kafkaQuorum;
    }

}
