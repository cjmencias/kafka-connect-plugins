package org.cjmencias.kafka.connect.transforms;

import java.util.Date;
import java.util.Map;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.data.Timestamp;
import org.apache.kafka.connect.header.Headers;
import org.apache.kafka.connect.transforms.Transformation;
import org.apache.kafka.connect.transforms.util.SimpleConfig;

/**
 * This transformation inserts the current system timestamp into the header. 
 * It is beneficial when there is a need to capture the time at which the record is processed, 
 * for example, when Sink connectors consume a record. 
 * It enables deriving information about the delay between the record's production time 
 * and its consumption time.
 *
 * @author cjmencias
 */
public class InsertHeaderCurrentTimestamp<R extends ConnectRecord<R>> implements Transformation<R> {

    public static final String CONFIG_HEADER = "header";

    public static final ConfigDef CONFIG_DEF = new ConfigDef().define(
        CONFIG_HEADER, ConfigDef.Type.STRING, ConfigDef.NO_DEFAULT_VALUE, 
        ConfigDef.Importance.MEDIUM, "Header name for the current timestamp.");

    private String header;

    @Override
    public void configure(Map<String, ?> props) {
        final SimpleConfig config = new SimpleConfig(CONFIG_DEF, props);
        header = config.getString(CONFIG_HEADER);
        if (header == null) {
            throw new ConfigException("No header name configured");
        }
    }

    @Override
    public R apply(R record) {
        Headers updatedHeaders = record.headers().duplicate();
        updatedHeaders.add(header, new Date(), Timestamp.SCHEMA);
        return record.newRecord(record.topic(), 
            record.kafkaPartition(), record.keySchema(), 
            record.key(), record.valueSchema(), 
            record.value(), record.timestamp(), updatedHeaders);
    }

    @Override
    public void close() {
        
    }

    @Override
    public ConfigDef config() {
        return CONFIG_DEF;
    }

}
