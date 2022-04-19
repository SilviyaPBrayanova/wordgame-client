package org.kotelisk.games.wordgame.clients;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;
import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.DeploymentException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;
import org.glassfish.tyrus.client.ClientManager;

@ClientEndpoint
public class WordgameClientEndpoint {

    private Logger LOGGER = Logger.getLogger(WordgameClientEndpoint.class.getName());
    private static CountDownLatch latch;

    @OnOpen
    public void onOpen(Session session){
        String log = "Connecting ... :" + System.lineSeparator() +
                "Id: " + session.getId() + System.lineSeparator();
        LOGGER.info(log);
        System.out.println("LOG: " + log);

        try {
            session.getBasicRemote().sendText("start");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        LOGGER.info("Received ...." + message);
        System.out.println("LOG: " + "Received ...." + message);

        BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
        try {
            String userInput = bufferRead.readLine();
            session.getBasicRemote().sendText(userInput);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason){
        LOGGER.info(String.format("Session %s close because of %s", session.getId(), closeReason));
        System.out.println("LOG: " + String.format("Session %s close because of %s", session.getId(), closeReason));
        latch.countDown();
    }

    public static void main(String[] args) throws IOException {
        latch = new CountDownLatch(1);

        ClientManager clientManager = ClientManager.createClient();

        try {
            Session session = clientManager.connectToServer(WordgameClientEndpoint.class,
                    new URI("ws://localhost:8025/websockets/wordgame"));
            latch.await();

        } catch (DeploymentException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
