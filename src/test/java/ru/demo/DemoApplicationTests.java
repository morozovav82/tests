package ru.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import generated.Main;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DemoApplicationTests {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void parseRequest() throws IOException {
        String xml = StreamUtils.copyToString(DemoApplicationTests.class.getClassLoader().getResourceAsStream("test.xml"), StandardCharsets.UTF_8);

        Main main = objectMapper.readValue(xml, Main.class);

        System.out.println(new ObjectMapper().writeValueAsString(main));

        assertThat(main.getRoleType().getRoleId()).isNotEmpty();
        assertThat(main.getRoleType().getRoleId()).containsExactlyInAnyOrder("1", "3");
    }

}
