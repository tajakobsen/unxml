package com.nerdforge.unxml.parsers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;
import org.w3c.dom.Document;

import static com.nerdforge.unxml.Parsers.*;
import static org.fest.assertions.Assertions.*;

public class SimpleParsersTest {

    @Test
    public void testMissingXmlNode(){
        String content = "<root></root>";
        Document input = document(content);

        ObjectParser parser = obj().attribute("id", "/root/entry/id").build();

        ObjectNode node = parser.apply(input);
        assertThat(node.at("/id").isNull()).isTrue();
    }

    @Test
    public void testParseNumber(){
        String content = "<root><id>1</id></root>";
        Document input = document(content);

        ObjectParser parser = obj()
                .attribute("id", "/root/id", numberParser())
                .attribute("missing", "/root/missing", numberParser())
                .build();

        ObjectNode node = parser.apply(input);
        assertThat(node.at("/id").asInt()).isEqualTo(1);
        assertThat(node.at("/missing").isNull()).isTrue();
    }
}
