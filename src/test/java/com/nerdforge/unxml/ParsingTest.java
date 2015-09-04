package com.nerdforge.unxml;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Guice;
import com.nerdforge.unxml.factory.ParsingFactory;
import com.nerdforge.unxml.parsers.Parser;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

import javax.inject.Inject;
import java.io.File;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import static org.fest.assertions.Assertions.assertThat;

public class ParsingTest {
    @Inject
    private Parsing parsing;

    @Before
    public void before(){
        Map<String, String> namespaces = new HashMap<String, String>(){{
            put("a", "http://www.w3.org/2005/Atom");
            put("app", "http://www.w3.org/2007/app");
        }};

        Guice.createInjector(new UnXmlModule(namespaces)).injectMembers(this);
    }


    @Test
    public void test(){
        // private Parsing parsing = ParsingFactory.getInstance().create();
        String xml = "<Root><Orders><Order><CustomerID></CustomerID><EmployeeID></EmployeeID></Order></Orders></Root>";
        Document document = parsing.xml().document(xml);

        Parser<ObjectNode> parser = parsing.obj().attribute("data", "Root", recursiveParser()).build();

        System.out.println(parser.apply(document).toString());
    }

    public Parser<ObjectNode> recursiveParser(){
        return  parsing.obj()
                .attribute("text", parsing.simple().nodeNameParser())
                .attribute("children",
                        parsing.arr("node()", parsing.with(this::recursiveParser) // recursivly
                        )
                ).build();
    }

    @Test
    public void testParseObjectFromFile() {
        File file = Paths.get("src/test/xml/homer.xml").toFile();
        Document input = parsing.xml().document(file);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Parser dateParser = parsing.simple().dateParser(formatter);

        Parser<ArrayNode> parser = parsing.arr("/a:feed/a:entry",
                parsing.obj()
                        .attribute("id", "@id", parsing.with(Integer::parseInt))
                        .attribute("name", "a:name")
                        .attribute("birthday", "a:birthday", dateParser)
                        .attribute("email", "app:email")
                        .attribute("phoneNumbers", parsing.arr("a:phoneNumbers/*", parsing.with(Integer::parseInt)))
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
