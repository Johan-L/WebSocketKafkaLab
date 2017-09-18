package com.jayelh.server;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/chatt")
public class ChattSocket {

	private static HashMap<String, Session> sessions = new HashMap<String, Session>();

	public ChattSocket() {

	}

	@OnOpen
	public void onOpen(Session session) {
		System.out.println("WebSocket opened: " + session.getId());
		sessions.put(session.getId(), session);

	}
	@OnMessage
	public void onMessage(String txt, Session sessionFromMessage) throws IOException {
		System.out.println("Message received: " + txt + " : session: " + sessionFromMessage.getId());

		Set<Session> allSessions = sessionFromMessage.getOpenSessions();

		allSessions.forEach((session) -> {
			try {
				session.getBasicRemote().sendText(txt);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	@OnClose
	public void onClose(CloseReason reason, Session session) {
		sessions.remove(session.getId());
		System.out.println("Closing a WebSocket due to " + reason.getReasonPhrase());

	}
}
