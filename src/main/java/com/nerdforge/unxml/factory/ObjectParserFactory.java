package com.nerdforge.unxml.factory;

import com.nerdforge.unxml.parsers.Parser;
import com.nerdforge.unxml.parsers.ObjectParser;

import java.util.Map;

public interface ObjectParserFactory {
    ObjectParser create(Map<String, Parser<?>> attributes);
}
