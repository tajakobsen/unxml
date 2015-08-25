package com.nerdforge.unxml.factory;

import com.google.inject.Guice;
import com.nerdforge.unxml.Parsing;
import com.nerdforge.unxml.UnXmlModule;
import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Map;

public class ParsingFactory {
    private final Provider<Parsing> parsing;

    @Inject
    private ParsingFactory(Provider<Parsing> parsing){
        this.parsing = parsing;
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

    /**
     * Returns a new Parsing object with all the utility methods to create parsers.
     * @return An instance of Parsing
     */
    public Parsing create(){
        return parsing.get();
    }
}
