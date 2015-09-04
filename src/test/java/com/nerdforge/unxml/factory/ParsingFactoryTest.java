package com.nerdforge.unxml.factory;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class ParsingFactoryTest {

    @Test
    public void testGetInstance(){
        ParsingFactory.getInstance().create();
    }

    @Test
    public void testGetInstanceWithNamespace(){
        Map<String, String> namespace = new HashMap<>();
        ParsingFactory.getInstance(namespace).create();
    }
}
