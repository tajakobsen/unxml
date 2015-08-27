package com.nerdforge.unxml.factory;

import com.nerdforge.unxml.parsers.ObjectNodeParser;
import com.nerdforge.unxml.parsers.Parser;

import java.util.Map;

public interface ObjectParserFactory {
    ObjectNodeParser create(Map<String, Parser<?>> attributes);
}
