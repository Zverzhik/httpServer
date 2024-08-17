package ru.otus.java.basic.http.server;

import java.io.IOException;
import java.net.Socket;

public class RequestHandler implements Runnable {
    private final Socket socket;
    private final Dispatcher dispatcher;
    private final byte[] buffer;

    public RequestHandler(Socket socket) {
        this.socket = socket;
        this.dispatcher = new Dispatcher();
        this.buffer = new byte[8192];
    }

    public void run() {
        try {
            int n = socket.getInputStream().read(buffer);
            String rawRequest = new String(buffer, 0, n);
            HttpRequest request = new HttpRequest(rawRequest);
            request.printInfo(false);
            dispatcher.execute(request, socket.getOutputStream());
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
