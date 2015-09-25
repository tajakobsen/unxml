package com.nerdforge.unxml;

import com.fasterxml.jackson.databind.JsonNode;
import com.nerdforge.unxml.factory.ArrayNodeParserBuilderFactory;
import com.nerdforge.unxml.factory.ObjectArrayParserBuilderFactory;
import com.nerdforge.unxml.json.JsonUtil;
import com.nerdforge.unxml.parsers.Parser;
import com.nerdforge.unxml.parsers.SimpleParsers;
import com.nerdforge.unxml.parsers.builders.ArrayNodeParserBuilder;
import com.nerdforge.unxml.parsers.builders.ObjectArrayParserBuilder;
import com.nerdforge.unxml.parsers.builders.ObjectNodeParserBuilder;
import com.nerdforge.unxml.parsers.builders.ParserBuilder;
import com.nerdforge.unxml.xml.XmlUtil;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.function.Function;
import java.util.function.Supplier;

public class Parsing {
    private final ArrayNodeParserBuilderFactory arrayNodeFactory;
    private final SimpleParsers simpleParsers;
    private Provider<ObjectNodeParserBuilder> objectParserBuilder;
    private final XmlUtil xmlUtil;
    private final JsonUtil jsonUtil;
    private final ObjectArrayParserBuilderFactory objectArrayParserBuilderFactory;

    @Inject
    public Parsing(ArrayNodeParserBuilderFactory arrayNodeFactory, SimpleParsers simpleParsers, Provider<ObjectNodeParserBuilder> objectParserBuilder, XmlUtil xmlUtil, JsonUtil jsonUtil, ObjectArrayParserBuilderFactory objectArrayParserBuilderFactory){
        this.arrayNodeFactory = arrayNodeFactory;
        this.simpleParsers = simpleParsers;
        this.objectParserBuilder = objectParserBuilder;
        this.xmlUtil = xmlUtil;
        this.jsonUtil = jsonUtil;
        this.objectArrayParserBuilderFactory = objectArrayParserBuilderFactory;
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
     * @return A new instance of ObjectArrayParserBuilder
     */
    public ObjectArrayParserBuilder arr(String xpath){
        return objectArrayParserBuilderFactory.create(xpath);
    }

    /**
     * Returns an ArrayNodeParser which will process a Node into an ArrayNode of Objects
     * @param xpath The Xpath pointing a list of Nodes that will become the array
     * @param builder A ObjectNodeParserBuilder that is configured to Parse Objects, which will
     *                become children of the Array.
     * @return A new instance of ArrayNodeParser
     */
    public ArrayNodeParserBuilder arr(String xpath, ParserBuilder builder){
        return arrayNodeFactory.create(xpath, builder.build());
    }

    /**
     * Returns an ArrayNodeParser which will process a Node into an ArrayNode of Objects/Arrays/Values
     * @param xpath The Xpath pointing a list of Nodes that will become the array
     * @param parser A Parser that will parse Objects/Arrays/Values, which will
     *                become children of the Array.
     * @return A new instance of ArrayNodeParser
     */
    public ArrayNodeParserBuilder arr(String xpath, Parser<?> parser){
        return arrayNodeFactory.create(xpath, parser);
    }

    /**
     * Utility method for getting a simple text parser
     * @return A parser what will return the text of the parent Parser.
     */
    public Parser<JsonNode> text(){
        return simpleParsers.textParser();
    }

    /**
     * Utility method for getting a simple number parser (Parses text as double)
     * @return A parser what will return the text of the parent Parser.
     */
    public Parser<JsonNode> number(){
        return simpleParsers.numberParser();
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
     * Creates a supplier that will get the parser lazily. This can be used for recursive parsing.
     * @param supplier The Supplier that will privde the node at parse time.
     * @return A Parser instance, that will unpack the supplied parser at parse time.
     */
    public Parser<JsonNode> with(Supplier<Parser<?>> supplier){
        return node -> supplier.get().apply(node);
    }

    /**
     * Returns some XML utility methods
     * @return An instance of XmlUtil
     */
    public XmlUtil xml(){
        return xmlUtil;
    }

    /**
     * Returns some JSON utility methods
     * @return An instance of JsonUtil
     */
    public JsonUtil json(){
        return jsonUtil;
    }


    /**
     * Returns an object with some simple preconfigured parsers
     * @return An instance of SimpleParsers
     */
    public SimpleParsers simple(){
        return simpleParsers;
    }
}