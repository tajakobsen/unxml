package com.nerdforge.unxml.parsers;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.junit.Test;
import org.w3c.dom.Document;

import static com.nerdforge.unxml.Parsers.*;
import static org.fest.assertions.Assertions.*;

public class ArrayParserTest {
    @Test
    public void testParseArray() throws Exception {
        String content = "<root>" +
                "<entry></entry>" +
                "<entry>" +
                    "<list>" +
                        "<value>x</value>" +
                        "<value>y</value>" +
                    "</list>" +
                "</entry>" +
                "</root>";
        Document input = document(content);


        ArrayParser parser = arr("/root/entry", arr("list/value"));

        ArrayNode node = parser.apply(input);
        assertThat(node.size()).isEqualTo(2);
        assertThat(node.at("/1/0").asText()).isEqualTo("x");
        assertThat(node.at("/1/1").asText()).isEqualTo("y");
    }

    @Test
    public void testParseArrayWithObjects() throws Exception {
        String content = "<root>" +
                    "<entry>" +
                        "<id>1</id>" +
                        "<title>mytitle</title>" +
                    "</entry><entry>" +
                        "<id>2</id>" +
                        "<title>mytitle2</title>" +
                    "</entry>" +
                "</root>";
        Document input = document(content);

        ArrayParser parser = arr("//entry", obj()
                .attribute("id", "id", with(Integer::parseInt))
                .attribute("title", "title")
                .build());


        ArrayNode node = parser.apply(input);

        assertThat(node.size()).isEqualTo(2);
        assertThat(node.at("/0/id").asInt()).isEqualTo(1);
        assertThat(node.at("/0/title").asText()).isEqualTo("mytitle");
    }
}
