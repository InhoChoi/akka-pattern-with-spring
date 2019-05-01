package com.inhochoi.springakka.persistence;

import akka.serialization.JSerializer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class JsonSerializer extends JSerializer {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Object fromBinaryJava(byte[] bytes, Class<?> manifest) {
        try {
            return objectMapper.readValue(bytes, manifest);
        } catch (IOException e) {
            log.error("Message byte : {}, Class : {} can't serialize", bytes, manifest);
            return null;
        }
    }

    @Override
    public int identifier() {
        return 1;
    }

    @Override
    public byte[] toBinary(Object o) {
        try {
            return objectMapper.writeValueAsBytes(o);
        } catch (JsonProcessingException e) {
            log.error("Object {} can't deserialize", o);
            return new byte[0];
        }
    }

    @Override
    public boolean includeManifest() {
        return true;
    }
}
