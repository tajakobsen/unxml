package com.nerdforge.unxml.xml;


import java.util.HashMap;
import java.util.Map;

import com.nerdforge.unxml.xml.SimpleNamespaceContext;
import org.junit.BeforeClass;
import org.junit.Test;
import static javax.xml.XMLConstants.*;
import static org.fest.assertions.Assertions.assertThat;

public class SimpleNamespaceContextTest {
    public static SimpleNamespaceContext context;

    @BeforeClass
    public static void init(){
        Map<String, String> mappings = new HashMap<>();
        mappings.put("foo", "http://foo");
        mappings.put("foo2", "http://foo");
        mappings.put("bar", "http://bar");
        mappings.put(XML_NS_PREFIX, XML_NS_URI);
        context = new SimpleNamespaceContext(mappings);
    }

    @Test
    public void testGetNamespaceURI() {
        assertThat(context.getNamespaceURI("foo")).isEqualTo("http://foo");
        assertThat(context.getNamespaceURI("foo2")).isEqualTo("http://foo");
        assertThat(context.getNamespaceURI("bar")).isEqualTo("http://bar");
        assertThat(context.getNamespaceURI("none")).isEqualTo(NULL_NS_URI);
        assertThat(context.getNamespaceURI(DEFAULT_NS_PREFIX)).isEqualTo(NULL_NS_URI);
    }

    @Test
    public void testGetPrefix() {
        assertThat(context.getPrefix("http://foo")).isIn("foo", "foo2");
        assertThat(context.getPrefix("http://bar")).isEqualTo("bar");
        assertThat(context.getPrefix("http://not-created")).isEqualTo(null);
    }

    @Test
    public void testGetPrefixes() {
        assertThat(context.getPrefixes("http://foo")).contains("foo", "foo2");
    }

    @Test
    public void testConstants() {
        // namespaces
        assertThat(context.getNamespaceURI(XML_NS_PREFIX)).isEqualTo(XML_NS_URI);
        assertThat(context.getNamespaceURI(XMLNS_ATTRIBUTE)).isEqualTo(XMLNS_ATTRIBUTE_NS_URI);

        // prefix
        assertThat(context.getPrefix(XML_NS_URI)).isEqualTo(XML_NS_PREFIX);
        assertThat(context.getPrefix(XMLNS_ATTRIBUTE_NS_URI)).isEqualTo(XMLNS_ATTRIBUTE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNamespaceURIWithNull(){
        context.getNamespaceURI(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetPrefixWithNull(){
        context.getPrefix(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetPrefixesWithNull(){
        context.getPrefixes(null);
    }
}