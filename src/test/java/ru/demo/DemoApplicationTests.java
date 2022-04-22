package ru.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import generated.Main;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ClassUtils;
import org.springframework.util.StreamUtils;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

        //without validation

        Main main = objectMapper.readValue(xml, Main.class);

        System.out.println(new ObjectMapper().writeValueAsString(main));

        assertThat(main.getRoleType().getRoleId()).isNotEmpty();
        assertThat(main.getRoleType().getRoleId()).containsExactlyInAnyOrder("1", "3");
    }

    @Test
    public void parseXmlUseJaxb() throws IOException, JAXBException, SAXException {
        String xml = StreamUtils.copyToString(DemoApplicationTests.class.getClassLoader().getResourceAsStream("test.xml"), StandardCharsets.UTF_8);

        JAXBContext context = JAXBContext.newInstance(Main.class);
        javax.xml.bind.Unmarshaller um = context.createUnmarshaller();

        //without validation

        Main main = (Main) um.unmarshal(new StringReader(xml));

        System.out.println(new ObjectMapper().writeValueAsString(main));

        assertThat(main.getRoleType().getRoleId()).isNotEmpty();
        assertThat(main.getRoleType().getRoleId()).containsExactlyInAnyOrder("1", "3");
    }

    @Test
    public void parseXmlUseJaxbWithValidate() throws IOException, JAXBException, SAXException {
        String xml = StreamUtils.copyToString(DemoApplicationTests.class.getClassLoader().getResourceAsStream("test.xml"), StandardCharsets.UTF_8);

        JAXBContext context = JAXBContext.newInstance(Main.class);
        javax.xml.bind.Unmarshaller um = context.createUnmarshaller();

        //set schema for validation
        SchemaFactory sf = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = sf.newSchema(ClassUtils.getDefaultClassLoader().getResource("xsd/test.xsd"));
        um.setSchema(schema);

        Exception exception = assertThrows(UnmarshalException.class, () -> {
            um.unmarshal(new StringReader(xml));
        });

        exception.printStackTrace();
        assertThat(exception.toString()).contains("is expected");
    }

}
