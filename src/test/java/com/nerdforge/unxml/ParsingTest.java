package com.nerdforge.unxml;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.nerdforge.unxml.factory.ParsingFactory;
import com.nerdforge.unxml.parsers.ArrayNodeParser;
import com.nerdforge.unxml.parsers.Parser;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;

import java.io.File;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import static org.fest.assertions.Assertions.assertThat;

public class ParsingTest {
    private static Parsing parsing;

    @BeforeClass
    public static void before(){
        Map<String, String> namespaces = new HashMap<String, String>(){{ // (1)
            put("a", "http://www.w3.org/2005/Atom");
            put("app", "http://www.w3.org/2007/app");
        }};
        parsing = ParsingFactory.getInstance(namespaces).create();
    }

    @Test
    public void testParseObjectFromFile() {
        File file = Paths.get("src/test/xml/homer.xml").toFile();
        Document input = parsing.xml().document(file);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Parser dateParser = parsing.simple().dateParser(formatter); // (2a)

        Parser<ArrayNode> parser = parsing.arr("/a:feed/a:entry",
                parsing.obj()
                        .attribute("id", "@id", parsing.with(Integer::parseInt)) // (3)
                        .attribute("name", "a:name")
                        .attribute("birthday", "a:birthday", dateParser) // (2b)
                        .attribute("email", "app:email") // (4)
                        .attribute("phoneNumbers", parsing.arr("a:phoneNumbers/*", parsing.with(Integer::parseInt))) // (5)
        ).build();

        ArrayNode node = parser.apply(input);
        assertThat(node.at("/0/id").asInt()).isEqualTo(1);
        assertThat(node.at("/0/name").asText()).isEqualTo("Homer Simpson");
        assertThat(node.at("/0/birthday/0").asInt()).isEqualTo(1956);
        assertThat(node.at("/0/birthday/1").asInt()).isEqualTo(3);
        assertThat(node.at("/0/birthday/2").asInt()).isEqualTo(1);
        assertThat(node.at("/0/email").asText()).isEqualTo("chunkylover53@aol.com");
        assertThat(node.at("/0/phoneNumbers/0").asInt()).isEqualTo(5551234);
        assertThat(node.at("/0/phoneNumbers/1").asInt()).isEqualTo(5555678);
        assertThat(node.at("/0/phoneNumbers/2").asInt()).isEqualTo(5559991);
    }
}
