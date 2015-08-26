package com.nerdforge.unxml.parsers;

import com.fasterxml.jackson.databind.JsonNode;
import org.w3c.dom.Node;

import java.util.function.Function;

public interface Parser<A extends JsonNode> extends Function<Node, A> {}
