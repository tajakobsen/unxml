package com.nerdforge.unxml.parsers.builders;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.nerdforge.unxml.factory.ObjectParserFactory;
import com.nerdforge.unxml.json.JsonUtil;
import com.nerdforge.unxml.parsers.InstanceParser;
import com.nerdforge.unxml.parsers.Parser;
import com.nerdforge.unxml.parsers.ObjectParser;
import com.nerdforge.unxml.parsers.SimpleParsers;
import org.w3c.dom.Node;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class ObjectParserBuilder {
    private Map<String, Parser<?>> attributes = Maps.newHashMap();
    private Optional<String> xpath = Optional.empty();
    private SimpleParsers simpleParsers;
    private ObjectParserFactory factory;
    private JsonUtil jsonUtil;

    @Inject
    public ObjectParserBuilder(SimpleParsers simpleParsers, ObjectParserFactory factory, JsonUtil jsonUtil) {
        this.simpleParsers = simpleParsers;
        this.factory = factory;
        this.jsonUtil = jsonUtil;
    }

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
    public ObjectParserBuilder attribute(String key, Parser<?> parser) {
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
    public ObjectParserBuilder attribute(String key, String xpath, Parser<?> parser){
        Parser<JsonNode> elementParser = simpleParsers.elementParser(xpath, parser);
        return attribute(key, elementParser::apply);
    }

    /**
     * Builds the ObjectParser with the attributes specified.
     * @return An ObjectParser
     */
    public ObjectParser build(){
        return factory.create(xpath.map(this::wrapAttributes).orElse(attributes));
    }

    /**
     * Uses Jackson to instansiate the ObjectNode as a Java Object of type A.
     * @param valueType The type of class that should be instansiated
     * @param <A> The Class the InstanceParser will return
     * @return A parser that outputs an object of Class A.
     */
    public <A> InstanceParser<A> as(Class<A> valueType) {
        return build().<A>andThen(jsonUtil.as(valueType))::apply;
    }

    private Map<String, Parser<?>> wrapAttributes(String path){
        return Maps.transformValues(attributes, parser -> simpleParsers.elementParser(path, parser));
    }
}