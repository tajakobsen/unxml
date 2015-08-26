package com.nerdforge.unxml.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;

public class JsonUtil {
    private ObjectMapper mapper;

    @Inject
    public JsonUtil(@Named("json-mapper") ObjectMapper mapper){
        this.mapper = mapper;
    }

    public <A> Function<ObjectNode, A> as(Class<A> valueType){
        return node -> treeToValue(node, valueType);
    }

    public <A> Function<ArrayNode, List<A>> asList(Class<A> valueType) {
        return node -> readValue(node.traverse(), listType(valueType));
    }

    private <A> List<A> readValue(JsonParser jsonParser, CollectionType type){
        try {
            return mapper.readValue(jsonParser, type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T treeToValue(TreeNode n, Class<T> valueType){
        try {
            return mapper.treeToValue(n, valueType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private <A> CollectionType listType(Class<A> valueType){
        return TypeFactory.defaultInstance().constructCollectionType(List.class, valueType);
    }
}
