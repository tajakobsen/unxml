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

public class ArrayNodeParser implements Parser<ArrayNode> {
    private String xpath;
    private Parser<?> parser;
    private XmlUtil xmlUtil;

    @Inject
    public ArrayNodeParser(@Assisted String xpath, @Assisted Parser<?> parser, XmlUtil xmlUtil) {
        this.xpath = xpath;
        this.parser = parser;
        this.xmlUtil = xmlUtil;
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

    private Collector<JsonNode, ArrayNode, ArrayNode> toArrayNode(){
        return Collector.of(JsonNodeFactory.instance::arrayNode, ArrayNode::add, ArrayNode::addAll);
    }
}