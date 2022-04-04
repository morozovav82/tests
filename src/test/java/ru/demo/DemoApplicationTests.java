package ru.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import generated.Main;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StreamUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DemoApplicationTests {

    @Autowired
    private ObjectMapper objectMapper;

    /*
        Работает, если вручную добавить аннотацию
        @JacksonXmlElementWrapper(useWrapping = false)

        над полем
        protected List<String> roleId;

        в классе RoleType

        или добавить в конфигурацию XmlMapper
        xmlMapper.setDefaultUseWrapper(false);
    */
    @Test
    public void parseXmlUseJackson() throws IOException {
        String xml = StreamUtils.copyToString(DemoApplicationTests.class.getClassLoader().getResourceAsStream("test.xml"), StandardCharsets.UTF_8);

        Main main = objectMapper.readValue(xml, Main.class);

        System.out.println(new ObjectMapper().writeValueAsString(main));

        assertThat(main.getRoleType().getRoleId()).isNotEmpty();
        assertThat(main.getRoleType().getRoleId()).containsExactlyInAnyOrder("1", "3");
    }

    @Test
    public void parseXmlUseJaxb() throws IOException, JAXBException {
        String xml = StreamUtils.copyToString(DemoApplicationTests.class.getClassLoader().getResourceAsStream("test.xml"), StandardCharsets.UTF_8);

        JAXBContext context = JAXBContext.newInstance(Main.class);
        javax.xml.bind.Unmarshaller um = context.createUnmarshaller();
        Main main = (Main) um.unmarshal(new StringReader(xml));

        System.out.println(new ObjectMapper().writeValueAsString(main));

        assertThat(main.getRoleType().getRoleId()).isNotEmpty();
        assertThat(main.getRoleType().getRoleId()).containsExactlyInAnyOrder("1", "3");
    }

}
