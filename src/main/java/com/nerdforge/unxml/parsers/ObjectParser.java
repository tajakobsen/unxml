package com.nerdforge.unxml.parsers;

import org.w3c.dom.Node;
import java.util.function.Function;

public interface ObjectParser<A> extends Function<Node, A>{}