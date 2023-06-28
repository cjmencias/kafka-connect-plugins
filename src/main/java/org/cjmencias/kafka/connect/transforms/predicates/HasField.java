package org.cjmencias.kafka.connect.transforms.predicates;

import static org.apache.kafka.connect.transforms.util.Requirements.requireMap;

import java.util.Map;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.transforms.predicates.Predicate;
import org.apache.kafka.connect.transforms.util.SimpleConfig;

/**
 * This predicate checks whether a field exists within the payload value.
 *
 * @author cjmencias
 */
public class HasField<R extends ConnectRecord<R>> implements Predicate<R> {

    private static final String CONFIG_FIELD = "field";

    public static final String OVERVIEW_DOC 
        = "A predicate which is true for records that has the specified field key.";

    public static final String PURPOSE = "predicate";

    public static final ConfigDef CONFIG_DEF = new ConfigDef()
        .define(CONFIG_FIELD, ConfigDef.Type.STRING, ConfigDef.NO_DEFAULT_VALUE,
            new ConfigDef.NonEmptyString(), ConfigDef.Importance.MEDIUM, "The field name.");

    private String field;

    @Override
    public ConfigDef config() {
        return CONFIG_DEF;
    }

    @Override
    public void configure(Map<String, ?> configs) {
        field = new SimpleConfig(config(), configs).getString(CONFIG_FIELD);
    }

    @Override
    public boolean test(R record) {
        if (operatingValue(record) == null) {
            return false;
        } else if (operatingSchema(record) == null) {
            final Map<String, Object> value = requireMap(operatingValue(record), PURPOSE);
            return value != null ? value.containsKey(field) : false;
        } else {
            final Schema schema = operatingSchema(record);
            return schema != null ? schema.fields().stream()
                .filter(f -> f.name().equals(field))
                .findFirst().isPresent() : false;
        }
    }

    @Override
    public void close() {
        
    }

    protected Object operatingValue(R record) {
        return record.value();
    }

    protected Schema operatingSchema(R record) {
        return record.valueSchema();
    }

}
