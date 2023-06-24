# kafka-connect-plugins

A collection of Simple Message Transformation (SMT) for Apache Kafka Connect


## Building the code

    $ ./mvnw clean install

## Transformations

### InsertHeaderCurrentTimestamp

This transformation inserts the current system timestamp into the header. It is beneficial when there is a need to capture the time at which the record is processed, for example, when Sink connectors consume a record. It enables deriving information about the delay between the record's production time and its consumption time.

```json
"transforms": "insertHeaderTs",
"transforms.insertHeaderTs.type": "org.cjmencias.kafka.connect.transforms.InsertHeaderCurrentTimestamp",
"transforms.insertHeaderTs.header": "laststreamdate"
```

