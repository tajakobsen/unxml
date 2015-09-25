package com.nerdforge.unxml.factory;

import com.nerdforge.unxml.parsers.builders.ObjectArrayParserBuilder;

public interface ObjectArrayParserBuilderFactory {
    ObjectArrayParserBuilder create(String xpath);
}
