package com.nerdforge.unxml.parsers.factory;

import com.nerdforge.unxml.parsers.ArrayParser;
import com.nerdforge.unxml.parsers.Parser;

public interface ArrayParserFactory {
    ArrayParser create(String xpath, Parser parser);
}
