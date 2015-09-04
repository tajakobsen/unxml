package com.nerdforge.unxml;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nerdforge.unxml.factory.ParsingFactory;
import com.nerdforge.unxml.parsers.Parser;
import org.w3c.dom.Document;

class TreeParser {
    private Parsing parsing = ParsingFactory.getInstance().create();

    public ObjectNode parseXml(String inputXML){
        Parsing parsing = ParsingFactory.getInstance().create();
        Document document = parsing.xml().document(inputXML);

        Parser<ObjectNode> parser = parsing.obj()
            .attribute("data", "Root", recursiveParser())
            .build();

        return parser.apply(document);
    }

    public Parser<ObjectNode> recursiveParser(){

        return  parsing.obj()
            .attribute("text", parsing.simple().nodeNameParser())
            .attribute("children",
                parsing.arr("node()",
                    parsing.with(this::recursiveParser) // recursivly
                )
            ).build();
    }
}