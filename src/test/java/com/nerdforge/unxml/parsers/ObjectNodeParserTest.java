package com.nerdforge.unxml.parsers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Guice;
import com.google.inject.Module;
import com.google.inject.testing.fieldbinder.Bind;
import com.google.inject.testing.fieldbinder.BoundFieldModule;
import com.google.inject.util.Modules;
import com.nerdforge.unxml.Parsing;
import com.nerdforge.unxml.UnXmlModule;
import com.nerdforge.unxml.parsers.builders.ObjectNodeParserBuilder;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.inject.Inject;
import java.util.function.Function;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ObjectNodeParserTest {
    @Inject
    private Parsing parsing;

    @Bind
    private Logger logger;

    @Before
    public  void before(){
        logger = mock(Logger.class);
        Module testModule = Modules.override(new UnXmlModule()).with(BoundFieldModule.of(this));
        Guice.createInjector(testModule).injectMembers(this);
    }

    @Test
    public void testParseObject() {
        String inputXmlString = "<root><id>1</id><title>mytitle</title></root>";
        Document input = parsing.xml().document(inputXmlString);

        ObjectNodeParserBuilder builder = parsing.obj()
            .attribute("id", "/root/id", parsing.with(Integer::parseInt))
            .attribute("title", "//title");

        Parser<ObjectNode> parser = builder.build();
        ObjectNode node = parser.apply(input);

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
    public void testParseObject2() {
        String inputXmlString = "<root><id>1</id><title>mytitle</title></root>";
        Document input = parsing.xml().document(inputXmlString);

        ObjectNodeParserBuilder builder = parsing.obj("root")
                .attribute("id", "id", parsing.with(Integer::parseInt))
                .attribute("title"); // test shorthand, where xpath and attribute is the same string

        Parser<ObjectNode> parser = builder.build();
        ObjectNode node = parser.apply(input);

        assertThat(node.get("id").asInt()).isEqualTo(1);
        assertThat(node.get("title").asText()).isEqualTo("mytitle");
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

        verify(logger, times(1)).warn(anyString(), eq("//entry/title"), eq("#document"));
        verify(logger, times(1)).warn(anyString(), eq("//entry/sub"), eq("#document"));
    }

    public static class Article {
        Integer id;
        String title;
    }
}