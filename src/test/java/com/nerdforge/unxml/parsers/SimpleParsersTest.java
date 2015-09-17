package com.nerdforge.unxml.parsers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Guice;
import com.google.inject.Module;
import com.google.inject.testing.fieldbinder.Bind;
import com.google.inject.testing.fieldbinder.BoundFieldModule;
import com.google.inject.util.Modules;
import com.nerdforge.unxml.Parsing;
import com.nerdforge.unxml.UnXmlModule;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.w3c.dom.Document;
import javax.inject.Inject;
import static org.fest.assertions.Assertions.*;
import static org.mockito.Mockito.*;

public class SimpleParsersTest {
    @Inject
    private Parsing parsing;

    @Bind
    private Logger logger;

    @Before
    public void before(){
        logger = mock(Logger.class);
        Module testModule = Modules.override(new UnXmlModule()).with(BoundFieldModule.of(this));
        Guice.createInjector(testModule).injectMembers(this);
    }

    @Test
    public void testMissingXmlNode(){
        String content = "<root></root>";
        Document input = parsing.xml().document(content);

        Parser<ObjectNode> parser = parsing.obj().attribute("id", "/root/entry/id").build();

        ObjectNode node = parser.apply(input);
        assertThat(node.at("/id").isNull()).isTrue();

        // verify logger.warn(...)
        verify(logger, times(1)).warn(anyString(), eq("/root/entry/id"), eq("#document"));
    }

    @Test
    public void testParseNumber(){
        String content = "<root><id>1</id></root>";
        Document input = parsing.xml().document(content);
        Parser numberParser = parsing.simple().numberParser();

        Parser<ObjectNode> parser = parsing.obj()
                .attribute("id", "/root/id", numberParser)
                .attribute("missing", "/root/missing", numberParser)
                .build();

        ObjectNode node = parser.apply(input);
        assertThat(node.at("/id").asInt()).isEqualTo(1);
        assertThat(node.at("/missing").isNull()).isTrue();

        // verify logger.warn(...)
        verify(logger, times(1)).warn(anyString(), eq("/root/missing"), eq("#document"));
    }

    @Test
    public void testNodeNameParser(){
        String content = "<root><id>1</id></root>";
        Document input = parsing.xml().document(content);
        Parser nodeNameParser = parsing.simple().nodeNameParser();

        Parser<ObjectNode> parser = parsing.obj()
                .attribute("foo", "/root", nodeNameParser)
                .attribute("bar", "/root/id", nodeNameParser)
                .build();

        ObjectNode node = parser.apply(input);
        assertThat(node.at("/foo").asText()).isEqualTo("root");
        assertThat(node.at("/bar").asText()).isEqualTo("id");
    }

    @Test
    public void testDateParser(){
        String content = "<root><date>2015-05-01</date></root>";
        Document input = parsing.xml().document(content);
        Parser dateParser = parsing.simple().dateParser();

        Parser<ObjectNode> parser = parsing.obj()
                .attribute("date", "/root/date", dateParser)
                .build();

        ObjectNode node = parser.apply(input);
        assertThat(node.at("/date").asText()).isEqualTo("2015-05-01");
    }
}
