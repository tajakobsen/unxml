package com.nerdforge.unxml.parsers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.google.inject.assistedinject.Assisted;
import com.nerdforge.unxml.xml.XmlUtil;
import org.w3c.dom.Node;

import javax.inject.Inject;
import java.util.stream.Collector;

public class ArrayParser implements Parser {
    private String xpath;
    private Parser parser;
    private XmlUtil xmlUtil;

    @Inject
    public ArrayParser(@Assisted String xpath, @Assisted Parser parser, XmlUtil xmlUtil) {
        this.xpath = xpath;
        this.parser = parser;
        this.xmlUtil = xmlUtil;
    }

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