package com.nerdforge.unxml.parsers.builders;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.nerdforge.unxml.factory.ArrayNodeParserBuilderFactory;
import com.nerdforge.unxml.parsers.ObjectParser;
import com.nerdforge.unxml.parsers.Parser;

import java.util.List;

/**
 * Creates a ParserBuilder with an array with an object inside.
 */
public class ObjectArrayParserBuilder implements ParserBuilder<ArrayNode> {
    private final String xpath;
    private ArrayNodeParserBuilderFactory factory;
    private ObjectNodeParserBuilder builder;

    @Inject
    public ObjectArrayParserBuilder(@Assisted String xpath, ArrayNodeParserBuilderFactory factory, ObjectNodeParserBuilder builder) {
        this.xpath = xpath;
        this.factory = factory;
        this.builder = builder;
    }

    /**
     * Special case where the xpath == json attribute name.
     * @param xpath The xpath to the node with a text value, and the key in the resulting ObjectNode
     * @return The builder itself, so commands can be chained
     */
    public ObjectArrayParserBuilder attribute(String xpath){
        builder.attribute(xpath);
        return this;
    }

    /**
     * Specify an attribute by key, that reads the text value on an xpath
     * @param key The key in the resulting ObjectNode
     * @param xpath The xpath to the node with a text value
     * @return The builder itself, so commands can be chained
     */
    public ObjectArrayParserBuilder attribute(String key, String xpath){
        builder.attribute(key, xpath);
        return this;
    }

    /**
     * Specify an attribute by key, and another Parser that can read an Object/Array/Value
     * @param key The key in the resulting ObjectNode
     * @param parser A Parser that can parse child nodes of the node passed to *this* parser.
     * @return The builder itself, so commands can be chained
     */
    public ObjectArrayParserBuilder attribute(String key, Parser<?> parser) {
        builder.attribute(key, parser);
        return this;
    }

    /**
     * Specify an attribute by key, and a ObjectNodeParserBuilder, that can read a child node, of the
     * one passed to this Parser.
     * @param key The key in the resulting ObjectNode
     * @param builder A builder that will create a Parser that can read the child object
     * @return The builder itself, so commands can be chained
     */
    public ObjectArrayParserBuilder attribute(String key, ParserBuilder builder){
        this.builder.attribute(key, builder);
        return this;
    }

    /**
     * Specify an attribute by key, and a ObjectNodeParserBuilder, that can read a child node, of the
     * one passed to this Parser.
     * @param key The key in the resulting ObjectNode
     * @param xpath The xpath to a Node that will be passed to the child object
     * @param builder A builder that will create a Parser that can read the child object
     * @return The builder itself, so commands can be chained
     */
    public ObjectArrayParserBuilder attribute(String key, String xpath, ParserBuilder builder){
        this.builder.attribute(key, xpath, builder);
        return this;
    }

    /**
     * Specify an attribute by key, and a ObjectNodeParserBuilder, that can read a child node, of the
     * one passed to this Parser.
     * @param key The key in the resulting ObjectNode
     * @param xpath The xpath to a Node that will be passed to the child object
     * @param parser A Parser that can parse child nodes of the node passed to *this* parser.
     * @return The builder itself, so commands can be chained
     */
    public ObjectArrayParserBuilder attribute(String key, String xpath, Parser<?> parser){
        builder.attribute(key, xpath, parser);
        return this;
    }

    @Override
    public Parser<ArrayNode> build() {
        return factory.create(xpath, builder.build()).build();
    }

    /**
     * Returns the parsed result as a List of Objects of class A
     * @param valueType  The type of class that should be instansiated in the list
     * @param <A> The Class the ListParser will return a list of.
     * @return Result as a List of Objects of class A
     */
    public <A> ObjectParser<List<A>> as(Class<A> valueType){
        return factory.create(xpath, builder.build()).as(valueType);
    }
}
