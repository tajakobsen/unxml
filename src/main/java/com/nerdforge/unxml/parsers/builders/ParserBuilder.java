package com.nerdforge.unxml.parsers.builders;

import com.fasterxml.jackson.databind.JsonNode;
import com.nerdforge.unxml.parsers.Parser;

public interface ParserBuilder<A extends JsonNode> {
    Parser<A> build();
}
