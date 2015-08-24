package com.nerdforge.unxml;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.nerdforge.unxml.parsers.ArrayParser;
import com.nerdforge.unxml.parsers.factory.ArrayParserFactory;
import com.nerdforge.unxml.parsers.Parser;
import com.nerdforge.unxml.parsers.SimpleParsers;
import com.nerdforge.unxml.parsers.builders.ObjectParserBuilder;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Map;
import java.util.function.Function;

public class UnXml {
    private final ArrayParserFactory arrayParserFactory;
    private final SimpleParsers simpleParsers;
    private Provider<ObjectParserBuilder> objectParserBuilder;

    @Inject
    public UnXml(ArrayParserFactory arrayParserFactory, SimpleParsers simpleParsers, Provider<ObjectParserBuilder> objectParserBuilder){
        this.arrayParserFactory = arrayParserFactory;
        this.simpleParsers = simpleParsers;
        this.objectParserBuilder = objectParserBuilder;
    }

    /**
     * Initiates a Guice injector, and returns an instance of UnXml
     * @return an instance of UnXml
     */
    public static UnXml getInstance(){
        return getInstance(null);
    }

    /**
     * Initiates a Guice injector, and returns an instance of UnXml
     * @param namespaces A map containing namespaces in the xml
     * @return an instance of UnXml
     */
    public static UnXml getInstance(Map<String, String> namespaces){
        return injector(namespaces).getInstance(UnXml.class);
    }

    /**
     * Returns a new ObjectParserBuilder.
     * @return  A new instance of ObjectParserBuilder
     */
    public ObjectParserBuilder obj(){
        return objectParserBuilder.get();
    }

    /**
     * Returns a new ObjectParserBuilder.
     * @param xpath Root xpath to start parsing from
     * @return  A new instance of ObjectParserBuilder
     */
    public ObjectParserBuilder obj(String xpath){
        return objectParserBuilder.get().xpath(xpath);
    }


    /**
     * Returns an ArrayParser which will process a Node into an ArrayNode of Strings
     * @param xpath The Xpath pointing a list of Nodes that will become the array
     * @return A new instance of ArrayParser
     */
    public ArrayParser arr(String xpath){
        return arrayParserFactory.create(xpath, simpleParsers.textParser());
    }

    /**
     * Returns an ArrayParser which will process a Node into an ArrayNode of Objects
     * @param xpath The Xpath pointing a list of Nodes that will become the array
     * @param builder A ObjectParserBuilder that is configured to Parse Objects, which will
     *                become children of the Array.
     * @return A new instance of ArrayParser
     */
    public ArrayParser arr(String xpath, ObjectParserBuilder builder){
        return arrayParserFactory.create(xpath, builder.build());
    }

    /**
     * Returns an ArrayParser which will process a Node into an ArrayNode of Objects/Arrays/Values
     * @param xpath The Xpath pointing a list of Nodes that will become the array
     * @param parser A Parser that will parse Objects/Arrays/Values, which will
     *                become children of the Array.
     * @return A new instance of ArrayParser
     */
    public ArrayParser arr(String xpath, Parser parser){
        return arrayParserFactory.create(xpath, parser);
    }

    /**
     * Use this method to pass in a function reference for doing an operation on a String value
     * read from the node, before it is mapped to a JsonNode.
     * @param transformer A reference to at method that mapps from String to Object, that will be
     *                    applied to the string value of the Node.
     * @return The function wrapped, and ready to be passed as a parameter
     */
    public Parser with(Function<String, Object> transformer){
        return simpleParsers.textParser(transformer);
    }


    private static Injector injector(Map<String, String> namespaces){
        return Guice.createInjector(new UnXmlModule(namespaces));
    }
}