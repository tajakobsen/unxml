package com.nerdforge.unxml;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.nerdforge.unxml.parsers.*;
import com.nerdforge.unxml.parsers.builders.ObjectParserBuilder;
import com.nerdforge.unxml.xml.XmlUtil;
import org.w3c.dom.Document;
import java.io.File;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class Parsers {
    private static Optional<Injector> injector = Optional.empty();
    private static Optional<UnXml> parserUtil = Optional.empty();

    /**
     * Returns a new ObjectParserBuilder.
     * @return  A new instance of ObjectParserBuilder
     */
    public static ObjectParserBuilder obj(){
        return unXml().obj();
    }

    /**
     * Returns an ArrayParser which will process a Node into an ArrayNode of Strings
     * @param xpath The Xpath pointing a list of Nodes that will become the array
     * @return A new instance of ArrayParser
     */
    public static ArrayParser arr(String xpath){
        return unXml().arr(xpath);
    }

    /**
     * Returns an ArrayParser which will process a Node into an ArrayNode of Objects
     * @param xpath The Xpath pointing a list of Nodes that will become the array
     * @param builder A ObjectParserBuilder that is configured to Parse Objects, which will
     *                become children of the Array.
     * @return A new instance of ArrayParser
     */
    public static ArrayParser arr(String xpath, ObjectParserBuilder builder){
        return unXml().arr(xpath, builder);
    }

    /**
     * Returns an ArrayParser which will process a Node into an ArrayNode of Objects/Arrays/Values
     * @param xpath The Xpath pointing a list of Nodes that will become the array
     * @param parser A Parser that will parse Objects/Arrays/Values, which will
     *                become children of the Array.
     * @return A new instance of ArrayParser
     */
    public static ArrayParser arr(String xpath, Parser parser){
        return unXml().arr(xpath, parser);
    }

    /**
     * Use this method to pass in a function reference for doing an operation on a String value
     * read from the node, before it is mapped to a JsonNode.
     * @param transformer A reference to at method that mapps from String to Object, that will be
     *                    applied to the string value of the Node.
     * @return The function wrapped, and ready to be passed as a parameter
     */
    public static Parser with(Function<String, Object> transformer){
        return unXml().with(transformer);
    }

    /**
     * Reads a file into an XML Document, that can be parsed.
     * @param file File to parse
     * @return A Document that can be parsed
     */
    public static Document document(File file){
        return xmlUtil().document(file);
    }

    /**
     * Transforms a String into an XML Document, that can be parsed.
     * @param input String to parse
     * @return A Document that can be parsed
     */
    public static Document document(String input){
        return xmlUtil().document(input);
    }

    public static Parser numberParser(){
        return valueParsers().numberParser();
    }

    private static SimpleParsers valueParsers(){
        return injector().getInstance(SimpleParsers.class);
    }

    private static XmlUtil xmlUtil(){
        return injector().getInstance(XmlUtil.class);
    }

    private static UnXml unXml() {
        return injector().getInstance(UnXml.class);
    }

    public static Injector injector(){
        return injector(null);
    }

    public static Injector injector(Map<String, String> namespaces){
        if(!injector.isPresent()){
            injector = Optional.of(Guice.createInjector(new UnXmlModule(namespaces)));
        }

        return injector.get();
    }
}
