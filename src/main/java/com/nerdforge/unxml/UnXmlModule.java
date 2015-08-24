package com.nerdforge.unxml;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.google.inject.PrivateModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;
import com.nerdforge.unxml.parsers.*;
import com.nerdforge.unxml.parsers.factory.ArrayParserFactory;
import com.nerdforge.unxml.xml.XmlUtil;
import org.apache.xerces.impl.Constants;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.HashMap;
import java.util.Map;

public class UnXmlModule extends PrivateModule {
    Map<String, String> namespaces = new HashMap<>();

    public UnXmlModule(){}

    public UnXmlModule(Map<String, String> namespaces){
        this.namespaces = namespaces;
    }

    @Override
    protected void configure() {
        // Generate ArrayParserFactory
        install(new FactoryModuleBuilder()
                .implement(ArrayParser.class, ArrayParser.class)
                .build(ArrayParserFactory.class));

        // JSON Mapper
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new Jdk8Module());
        mapper.registerModule(new JSR310Module());
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        bind(ObjectMapper.class).annotatedWith(Names.named("json-mapper")).toInstance(mapper);

        // bind xml namespaces
        bind(NamespaceContext.class).toInstance(new SimpleNamespaceContext(namespaces));

        // bind document builder factory
        bind(DocumentBuilderFactory.class).toInstance(documentBuilderFactory());

        bind(UnXml.class);
        expose(UnXml.class);

        bind(XmlUtil.class);
        expose(XmlUtil.class);

        bind(SimpleParsers.class);
        expose(SimpleParsers.class);
    }

    private static DocumentBuilderFactory documentBuilderFactory() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance("org.apache.xerces.jaxp.DocumentBuilderFactoryImpl", UnXmlModule.class.getClassLoader());
            factory.setFeature(Constants.SAX_FEATURE_PREFIX + Constants.EXTERNAL_GENERAL_ENTITIES_FEATURE, false);
            factory.setFeature(Constants.SAX_FEATURE_PREFIX + Constants.EXTERNAL_PARAMETER_ENTITIES_FEATURE, false);
            factory.setFeature(Constants.XERCES_FEATURE_PREFIX + Constants.DISALLOW_DOCTYPE_DECL_FEATURE, true);
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            factory.setNamespaceAware(true);
            return factory;
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }
}