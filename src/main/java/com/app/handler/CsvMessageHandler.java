package com.app.handler;

import com.opencsv.bean.CsvToBeanBuilder;
import com.app.domain.CsvData;
import com.app.domain.FileModel;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.json.Jackson2JsonObjectMapper;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.MessagingException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class CsvMessageHandler implements MessageHandler {

    @Autowired
    private Jackson2JsonObjectMapper objectMapper;

    @SneakyThrows
    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        MessageHeaders headers = message.getHeaders();
        System.out.println(headers);
        final Path originalFile = Paths.get(headers.get("file_originalFile").toString());
        try (
                FileReader reader = new FileReader(originalFile.toFile());
        ) {
            final List<CsvData> parse1 = new CsvToBeanBuilder<CsvData>(reader)
                    .withType(CsvData.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build()
                    .parse();
            FileModel model = new FileModel();
            model.setCsvData(parse1);
            model.setFile(headers.get("file_name").toString());
            model.setSrcName(originalFile.toString());
            FileWriter fileWriter = new FileWriter(Paths.get("remote").resolve("from.json").toFile());
            objectMapper.toJson(model, fileWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

