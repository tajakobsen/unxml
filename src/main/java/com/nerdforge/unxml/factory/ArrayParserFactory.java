package com.nerdforge.unxml.factory;

import com.nerdforge.unxml.parsers.ArrayNodeParser;
import com.nerdforge.unxml.parsers.Parser;

public interface ArrayParserFactory {
    ArrayNodeParser create(String xpath, Parser<?> parser);
}
