package com.nerdforge.unxml.xml;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.stream.Collectors.toList;

@Singleton
public class XmlUtil {
    private final Logger logger;
    private final DocumentBuilderFactory factory;
    private final ErrorHandler errorHandler;
    private final NamespaceContext namespaceContext;
    private XPathFactory xpathFactory = XPathFactory.newInstance();

    @Inject
    public XmlUtil(Logger logger, DocumentBuilderFactory factory, ErrorHandler errorHandler, NamespaceContext namespaceContext){
        this.logger = logger;
        this.factory = factory;
        this.errorHandler = errorHandler;
        this.namespaceContext = namespaceContext;
    }

    public Document document(File file){
        checkNotNull(file);
        return document(file.toURI());
    }

    public Document document(URI uri) {
        checkNotNull(uri);
        return document(new InputSource(uri.toASCIIString()));
    }


    public Document document(String str){
        checkNotNull(str);
        InputStream input = new ByteArrayInputStream(str.getBytes(Charsets.UTF_8));
        return document(new InputSource(input));
    }

    public Document document(InputSource source){
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setErrorHandler(errorHandler);
            return builder.parse(source);
        } catch (SAXException | IOException | ParserConfigurationException e) {
            throw Throwables.propagate(e);
        }
    }

    public Optional<Node> parseNode(String xpath, Node node){
        return Optional.ofNullable((Node) evaluate(xpath, node, XPathConstants.NODE));
    }

    public List<Node> parseNodes(String xpath, Node node){
        return normalizeNodeList((NodeList) evaluate(xpath, node, XPathConstants.NODESET));
    }

    private Object evaluate(String path, Node node, QName returnType) {
        try {
            XPath xpath = xpathFactory.newXPath();
            xpath.setNamespaceContext(namespaceContext);
            logger.debug("Evaluating XML with: xpath=[{}], node=[{}], returnType=[{}]", path, node.getNodeName(), returnType.getLocalPart());
            Object result = xpath.evaluate(path, node, returnType);
            return logWarnIfNull(result, path, node.getNodeName());
        } catch (XPathExpressionException e) {
            throw Throwables.propagate(e);
        }
    }

    private Object logWarnIfNull(Object obj, String path, String nodeName){
        if(obj == null){
            logger.warn("No node found at xpath=[{}], nodeName=[{}]", path, nodeName);
        }

        return obj;
    }

    private List<Node> normalizeNodeList(NodeList nodeList){
        return IntStream.range(0, nodeList.getLength())
                .mapToObj(nodeList::item)
                .collect(toList());
    }
}