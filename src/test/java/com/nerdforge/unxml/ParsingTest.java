package com.nerdforge.unxml;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.nerdforge.unxml.factory.ParsingFactory;
import com.nerdforge.unxml.parsers.ArrayParser;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import static org.fest.assertions.Assertions.assertThat;

public class ParsingTest {
    private static Parsing parsing;

    @BeforeClass
    public static void before(){
        Map<String, String> namespaces = new HashMap<String, String>(){{
            put("a", "http://www.w3.org/2005/Atom");
            put("app", "http://www.w3.org/2007/app");
        }};
        parsing = ParsingFactory.getInstance(namespaces).get();
    }

    @Test
    public void testParseObject() throws Exception {
        String content = "<?xml version=\"1.0\"?><feed xmlns=\"http://www.w3.org/2005/Atom\">\n" +
                "  <entry id=\"1\">\n" +
                "    <name>Homer Simpson</name>\n" +
                "    <birthday>1956-03-01</birthday>\n" +
                "    <email>chunkylover53@aol.com</email>\n" +
                "    <phoneNumbers>\n" +
                "      <home>5551234</home>\n" +
                "      <mobile>5555678</mobile>\n" +
                "      <work>5559991</work>\n" +
                "    </phoneNumbers>\n" +
                "  </entry>\n" +
                "</feed>";
        Document input = parsing.xml().document(content);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        ArrayParser parser = parsing.arr("/a:feed/a:entry",
                parsing.obj()
                        .attribute("id", "@id", parsing.with(Integer::parseInt))
                        .attribute("name", "a:name")
                        .attribute("birthday", "a:birthday", parsing.with(birthday -> LocalDate.parse(birthday, dateFormatter)))
                        .attribute("email", "a:email")
                        .attribute("phoneNumbers", parsing.arr("a:phoneNumbers/*", parsing.with(Integer::parseInt)))
        );

        ArrayNode node = parser.apply(input);
        //assertThat(node.at("/0/id").asInt()).isEqualTo(1);
        assertThat(node.at("/0/name").asText()).isEqualTo("Homer Simpson");
        assertThat(node.at("/0/birthday/0").asInt()).isEqualTo(1956);
        assertThat(node.at("/0/birthday/1").asInt()).isEqualTo(3);
        assertThat(node.at("/0/birthday/2").asInt()).isEqualTo(1);
        assertThat(node.at("/0/email").asText()).isEqualTo("chunkylover53@aol.com");
        assertThat(node.at("/0/phoneNumbers/0").asInt()).isEqualTo(5551234);
        assertThat(node.at("/0/phoneNumbers/1").asInt()).isEqualTo(5555678);
        assertThat(node.at("/0/phoneNumbers/2").asInt()).isEqualTo(5559991);
    }
}
