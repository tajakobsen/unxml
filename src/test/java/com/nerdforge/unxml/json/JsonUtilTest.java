package com.nerdforge.unxml.json;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Guice;
import com.nerdforge.unxml.Parsing;
import com.nerdforge.unxml.UnXmlModule;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;

import static org.fest.assertions.Assertions.assertThat;

public class JsonUtilTest {
    @Inject
    private Parsing parsing;
    private JsonUtil json;
    private ObjectNode emptyObjectNode;

    @Before
    public void before(){
        Guice.createInjector(new UnXmlModule()).injectMembers(this);
        json = parsing.json();
        emptyObjectNode = json.mapper().createObjectNode();
    }

    @Test
    public void testAs(){
        ObjectNode node = json.mapper().createObjectNode()
                .put("id", 1)
                .put("title", "myTitle");

        Foo foo = json.as(Foo.class).apply(node);

        assertThat(foo.id).isEqualTo(1);
        assertThat(foo.title).isEqualTo("myTitle");
    }

    @Test
    public void testAsWithEmpty(){
        Foo foo = json.as(Foo.class).apply(emptyObjectNode);
        assertThat(foo.id).isEqualTo(0);
        assertThat(foo.title).isEqualTo(null);
        // TODO Get exception if missing
    }

    @Test
    public void testAsListWithEmpty(){
        Foo foo = json.as(Foo.class).apply(emptyObjectNode);
        assertThat(foo.id).isEqualTo(0);
        assertThat(foo.title).isEqualTo(null);
    }

    @Test(expected = RuntimeException.class)
    public void testAsWithUnavailableClass(){
        json.as(Bar.class).apply(emptyObjectNode);
    }

    @Test(expected = RuntimeException.class)
    public void testAsListWithUnavailableClass(){
        ArrayNode arr = json.mapper().createArrayNode().add(emptyObjectNode);
        json.asList(Bar.class).apply(arr);
    }

    public static class Foo {
        public int id;
        public String title;
    }

    private class Bar {
        public Integer id;
        public String title;
    }
}
