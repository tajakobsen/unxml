package com.nerdforge.unxml;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import static javax.xml.XMLConstants.*;
import javax.xml.namespace.NamespaceContext;

/**
 * Simple implementation of NamespaceContext using Guava MultiMaps.
 * @author Tom Arild Jakobsen
 */
public final class SimpleNamespaceContext implements NamespaceContext {
    private final Map<String, String> namespaces; // [{ "foo": "http://foo" }]
    private final Multimap<String, String> prefixes; // [{ "http://foo": ["foo"] }]

    /**
     * Constructor for SimpleNamespaceContext
     * @param namespaces A map where the key is a prefix, and the value is a namespace.
     */
    public SimpleNamespaceContext(Map<String, String> namespaces){
        this.namespaces = addConstants(namespaces);
        prefixes = Multimaps.invertFrom(Multimaps.forMap(this.namespaces), ArrayListMultimap.create());
    }

    @Override
    public String getNamespaceURI(String prefix) {
        checkNotNull(prefix);
        return namespaces.getOrDefault(prefix, NULL_NS_URI);
    }

    @Override
    public String getPrefix(String namespaceURI) {
        checkNotNull(namespaceURI);
        return prefixes.get(namespaceURI).stream().findFirst().orElse(null);
    }

    @Override
    public Iterator<String> getPrefixes(String namespaceURI){
        checkNotNull(namespaceURI);
        return prefixes.get(namespaceURI).iterator();
    }

    private String checkNotNull(String reference) {
        if (reference == null) {
            throw new IllegalArgumentException();
        }
        return reference;
    }

    private Map<String, String> addConstants(Map<String, String> map){
        Map<String, String> namespaces = Maps.newHashMap(map);
        namespaces.putIfAbsent(XML_NS_PREFIX, XML_NS_URI);
        namespaces.putIfAbsent(XMLNS_ATTRIBUTE, XMLNS_ATTRIBUTE_NS_URI);
        return Collections.unmodifiableMap(namespaces);
    }
}