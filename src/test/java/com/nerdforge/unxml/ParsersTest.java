package com.nerdforge.unxml;

import static com.nerdforge.unxml.Parsers.*;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.nerdforge.unxml.parsers.ArrayParser;
import org.junit.Test;
import org.w3c.dom.Document;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.nerdforge.unxml.Parsers.with;
import static org.fest.assertions.Assertions.assertThat;

public class ParsersTest {
    Map<String, String> NAMESPACES = Collections.unmodifiableMap(new HashMap<String, String>() {{
        put("a", "http://www.w3.org/2005/Atom");
        put("app", "http://www.w3.org/2007/app");
    }});

    @Test
    public void testParseObject() throws Exception {
        Parsers.injector(NAMESPACES);

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
        Document input = document(content);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        ArrayParser parser = arr("/a:feed/a:entry",
            obj()
                .attribute("id", "@id", with(Integer::parseInt))
                .attribute("name", "a:name")
                .attribute("birthday", "a:birthday", with(birthday -> LocalDate.parse(birthday, dateFormatter)))
                .attribute("email", "a:email")
                .attribute("phoneNumbers", arr("a:phoneNumbers/*", with(Integer::parseInt)))
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
