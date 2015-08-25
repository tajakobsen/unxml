package com.nerdforge.unxml.parsers;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.assistedinject.Assisted;
import org.w3c.dom.Node;

import javax.inject.Inject;
import java.util.Map;

public class ObjectParser implements Parser {
    private final Map<String, Parser> attributes;

    @Inject
    public ObjectParser(@Assisted Map<String, Parser> attributes) {
        this.attributes = attributes;
    }

    @Override
    public ObjectNode apply(final Node node) {
        ObjectNode json = JsonNodeFactory.instance.objectNode();
        attributes.forEach((key, parser) -> json.set(key, parser.apply(node)));
        return json;
    }
}