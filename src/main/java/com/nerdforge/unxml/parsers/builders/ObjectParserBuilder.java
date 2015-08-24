package com.nerdforge.unxml.parsers.builders;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.nerdforge.unxml.parsers.ObjectParser;
import com.nerdforge.unxml.parsers.SimpleParsers;
import com.nerdforge.unxml.parsers.Parser;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ObjectParserBuilder {
    private SimpleParsers simpleParsers;
    @Inject
    public ObjectParserBuilder(SimpleParsers simpleParsers) {
        this.simpleParsers = simpleParsers;
    }

    private Map<String, Parser> attributes = new HashMap<>();
    private Optional<String> xpath = Optional.empty();

    /**
     * Spesify the xpath to the root node, to parse attributes from
     * @param root The xpath to the root node
     * @return The builder itself, so commands can be chained
     */
    public ObjectParserBuilder xpath(String root){
        this.xpath = Optional.of(root);
        return this;
    }

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
        return attribute(key, node -> simpleParsers.elementParser(xpath, parser).apply(node));
    }

    public ObjectParser build(){
        return new ObjectParser(xpath.map(this::wrapAttributes).orElse(attributes));
    }

    private Map<String, Parser> wrapAttributes(String path){
        return Maps.transformEntries(attributes, (key, parser) -> simpleParsers.elementParser(path, parser));
    }
}