package ru.otus.java.basic.http.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.Socket;

public class RequestHandler implements Runnable {
    private final Socket socket;
    private final Dispatcher dispatcher;
    private final byte[] buffer;
    private static final Logger logger = LogManager.getLogger(RequestHandler.class.getName());

    public RequestHandler(Socket socket) {
        this.socket = socket;
        this.dispatcher = new Dispatcher();
        this.buffer = new byte[8189];
    }

    public void run() {
        logger.debug("Обработка в потоке: {}", Thread.currentThread().getName());
        try {
            int n = socket.getInputStream().read(buffer);
            if (n < 1) {
                return;
            }
            String rawRequest = new String(buffer, 0, n);
            HttpRequest request = new HttpRequest(rawRequest);
            request.printInfo();
            dispatcher.execute(request, socket.getOutputStream());
            socket.close();
        } catch (IOException e) {
            logger.error(e);
        }
    }
}
