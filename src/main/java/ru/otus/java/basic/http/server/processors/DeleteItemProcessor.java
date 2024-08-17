package ru.otus.java.basic.http.server.processors;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.otus.java.basic.http.server.BadRequestException;
import ru.otus.java.basic.http.server.HttpRequest;
import ru.otus.java.basic.http.server.app.ItemsRepository;

public class DeleteItemProcessor implements RequestProcessor {
    private ItemsRepository itemsRepository;
    private static final Logger logger = LogManager.getLogger(DeleteItemProcessor.class.getName());

    public DeleteItemProcessor(ItemsRepository itemsRepository) {
        this.itemsRepository = itemsRepository;
    }

    @Override
    public void execute(HttpRequest request, OutputStream out) throws IOException {
        if (!request.containsParameter("id")) {
            logger.error("Не найден параметр id");
            throw new BadRequestException("Не найден параметр id");
        }
        long deleteId;
        try {
            deleteId = Long.parseLong(request.getParameter("id"));
        } catch (NumberFormatException e) {
            logger.error("неверный тип у deleteId");
            throw new BadRequestException("имеет неверный у deleteId");
        }
        String response = "";
        if (itemsRepository.delete(deleteId)) {
            response = response +
                    "HTTP/1.1 200 OK\r\n" +
                    "\r\n";
            logger.info("Элемент с id = {} удален успешно", deleteId);
        } else {
            response = response +
                    "HTTP/1.1 204 No Content\r\n" +
                    "\r\n";
            logger.info("Элемент с id = {} не существует", deleteId);
        }
        out.write(response.getBytes(StandardCharsets.UTF_8));
    }
}
