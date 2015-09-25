package com.nerdforge.unxml.parsers;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.inject.Guice;
import com.nerdforge.unxml.Parsing;
import com.nerdforge.unxml.UnXmlModule;
import com.nerdforge.unxml.parsers.builders.ArrayNodeParserBuilder;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import javax.inject.Inject;
import java.util.List;
import static org.fest.assertions.Assertions.*;

public class ArrayNodeParserTest {
    @Inject
    private Parsing parsing;

    @Before
    public void before(){
        Guice.createInjector(new UnXmlModule()).injectMembers(this);
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

        Parser<ArrayNode> parser = parsing.arr("/root/entry", parsing.arr("list/value", parsing.text())).build();

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
                        "<title>myTitle</title>" +
                    "</entry><entry>" +
                        "<id>2</id>" +
                        "<title>myTitle2</title>" +
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
        assertThat(node.at("/0/title").asText()).isEqualTo("myTitle");

        // Make an object
        ObjectParser<List<Foo>> articleListParser = builder.as(Foo.class);
        List<Foo> foos = articleListParser.apply(input);

        assertThat(foos).hasSize(2);
        assertThat(foos.get(0).id).isEqualTo(1);
        assertThat(foos.get(0).title).isEqualTo("myTitle");
    }

    @Test
    public void testComboParserBuilder() {
        String content = "<root>" +
                    "<entry>" +
                        "<id>1</id>" +
                        "<title>myTitle</title>" +
                    "</entry><entry>" +
                        "<id>2</id>" +
                        "<title>myTitle2</title>" +
                    "</entry>" +
                "</root>";
        Document input = parsing.xml().document(content);

        Parser<ArrayNode> parser = parsing.arr("/root/entry")
                .attribute("id", "id", parsing.number())
                .attribute("title")
                .build();

        ArrayNode node = parser.apply(input);

        assertThat(node.size()).isEqualTo(2);
        assertThat(node.at("/0/id").asInt()).isEqualTo(1);
        assertThat(node.at("/0/title").asText()).isEqualTo("myTitle");
    }

    public static class Foo {
        public Integer id;
        public String title;
    }
}
