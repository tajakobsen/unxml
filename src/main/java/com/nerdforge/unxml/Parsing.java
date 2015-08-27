package com.nerdforge.unxml;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.nerdforge.unxml.json.JsonUtil;
import com.nerdforge.unxml.parsers.ArrayNodeParser;
import com.nerdforge.unxml.factory.ArrayParserFactory;
import com.nerdforge.unxml.parsers.Parser;
import com.nerdforge.unxml.parsers.SimpleParsers;
import com.nerdforge.unxml.parsers.builders.ObjectNodeParserBuilder;
import com.nerdforge.unxml.xml.XmlUtil;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.function.Function;

public class Parsing {
    private final ArrayParserFactory arrayParserFactory;
    private final SimpleParsers simpleParsers;
    private Provider<ObjectNodeParserBuilder> objectParserBuilder;
    private final XmlUtil xmlUtil;

    @Inject
    public Parsing(ArrayParserFactory arrayParserFactory, SimpleParsers simpleParsers, Provider<ObjectNodeParserBuilder> objectParserBuilder, XmlUtil xmlUtil){
        this.arrayParserFactory = arrayParserFactory;
        this.simpleParsers = simpleParsers;
        this.objectParserBuilder = objectParserBuilder;
        this.xmlUtil = xmlUtil;
    }

    /**
     * Returns a new ObjectNodeParserBuilder.
     * @return  A new instance of ObjectNodeParserBuilder
     */
    public ObjectNodeParserBuilder obj(){
        return objectParserBuilder.get();
    }

    /**
     * Returns a new ObjectNodeParserBuilder.
     * @param xpath Root xpath to start parsing from
     * @return  A new instance of ObjectNodeParserBuilder
     */
    public ObjectNodeParserBuilder obj(String xpath){
        return objectParserBuilder.get().xpath(xpath);
    }


    /**
     * Returns an ArrayNodeParser which will process a Node into an ArrayNode of Strings
     * @param xpath The Xpath pointing a list of Nodes that will become the array
     * @return A new instance of ArrayNodeParser
     */
    public Parser<ArrayNode> arr(String xpath){
        return arrayParserFactory.create(xpath, simpleParsers.textParser());
    }

    /**
     * Returns an ArrayNodeParser which will process a Node into an ArrayNode of Objects
     * @param xpath The Xpath pointing a list of Nodes that will become the array
     * @param builder A ObjectNodeParserBuilder that is configured to Parse Objects, which will
     *                become children of the Array.
     * @return A new instance of ArrayNodeParser
     */
    public Parser<ArrayNode> arr(String xpath, ObjectNodeParserBuilder builder){
        return arrayParserFactory.create(xpath, builder.build());
    }

    /**
     * Returns an ArrayNodeParser which will process a Node into an ArrayNode of Objects/Arrays/Values
     * @param xpath The Xpath pointing a list of Nodes that will become the array
     * @param parser A Parser that will parse Objects/Arrays/Values, which will
     *                become children of the Array.
     * @return A new instance of ArrayNodeParser
     */
    public Parser<ArrayNode> arr(String xpath, Parser<?> parser){
        return arrayParserFactory.create(xpath, parser);
    }

    /**
     * Use this method to pass in a function reference for doing an operation on a String value
     * read from the node, before it is mapped to a JsonNode.
     * @param transformer A reference to at method that mapps from String to Object, that will be
     *                    applied to the string value of the Node.
     * @return The function wrapped, and ready to be passed as a parameter
     */
    public Parser<JsonNode> with(Function<String, Object> transformer){
        return simpleParsers.textParser(transformer);
    }

    /**
     * Returns some XML utility methods
     * @return An instance of XmlUtil
     */
    public XmlUtil xml(){
        return xmlUtil;
    }

    /**
     * Returns an object with some simple preconfigured parsers
     * @return An instance of SimpleParsers
     */
    public SimpleParsers simple(){
        return simpleParsers;
    }
}