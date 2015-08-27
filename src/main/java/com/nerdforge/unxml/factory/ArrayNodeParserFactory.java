package com.nerdforge.unxml.factory;

import com.nerdforge.unxml.parsers.ArrayNodeParser;
import com.nerdforge.unxml.parsers.Parser;

public interface ArrayNodeParserFactory {
    ArrayNodeParser create(String xpath, Parser<?> parser);
}
