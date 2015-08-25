package com.nerdforge.unxml.parsers;

import com.fasterxml.jackson.databind.JsonNode;
import com.nerdforge.unxml.Parsing;
import com.nerdforge.unxml.factory.ParsingFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;

import static org.fest.assertions.Assertions.assertThat;

public class ObjectParserTest {
    private static Parsing parsing;

    @BeforeClass
    public static void before(){
        parsing = ParsingFactory.getInstance().get();
    }

    @Test
    public void testParseObject() throws Exception {
        String sinmpleContent = "<root><id>1</id><title>mytitle</title></root>";

        Parser parser = parsing.obj()
                .attribute("id", "/root/id", parsing.with(Integer::parseInt))
                .attribute("title", "//title")
                .build();

        Document input = parsing.xml().document(sinmpleContent);
        JsonNode node = parser.apply(input);

        assertThat(node.get("id").asInt()).isEqualTo(1);
        assertThat(node.get("title").asText()).isEqualTo("mytitle");
        assertThat(node.toString()).isEqualTo("{\"id\":1,\"title\":\"mytitle\"}");
    }

    @Test
    public void testParseSubObject() throws Exception {
        String sinmpleContent = "<root><entry><title>parent</title><sub><id>1</id><title>mytitle</title></sub></entry></root>";

        Parser parser = parsing.obj()
                .attribute("title", "//entry/title")
                .attribute("sub", "//entry/sub",
                        parsing.obj()
                        .attribute("id", "id")
                        .attribute("title", "title"))
                .build();

        Document input = parsing.xml().document(sinmpleContent);
        JsonNode node = parser.apply(input);

        assertThat(node.path("title").asText()).isEqualTo("parent");
        assertThat(node.at("/sub/id").asText()).isEqualTo("1");
        assertThat(node.at("/sub/title").asText()).isEqualTo("mytitle");
    }

    @Test
    public void testParseSubObjectNoExist() throws Exception {
        String sinmpleContent = "<root><entry></entry></root>"; // no <sub></sub>
        Document input = parsing.xml().document(sinmpleContent);

        Parser parser = parsing.obj()
                .attribute("title", "//entry/title")
                .attribute("sub", "//entry/sub",
                        parsing.obj()
                                .attribute("id", "id")
                                .attribute("title", "title"))
                .build();
        JsonNode node = parser.apply(input);
        assertThat(node.path("sub").isNull()).isEqualTo(Boolean.TRUE);
    }
}