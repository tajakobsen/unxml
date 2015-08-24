package com.nerdforge.unxml.parsers;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

@Singleton
public class SimpleParsers {
    private ObjectMapper mapper;

    @Inject
    public SimpleParsers(@Named("json-mapper") ObjectMapper mapper){
        this.mapper = mapper;
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
}
