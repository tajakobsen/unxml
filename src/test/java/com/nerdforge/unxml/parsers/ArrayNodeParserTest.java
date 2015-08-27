package com.nerdforge.unxml.parsers;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.nerdforge.unxml.Parsing;
import com.nerdforge.unxml.factory.ParsingFactory;
import com.nerdforge.unxml.parsers.builders.ArrayNodeParserBuilder;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;

import java.util.List;

import static org.fest.assertions.Assertions.*;

public class ArrayNodeParserTest {
    private static Parsing parsing;

    @BeforeClass
    public static void before(){
        parsing = ParsingFactory.getInstance().create();
    }

    @Test
    public void testParseArray() {
        String content = "<root>" +
                "<entry></entry>" +
                "<entry>" +
                    "<list>" +
                        "<value>x</value>" +
                        "<value>y</value>" +
                    "</list>" +
                "</entry>" +
                "</root>";
        Document input = parsing.xml().document(content);

        Parser<ArrayNode> parser = parsing.arr("/root/entry", parsing.arr("list/value")).build();

        ArrayNode node = parser.apply(input);
        assertThat(node.size()).isEqualTo(2);
        assertThat(node.at("/1/0").asText()).isEqualTo("x");
        assertThat(node.at("/1/1").asText()).isEqualTo("y");
    }

    @Test
    public void testParseArrayWithObjects() {
        String content = "<root>" +
                    "<entry>" +
                        "<id>1</id>" +
                        "<title>mytitle</title>" +
                    "</entry><entry>" +
                        "<id>2</id>" +
                        "<title>mytitle2</title>" +
                    "</entry>" +
                "</root>";
        Document input = parsing.xml().document(content);

        ArrayNodeParserBuilder builder = parsing.arr("//entry", parsing.obj()
                .attribute("id", "id", parsing.with(Integer::parseInt))
                .attribute("title", "title"));

        Parser<ArrayNode> parser = builder.build();
        ArrayNode node = parser.apply(input);

        assertThat(node.size()).isEqualTo(2);
        assertThat(node.at("/0/id").asInt()).isEqualTo(1);
        assertThat(node.at("/0/title").asText()).isEqualTo("mytitle");

        // Make an object
        ObjectParser<List<Article>> articleListParser = builder.as(Article.class);
        List<Article> articles = articleListParser.apply(input);

        assertThat(articles).hasSize(2);
        assertThat(articles.get(0).id).isEqualTo(1);
        assertThat(articles.get(0).title).isEqualTo("mytitle");
    }

    public static class Article {
        public Integer id;
        public String title;
    }
}
