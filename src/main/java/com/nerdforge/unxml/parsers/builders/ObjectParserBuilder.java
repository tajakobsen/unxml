package com.nerdforge.unxml.parsers.builders;

import com.fasterxml.jackson.databind.node.NullNode;
import com.google.inject.Inject;
import com.nerdforge.unxml.parsers.ObjectParser;
import com.nerdforge.unxml.parsers.SimpleParsers;
import com.nerdforge.unxml.parsers.Parser;
import com.nerdforge.unxml.xml.XmlUtil;

import java.util.HashMap;
import java.util.Map;

public class ObjectParserBuilder {
    private SimpleParsers simpleParsers;
    private XmlUtil xmlUtil;

    @Inject
    public ObjectParserBuilder(SimpleParsers simpleParsers,  XmlUtil xmlUtil) {
        this.simpleParsers = simpleParsers;
        this.xmlUtil = xmlUtil;
    }

    private Map<String, Parser> attributes = new HashMap<>();

    /**
     * Specify an attribute by key, that reads the text value on an xpath
     * @param key The key in the resulting ObjectNode
     * @param xpath The xpath to the node with a text value
     * @return The builder itself, so commands can be chained
     */
    public ObjectParserBuilder attribute(String key, String xpath){
        return attribute(key, xpath, simpleParsers.textParser());
    }

    /**
     * Specify an attribute by key, and another Parser that can read an Object/Array/Value
     * @param key The key in the resulting ObjectNode
     * @param parser A Parser that can parse child nodes of the node passed to *this* parser.
     * @return The builder itself, so commands can be chained
     */
    public ObjectParserBuilder attribute(String key, Parser parser) {
        attributes.put(key, parser);
        return this;
    }

    /**
     * Specify an attribute by key, and a ObjectParserBuilder, that can read a child node, of the
     * one passed to this Parser.
     * @param key The key in the resulting ObjectNode
     * @param xpath The xpath to a Node that will be passed to the child object
     * @param builder A builder that will create a Parser that can read the child object
     * @return The builder itself, so commands can be chained
     */
    public ObjectParserBuilder attribute(String key, String xpath, ObjectParserBuilder builder){
        return attribute(key, xpath, builder.build());
    }

    /**
     * Specify an attribute by key, and a ObjectParserBuilder, that can read a child node, of the
     * one passed to this Parser.
     * @param key The key in the resulting ObjectNode
     * @param xpath The xpath to a Node that will be passed to the child object
     * @param parser A Parser that can parse child nodes of the node passed to *this* parser.
     * @return The builder itself, so commands can be chained
     */
    public ObjectParserBuilder attribute(String key, String xpath, Parser parser){
        return attribute(key, node -> xmlUtil.parseNode(xpath, node)
                .map(parser)
                .orElse(NullNode.getInstance()));
    }

    public ObjectParser build(){
        return new ObjectParser(attributes);
    }
}
