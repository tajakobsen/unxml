package com.nerdforge.unxml.parsers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nerdforge.unxml.Parsing;
import com.nerdforge.unxml.factory.ParsingFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;

import java.util.function.Function;

import static org.fest.assertions.Assertions.*;

public class SimpleParsersTest {
    private static Parsing parsing;

    @BeforeClass
    public static void before(){
        parsing = ParsingFactory.getInstance().create();
    }

    @Test
    public void testMissingXmlNode(){
        String content = "<root></root>";
        Document input = parsing.xml().document(content);

        ObjectParser parser = parsing.obj().attribute("id", "/root/entry/id").build();

        ObjectNode node = parser.apply(input);
        assertThat(node.at("/id").isNull()).isTrue();
    }

    @Test
    public void testParseNumber(){
        String content = "<root><id>1</id></root>";
        Document input = parsing.xml().document(content);
        Parser numberParser = parsing.simple().numberParser();

        ObjectParser parser = parsing.obj()
                .attribute("id", "/root/id", numberParser)
                .attribute("missing", "/root/missing", numberParser)
                .build();

        ObjectNode node = parser.apply(input);
        assertThat(node.at("/id").asInt()).isEqualTo(1);
        assertThat(node.at("/missing").isNull()).isTrue();
    }
}
