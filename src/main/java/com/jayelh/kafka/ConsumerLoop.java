package com.jayelh.kafka;

import com.jayelh.client.ChattClientSocket;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.websocket.ContainerProvider;
import javax.websocket.WebSocketContainer;
import java.net.URI;
import java.util.*;

public class ConsumerLoop implements Runnable {
    private final KafkaConsumer<String, String> consumer;
    private final List<String> topics;
    private final int id;
    private  ChattClientSocket socket;
    private WebSocketContainer container;

    public ConsumerLoop() {
        this.id = 42;
        topics = new LinkedList<>();
        topics.add("polopoly.changelist");
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", "test-consumer-group");
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", StringDeserializer.class.getName());
        this.consumer = new KafkaConsumer<>(props);

        socket = new ChattClientSocket();
        container = ContainerProvider.getWebSocketContainer();
    }

    public static void main(String args[]) {
        (new Thread(new ConsumerLoop())).start();
    }

    @Override
    public void run() {
        try {
            String dest = "ws://localhost:9999/chatt";

            container.connectToServer(socket, new URI(dest));
            socket.getLatch().await();
            socket.sendMessage("Hello world!");

            consumer.subscribe(topics);

            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Long.MAX_VALUE);
                for (ConsumerRecord<String, String> record : records) {

                    JsonElement responseElement = new JsonParser().parse(record.value());
                    JsonObject object = responseElement.getAsJsonObject();
                    String string = object.get("payload").getAsString();

                    System.out.println("string: " + string);
                    String[] parts = string.split(":");
                    if(parts[0].equals("mutation") && !parts[1].equals("draft")) {
                        String[] smallerParts = parts[2].split("/");
                        String contentId = smallerParts[1] + "/" + smallerParts[2];
                        System.out.println("Updated " + contentId);
                        socket.sendMessage("Content was mutaded: " + contentId);
                    }





                }
            }
        } catch (WakeupException e) {
            // ignore for shutdown

        } catch (Throwable t) {
            t.printStackTrace();

        } finally {
            consumer.close();
        }
    }

    public void shutdown() {
        consumer.wakeup();
    }
}