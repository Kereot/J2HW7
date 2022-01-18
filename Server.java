package server;

import org.omg.CORBA.PUBLIC_MEMBER;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    private ServerSocket server;
    private Socket socket;
    private final int PORT = 8189;

    private List<ClientHandler> clients;
    private AuthService authService;


    public Server() {
        clients = new CopyOnWriteArrayList<>();
        authService = new SimpleAuthService();

        try {
            server = new ServerSocket(PORT);
            System.out.println("Server started!");

            while (true) {
                socket = server.accept();
                System.out.println("Client connected: " + socket.getRemoteSocketAddress());
                new ClientHandler(this, socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("Server stop");
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
    }

    public void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }

    public void broadcastMsg(ClientHandler sender, String msg) {
        String message = String.format("[ %s ]: %s", sender.getNickname(), msg);
        for (ClientHandler c : clients) {
            c.sendMsg(message);
        }
    }

    public void privateMsg(ClientHandler sender, String recipient, String msg) {
        boolean success = false; // т.к. нет проверки на уникальность никнеймов, это позволит пройти весь цикл и отправить всем с таким никнейном; иначе можно было бы поставить break или return, чтобы не гонять цикл до конца при совпадении.
        sender.sendMsg("Whispered to [ " + recipient + " ]: " + msg);
        String message = String.format("Whisper from [ %s ]: %s", sender.getNickname(), msg);
        for (ClientHandler c : clients) {
            if (c.getNickname().equals(recipient) && c != sender) {
                c.sendMsg(message);
                success = true;
            }
        }
        if (!success) {sender.sendMsg("Server: no such recipient");}
    }

    public AuthService getAuthService() {
        return authService;
    }
}
