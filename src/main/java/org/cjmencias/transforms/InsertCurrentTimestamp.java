package org.cjmencias.transforms;

import java.util.Map;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.transforms.Transformation;
import org.apache.kafka.connect.transforms.util.SimpleConfig;

/**
 * This transformation inserts the current system timestamp into the record payload. 
 * It is useful when you need to capture the time at which the record is processed, 
 * such as when Sink connectors consume a record. It provides information about 
 * the delay between the time the record is produced and when it is consumed.
 */
public class InsertCurrentTimestamp<R extends ConnectRecord<R>> implements Transformation<R> {

    private static final String TIMESTAMP_FIELD = "timestamp.field";

    private static final String TIMESTAMP_FIELD_DEFAULT = "lastchangedate";

    public static final ConfigDef CONFIG_DEF = new ConfigDef()
            .define(TIMESTAMP_FIELD, ConfigDef.Type.STRING, 
            TIMESTAMP_FIELD_DEFAULT, ConfigDef.Importance.MEDIUM,
                "Field name for record timestamp.");

    private String timestamp;

    @Override
    public void configure(Map<String, ?> props) {
        final SimpleConfig config = new SimpleConfig(CONFIG_DEF, props);
        timestamp = config.getString(TIMESTAMP_FIELD);
    }

    @Override
    public R apply(R record) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'apply'");
    }

    @Override
    public void close() {
        
    }

    @Override
    public ConfigDef config() {
        return CONFIG_DEF;
    }
    
}
