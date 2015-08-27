package com.nerdforge.unxml.parsers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.google.inject.assistedinject.Assisted;
import com.nerdforge.unxml.json.JsonUtil;
import com.nerdforge.unxml.xml.XmlUtil;
import org.w3c.dom.Node;

import javax.inject.Inject;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collector;

public class ArrayParser implements Parser<ArrayNode> {
    private String xpath;
    private Parser<?> parser;
    private XmlUtil xmlUtil;
    private JsonUtil jsonUtil;

    @Inject
    public ArrayParser(@Assisted String xpath, @Assisted Parser<?> parser, XmlUtil xmlUtil, JsonUtil jsonUtil) {
        this.xpath = xpath;
        this.parser = parser;
        this.xmlUtil = xmlUtil;
        this.jsonUtil = jsonUtil;
    }

    /**
     * Applies the parser to a Node, and returns a JsonArray
     * @param node The node to parse
     * @return A JsonArray
     */
    @Override
    public ArrayNode apply(Node node) {
        return xmlUtil.parseNodes(xpath, node)
                .stream()
                .map(parser)
                .collect(toArrayNode());
    }

    /**
     * Returns the parsed result as a List of Objects of class A
     * @return Result as a List of Objects
     */
    public <A> ListParser<A> as(Class<A> valueType){
        return this.<List<A>>andThen(jsonUtil.asList(valueType))::apply;
    }

    private Collector<JsonNode, ArrayNode, ArrayNode> toArrayNode(){
        return Collector.of(JsonNodeFactory.instance::arrayNode, ArrayNode::add, ArrayNode::addAll);
    }
}