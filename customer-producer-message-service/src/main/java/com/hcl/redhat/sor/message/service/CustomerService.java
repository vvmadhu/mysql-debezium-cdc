package com.hcl.redhat.sor.message.service;

import com.hcl.redhat.sor.message.entity.Customer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    @Autowired
    Producer<String, Customer> customerProducer;

    @Value("${customer.topic}")
    String customerTopic;

    private void publish(Customer customer)  {
        customerProducer.send(new ProducerRecord(customerTopic, customer));
        System.out.println("Customer data published to on-prem topic !!!");

    }
}
