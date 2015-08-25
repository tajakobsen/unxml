package com.nerdforge.unxml.parsers;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.nerdforge.unxml.Parsing;
import com.nerdforge.unxml.factory.ParsingFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import static org.fest.assertions.Assertions.*;

public class ArrayParserTest {
    private static Parsing parsing;

    @BeforeClass
    public static void before(){
        parsing = ParsingFactory.getInstance().create();
    }

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
        Document input = parsing.xml().document(content);

        ArrayParser parser = parsing.arr("/root/entry", parsing.arr("list/value"));

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
        Document input = parsing.xml().document(content);

        ArrayParser parser = parsing.arr("//entry", parsing.obj()
                .attribute("id", "id", parsing.with(Integer::parseInt))
                .attribute("title", "title")
                .build());

        ArrayNode node = parser.apply(input);

        assertThat(node.size()).isEqualTo(2);
        assertThat(node.at("/0/id").asInt()).isEqualTo(1);
        assertThat(node.at("/0/title").asText()).isEqualTo("mytitle");
    }
}
