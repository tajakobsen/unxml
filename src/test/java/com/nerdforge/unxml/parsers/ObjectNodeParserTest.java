package com.nerdforge.unxml.parsers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nerdforge.unxml.Parsing;
import com.nerdforge.unxml.factory.ParsingFactory;
import com.nerdforge.unxml.parsers.builders.ObjectNodeParserBuilder;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.function.Function;

import static org.fest.assertions.Assertions.assertThat;

public class ObjectNodeParserTest {
    private static Parsing parsing;

    @BeforeClass
    public static void before(){
        parsing = ParsingFactory.getInstance().create();
    }

    @Test
    public void testParseObject() {
        String inputXmlString = "<root><id>1</id><title>mytitle</title></root>";
        Document input = parsing.xml().document(inputXmlString);

        ObjectNodeParserBuilder builder = parsing.obj()
            .attribute("id", "/root/id", parsing.with(Integer::parseInt))
            .attribute("title", "//title");

        Parser<ObjectNode> parser = builder.build();
        JsonNode node = parser.apply(input);

        assertThat(node.get("id").asInt()).isEqualTo(1);
        assertThat(node.get("title").asText()).isEqualTo("mytitle");
        assertThat(node.toString()).isEqualTo("{\"id\":1,\"title\":\"mytitle\"}");

        // Creating object
        Function<Node, Article> articleParser = builder.as(Article.class);
        Article article = articleParser.apply(input);

        assertThat(article.id).isEqualTo(1);
        assertThat(article.title).isEqualTo("mytitle");
    }

    @Test
    public void testParseSubObject() {
        String inputXmlString = "<root><entry><title>parent</title><sub><id>1</id><title>mytitle</title></sub></entry></root>";
        Document input = parsing.xml().document(inputXmlString);

        Parser<ObjectNode> parser = parsing.obj()
            .attribute("title", "//entry/title")
            .attribute("sub", "//entry/sub",
                parsing.obj()
                    .attribute("id", "id")
                    .attribute("title", "title"))
                .build();

        ObjectNode node = parser.apply(input);

        assertThat(node.path("title").asText()).isEqualTo("parent");
        assertThat(node.at("/sub/id").asText()).isEqualTo("1");
        assertThat(node.at("/sub/title").asText()).isEqualTo("mytitle");
    }

    @Test
    public void testParseSubObjectNoExist() {
        Document input = parsing.xml().document("<root><entry></entry></root>"); // no <sub></sub>

        Parser<ObjectNode> parser = parsing.obj()
            .attribute("title", "//entry/title")
            .attribute("sub", "//entry/sub",
                parsing.obj()
                    .attribute("id", "id")
                    .attribute("title", "title"))
                .build();
        ObjectNode node = parser.apply(input);
        assertThat(node.path("sub").isNull()).isEqualTo(Boolean.TRUE);
    }

    public static class Article {
        public Integer id;
        public String title;
    }
}