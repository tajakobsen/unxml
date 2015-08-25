package com.nerdforge.unxml.factory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.nerdforge.unxml.Parsing;
import com.nerdforge.unxml.UnXmlModule;
import com.nerdforge.unxml.parsers.SimpleParsers;

import javax.inject.Inject;
import java.util.Map;

public class ParsingFactory {
    private final Injector injector;

    @Inject
    private ParsingFactory(Injector injector){
        this.injector = injector;
    }

    /**
     * Returns an instance of the factory
     * @return An instance of this factory
     */
    public static ParsingFactory getInstance(){
        return Guice.createInjector(new UnXmlModule()).getInstance(ParsingFactory.class);
    }

    /**
     * Returns an instance of the factory
     * @param namespaces A map containing the namespaces in the xml, that can be looked up.
     * @return An instance of this factory
     */
    public static ParsingFactory getInstance(Map<String, String> namespaces){
        return Guice.createInjector(new UnXmlModule(namespaces)).getInstance(ParsingFactory.class);
    }

    public Parsing get(){
        return injector.getInstance(Parsing.class);
    }

    public SimpleParsers parsers(){
        return injector.getInstance(SimpleParsers.class);
    }
}
