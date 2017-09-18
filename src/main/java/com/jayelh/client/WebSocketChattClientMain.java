package com.jayelh.client;

import com.jayelh.kafka.ConsumerLoop;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.net.URI;
import java.util.*;

import javax.websocket.ContainerProvider;
import javax.websocket.WebSocketContainer;

public class WebSocketChattClientMain {


	public static void main(String[] args) {

		try {

			System.out.println("Start");

			String dest = "ws://localhost:9999/chatt";
			ChattClientSocket socket = new ChattClientSocket();
			WebSocketContainer container = ContainerProvider.getWebSocketContainer();
			container.connectToServer(socket, new URI(dest));
			socket.getLatch().await();
			socket.sendMessage("Hello world!");






			Thread.sleep(10000l);


		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}
