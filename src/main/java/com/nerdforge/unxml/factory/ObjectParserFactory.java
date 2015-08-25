package com.nerdforge.unxml.factory;

import com.nerdforge.unxml.parsers.ObjectParser;
import com.nerdforge.unxml.parsers.Parser;

import java.util.Map;

public interface ObjectParserFactory {
    ObjectParser create(Map<String, Parser> attributes);
}
