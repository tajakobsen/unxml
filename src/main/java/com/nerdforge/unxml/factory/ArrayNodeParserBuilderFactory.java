package com.nerdforge.unxml.factory;

import com.nerdforge.unxml.parsers.Parser;
import com.nerdforge.unxml.parsers.builders.ArrayNodeParserBuilder;

public interface ArrayNodeParserBuilderFactory {
    ArrayNodeParserBuilder create(String xpath, Parser<?> parser);
}
