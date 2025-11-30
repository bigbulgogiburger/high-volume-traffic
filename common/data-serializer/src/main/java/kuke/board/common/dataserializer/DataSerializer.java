package kuke.board.common.dataserializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class DataSerializer {

    private static final ObjectMapper objectMapper = initialize();

    private static ObjectMapper initialize() {
        return new ObjectMapper().registerModule(new JavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }


    public static <T> T deserialize(String data, Class<T> clazz) {
        try{
            return objectMapper.readValue(data,clazz);
        }catch (JsonProcessingException e){
            log.error("deserialize error data={}, clazz={}",data,clazz,e);
            return null;
        }
    }

    public static <T> T deserialize(Object data, Class<T> clazz) {
        return objectMapper.convertValue(data, clazz);
    }

    public static String serialize(Object data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            log.error("serialize error data={}",data,e);
            return null;

        }
    }
}
