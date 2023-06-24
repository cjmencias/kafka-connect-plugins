package org.cjmencias.kafka.connect.transforms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.header.ConnectHeaders;
import org.apache.kafka.connect.header.Header;
import org.apache.kafka.connect.source.SourceRecord;
import org.junit.Test;


/**
 * Unit test cases for {@code InsertHeaderCurrentTimestamp}.
 *
 * @author cjmencias
 */
public class InsertHeaderCurrentTimestampTest {

    private InsertHeaderCurrentTimestamp<SourceRecord> xform = new InsertHeaderCurrentTimestamp<>();

    private Map<String, ?> config(String header) {
        Map<String, String> result = new HashMap<>();
        if (header != null) {
            result.put(InsertHeaderCurrentTimestamp.CONFIG_HEADER, header);
        }
        return result;
    }

    private SourceRecord initSourceRecord() {
        Map<String, ?> sourcePartition = null;
        Map<String, ?> sourceOffset = null;
        String topic = "topic";
        Integer partition = 0;
        Schema keySchema = null;
        Object key = "key";
        Schema valueSchema = null;
        Object value = "value";
        Long timestamp = 0L;
        ConnectHeaders headers = new ConnectHeaders();

        return new SourceRecord(sourcePartition, sourceOffset, topic, partition,
                keySchema, key, valueSchema, value, timestamp, headers);
    }

    @Test
    public void testHeaderNameSpecified() {
        String headerName = "laststreamdate";
        xform.configure(config(headerName));

        SourceRecord recOrig = initSourceRecord();
        SourceRecord recNew = xform.apply(recOrig);

        assertNotNull(recNew);
        assertNotNull(recNew.headers());

        Header header = recNew.headers().lastWithName(headerName);
        assertNotNull(header);
        assertEquals(headerName, header.key());
        assertNotNull(headerName, header.value());
        assertTrue(header.value() instanceof Date);
    }

    @Test(expected = ConfigException.class)
    public void testHeaderNameMissing() {
        xform.configure(config(null));
        SourceRecord recOrig = initSourceRecord();
        xform.apply(recOrig);
    }

}
