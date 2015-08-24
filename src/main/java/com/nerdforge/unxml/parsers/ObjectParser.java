package com.nerdforge.unxml.parsers;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.w3c.dom.Node;
import java.util.Map;

public class ObjectParser implements Parser {
    private final Map<String, Parser> attributes;

    public ObjectParser(Map<String, Parser> attributes) {
        this.attributes = attributes;
    }

    @Override
    public ObjectNode apply(Node node) {
        ObjectNode json = JsonNodeFactory.instance.objectNode();

        attributes.keySet()
                .stream()
                .forEach(key -> json.set(key, attributes.get(key).apply(node)));

        return json;
    }
}
