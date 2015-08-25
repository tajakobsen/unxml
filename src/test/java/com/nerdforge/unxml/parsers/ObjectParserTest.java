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
        parsing = ParsingFactory.getInstance().create();
    }

    @Test
    public void testParseObject() throws Exception {
        String content = "<root><id>1</id><title>mytitle</title></root>";
        Document input = parsing.xml().document(content);

        Parser parser = parsing.obj()
            .attribute("id", "/root/id", parsing.with(Integer::parseInt))
            .attribute("title", "//title")
            .build();

        JsonNode node = parser.apply(input);

        assertThat(node.get("id").asInt()).isEqualTo(1);
        assertThat(node.get("title").asText()).isEqualTo("mytitle");
        assertThat(node.toString()).isEqualTo("{\"id\":1,\"title\":\"mytitle\"}");
    }

    @Test
    public void testParseSubObject() throws Exception {
        String content = "<root><entry><title>parent</title><sub><id>1</id><title>mytitle</title></sub></entry></root>";
        Document input = parsing.xml().document(content);

        Parser parser = parsing.obj()
            .attribute("title", "//entry/title")
            .attribute("sub", "//entry/sub",
                parsing.obj()
                    .attribute("id", "id")
                    .attribute("title", "title"))
            .build();

        JsonNode node = parser.apply(input);

        assertThat(node.path("title").asText()).isEqualTo("parent");
        assertThat(node.at("/sub/id").asText()).isEqualTo("1");
        assertThat(node.at("/sub/title").asText()).isEqualTo("mytitle");
    }

    @Test
    public void testParseSubObjectNoExist() throws Exception {
        Document input = parsing.xml().document("<root><entry></entry></root>"); // no <sub></sub>

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