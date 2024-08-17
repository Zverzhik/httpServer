package ru.otus.java.basic.http.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer {
    private int port;
    private static final Logger logger = LogManager.getLogger(HttpServer.class.getName());

    public HttpServer(int port) {
        this.port = port;
    }

    public void start() {
        int totalThreads = Runtime.getRuntime().availableProcessors();
        CountDownLatch countDownLatch = new CountDownLatch(totalThreads);
        ExecutorService executorService = Executors.newFixedThreadPool(totalThreads);
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("Сервер запущен на порту: {}", port);
            try {
                while (true) {
                    Socket socket = serverSocket.accept();
                    executorService.execute(new RequestHandler(socket));
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                countDownLatch.await();
                executorService.shutdown();
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

