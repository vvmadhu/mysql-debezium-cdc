package com.hcl.redhat.sor.message.listener;

import com.hcl.redhat.sor.message.service.CustomerService;
import io.debezium.data.Envelope;
import io.debezium.embedded.EmbeddedEngine;
import io.debezium.config.Configuration;

import java.util.concurrent.Executor;

import io.debezium.util.Clock;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.Executors;

@Component
public class CaptureChanges {
    private final Executor executor = Executors.newSingleThreadExecutor();

    private final EmbeddedEngine engine;

    CustomerService customerService;

    private CaptureChanges(Configuration customerConnector, CustomerService customerService) {
        this.engine = EmbeddedEngine
                .create()
                .using(customerConnector)
                //.using(Clock.SYSTEM)
                .notifying(this::handleEvent)
                .build();

        this.customerService = customerService;
    }


    @PostConstruct
    private void start() {
        System.out.println("Post Construct ....");
        this.executor.execute(engine);
    }

    @PreDestroy
    private void stop() {
        if (this.engine != null) {
            this.engine.stop();
        }
    }

    private void handleEvent(SourceRecord sourceRecord) {
        Struct sourceRecordValue = (Struct) sourceRecord.value();
        System.out.println("Topic = "+sourceRecord.topic());

        System.out.println("sourceRecordValue = "+sourceRecordValue.toString());
        /*if(sourceRecordValue != null) {

            sourceRecordValue.
            Envelope.Operation operation =
                    Envelope.Operation.forCode((String) sourceRecordValue.get(Envelope.FieldName.OPERATION));

            System.out.println("Update is happen !!! "+ operation.name());
        }*/
    }
}
