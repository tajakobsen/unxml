package com.nerdforge.unxml.parsers;

import org.w3c.dom.Node;

import java.util.List;
import java.util.function.Function;

public interface ListParser<A> extends Function<Node, List<A>> {}