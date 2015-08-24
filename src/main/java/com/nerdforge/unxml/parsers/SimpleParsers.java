package com.nerdforge.unxml.parsers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.nerdforge.unxml.xml.XmlUtil;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

@Singleton
public class SimpleParsers {
    private ObjectMapper mapper;
    private XmlUtil xmlUtil;

    @Inject
    public SimpleParsers(@Named("json-mapper") ObjectMapper mapper, XmlUtil xmlUtil){
        this.mapper = mapper;
        this.xmlUtil = xmlUtil;
    }

    public Parser dateParser(){
        return textParser(LocalDate::parse);
    }

    public Parser dateParser(DateTimeFormatter formatter){
        return textParser(value -> LocalDate.parse(value, formatter));
    }

    public Parser numberParser(){
        return textParser(Double::parseDouble);
    }

    public Parser textParser(){
        return node -> mapper.valueToTree(node.getTextContent());
    }

    public Parser textParser(Function<String, Object> transformer){
        return node -> mapper.valueToTree(transformer.apply(node.getTextContent()));
    }

    public Parser elementParser(String xpath, Parser parser){
        return node -> xmlUtil.parseNode(xpath, node)
                .map(parser)
                .orElse(NullNode.getInstance());
    }
}
