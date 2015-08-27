package com.nerdforge.unxml.parsers.builders;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.nerdforge.unxml.factory.ArrayNodeParserFactory;
import com.nerdforge.unxml.json.JsonUtil;
import com.nerdforge.unxml.parsers.ObjectParser;
import com.nerdforge.unxml.parsers.Parser;
import java.util.List;

public class ArrayNodeParserBuilder implements ParserBuilder<ArrayNode> {
    private final String xpath;
    private final Parser<?> parser;
    private final ArrayNodeParserFactory arrayNodeParserFactory;
    private final JsonUtil jsonUtil;

    @Inject
    public ArrayNodeParserBuilder(@Assisted String xpath, @Assisted Parser<?> parser, ArrayNodeParserFactory arrayNodeParserFactory, JsonUtil jsonUtil) {
        this.xpath = xpath;
        this.parser = parser;
        this.arrayNodeParserFactory = arrayNodeParserFactory;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public Parser<ArrayNode> build() {
        return arrayNodeParserFactory.create(xpath, parser);
    }

    /**
     * Returns the parsed result as a List of Objects of class A
     * @param valueType  The type of class that should be instansiated in the list
     * @param <A> The Class the ListParser will return a list of.
     * @return Result as a List of Objects of class A
     */
    public <A> ObjectParser<List<A>> as(Class<A> valueType){
        return build().andThen(jsonUtil.asList(valueType))::apply;
    }
}
