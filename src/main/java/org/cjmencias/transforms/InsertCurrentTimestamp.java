package org.cjmencias.transforms;

import static org.apache.kafka.connect.transforms.util.Requirements.requireMap;
import static org.apache.kafka.connect.transforms.util.Requirements.requireStruct;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.data.Timestamp;
import org.apache.kafka.connect.transforms.Transformation;
import org.apache.kafka.connect.transforms.util.SchemaUtil;
import org.apache.kafka.connect.transforms.util.SimpleConfig;

/**
 * This transformation inserts the current system timestamp into the record payload. 
 * It is useful when you need to capture the time at which the record is processed, 
 * such as when Sink connectors consume a record. It provides information about 
 * the delay between the time the record is produced and when it is consumed.
 */
public class InsertCurrentTimestamp<R extends ConnectRecord<R>> implements Transformation<R> {

    private static final String TIMESTAMP_FIELD = "timestamp.field";

    private static final String PURPOSE = "field insertion";

    public static final ConfigDef CONFIG_DEF = new ConfigDef().define(
        TIMESTAMP_FIELD, ConfigDef.Type.STRING, ConfigDef.NO_DEFAULT_VALUE, 
        ConfigDef.Importance.MEDIUM, "Field name for record timestamp.");

    private String timestampField;

    @Override
    public void configure(Map<String, ?> props) {
        final SimpleConfig config = new SimpleConfig(CONFIG_DEF, props);
        timestampField = config.getString(TIMESTAMP_FIELD);
        if (timestampField == null) {
            throw new ConfigException("No timestamp field configured");
        }
    }

    @Override
    public R apply(R record) {
        if (operatingValue(record) == null) {
            return record;
        } else if (operatingSchema(record) == null) {
            return applySchemaless(record);
        } else {
            return applyWithSchema(record);
        }
    }

    @Override
    public void close() {
        
    }

    @Override
    public ConfigDef config() {
        return CONFIG_DEF;
    }

    protected Object operatingValue(R record) {
        return record.value();
    }

    protected Schema operatingSchema(R record) {
        return record.valueSchema();
    }

    protected R applySchemaless(R record) {
        final Map<String, Object> value = requireMap(operatingValue(record), PURPOSE);
        final Map<String, Object> updatedValue = new HashMap<>(value);
        updatedValue.put(timestampField, System.currentTimeMillis());
        return newRecord(record, null, updatedValue);
    }

    protected R applyWithSchema(R record) {
        final Struct value = requireStruct(operatingValue(record), PURPOSE);
        Schema updatedSchema = makeUpdatedSchema(value.schema());
        final Struct updatedValue = new Struct(updatedSchema);
        for (Field field : value.schema().fields()) {
            updatedValue.put(field.name(), value.get(field));
        }
        updatedValue.put(timestampField, new Date());
        return newRecord(record, updatedSchema, updatedValue);
    }

    protected R newRecord(R record, Schema updatedSchema, Object updatedValue) {
        return record.newRecord(record.topic(), record.kafkaPartition(), 
            updatedSchema, updatedValue, record.valueSchema(), 
            record.value(), record.timestamp());
    }

    protected Schema makeUpdatedSchema(Schema schema) {
        final SchemaBuilder builder = SchemaUtil.copySchemaBasics(schema, SchemaBuilder.struct());
        for (Field field : schema.fields()) {
            builder.field(field.name(), field.schema());
        }
        builder.field(timestampField, Timestamp.SCHEMA);
        return builder.build();
    }

}
