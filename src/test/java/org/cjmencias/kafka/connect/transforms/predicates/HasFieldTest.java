package org.cjmencias.kafka.connect.transforms.predicates;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.common.config.ConfigValue;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.transforms.util.SimpleConfig;
import org.junit.Test;

/**
 * Unit test cases for {@code HasFieldKey}.
 *
 * @author cjmencias
 */
public class HasFieldTest {

    @Test(expected = ConfigException.class)
    public void testConfigFieldRequired() {
        HasField<SourceRecord> predicate = new HasField<>();
        new SimpleConfig(predicate.config(), new HashMap<>());
    }

    @Test
    public void testConfig() {
        HasField<SourceRecord> predicate = new HasField<>();
        predicate.config().validate(Collections.singletonMap("field", "value"));

        List<ConfigValue> configs = predicate.config()
            .validate(Collections.singletonMap("field", ""));

        assertEquals(List.of("Invalid value  for configuration field: String must be non-empty"), 
            configs.get(0).errorMessages());
    }


    @Test
    public void testTombstone() {
        HasField<SourceRecord> predicate = new HasField<>();
        predicate.configure(Collections.singletonMap("field", "after"));

        SourceRecord record = new SourceRecord(null, null, null, null, null);
        assertFalse(predicate.test(record));
    }

    @Test
    public void testSchemaless() {
        HasField<SourceRecord> predicate = new HasField<>();
        predicate.configure(Collections.singletonMap("field", "after"));

        // missing
        Map<String, Object> value = new HashMap<>();
        SourceRecord record = new SourceRecord(null, null, null, null, value);
        assertFalse(predicate.test(record));

        // exists
        value.put("after", Collections.emptyMap());
        assertTrue(predicate.test(record));
    }

    @Test
    public void testWithSchema() {
        HasField<SourceRecord> predicate = new HasField<>();
        predicate.configure(Collections.singletonMap("field", "after"));

        // missing
        SchemaBuilder builder = SchemaBuilder.struct();
        Schema valueSchema = builder.build();
        Struct value = new Struct(valueSchema);
        SourceRecord record = new SourceRecord(null, null, null, valueSchema, value);
        assertFalse(predicate.test(record));

        // exists
        builder = SchemaBuilder.struct();
        builder.field("after", Schema.STRING_SCHEMA);
        valueSchema = builder.build();
        value = new Struct(valueSchema);
        record = new SourceRecord(null, null, null, valueSchema, value);
        assertTrue(predicate.test(record));
    }

}
