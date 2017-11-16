package com.nerdforge.unxml.xml;

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
import org.xml.sax.SAXParseException;

import javax.inject.Inject;
import static org.fest.assertions.Fail.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class XmlUtilTest {
    @Bind
    private Logger logger;

    @Inject
    private Parsing parsing;
    private XmlUtil util;

    @Before
    public void before(){
        logger = mock(Logger.class);
        Module testModule = Modules.override(new UnXmlModule()).with(BoundFieldModule.of(this));
        Guice.createInjector(testModule).injectMembers(this);
        util = parsing.xml();
    }

    @Test
    public void testParseInvalidXml() {
        try {
            String noEndTag = "<root>";
            util.document(noEndTag);
            //noinspection ThrowableNotThrown
            fail("SAXParseException should have been thrown");
        } catch(RuntimeException e){
            verify(logger, times(1)).error(anyString(), any(SAXParseException.class));
        }
    }

    @Test(expected = RuntimeException.class)
    public void testParseWithInvalidXPath(){
        Document node = util.document("<root></root>");
        util.parseNode("should*fail", node);
    }
}
