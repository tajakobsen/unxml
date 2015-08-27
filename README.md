# unXml

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.nerdforge/unxml/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.nerdforge/unxml)

Java 8 Library for mapping XPaths to JSON-attributes. It parses [org.w3c.dom](https://docs.oracle.com/javase/8/docs/api/org/w3c/dom/package-summary.html) XML [Nodes](https://docs.oracle.com/javase/8/docs/api/index.html?org/w3c/dom/Node.html) and creates Jackson [JsonNodes](http://fasterxml.github.io/jackson-databind/javadoc/2.5/com/fasterxml/jackson/databind/JsonNode.html).

## Latest release

The most recent release is unXml 0.8, released August 27, 2015.

To add a dependency on unXml using Maven, use the following:

```xml
<dependency>
  <groupId>com.nerdforge</groupId>
  <artifactId>unxml</artifactId>
  <version>0.8</version>
</dependency>
```

## Parser

A [Parser&lt;ObjectNode&gt;](src/main/java/com/nerdforge/unxml/parsers/Parser.java) and a [Parser&lt;ArrayNode&gt;](src/main/java/com/nerdforge/unxml/parsers/Parser.java) can do the following transformations:

[Node](https://docs.oracle.com/javase/8/docs/api/index.html?org/w3c/dom/Node.html) ➝ [ObjectNode](http://fasterxml.github.io/jackson-databind/javadoc/2.5/com/fasterxml/jackson/databind/ObjectNode.html)

[Node](https://docs.oracle.com/javase/8/docs/api/index.html?org/w3c/dom/Node.html) ➝ [ArrayNode](http://fasterxml.github.io/jackson-databind/javadoc/2.5/com/fasterxml/jackson/databind/ArrayNode.html)


And since [Document](https://docs.oracle.com/javase/8/docs/api/org/w3c/dom/Document.html) extends [Node](https://docs.oracle.com/javase/8/docs/api/index.html?org/w3c/dom/Node.html), `Document` can also be used as input.

## Using Parsing to create a Parser

To create a [Parser](src/main/java/com/nerdforge/unxml/parsers/Parser.java) you first need an instance of  [Parsing](src/main/java/com/nerdforge/unxml/Parsing.java).

```java
Parsing parsing = ParsingFactory.getInstance().create();

// create parser that will output a Jackson ObjectNode
Parser<ObjectNode> parser = parsing.obj().attribute("resultKey", "//my-xpath").build();
Parser<ObjectNode> parser2 = parsing.obj("//my-root").attribute("id", "@id").build();

// create parser that will output a Jackson ArrayNode
Parser<ArrayNode> parser3 = parsing.arr(parsing.obj().attribute("id", "@id")).build();
```

## Parsing to an Object or List

By using the `as`-method on [ObjectParserBuilder](src/main/java/com/nerdforge/unxml/parsers/builders/ObjectParserBuilder.java) or [ArrayParserBuilder](src/main/java/com/nerdforge/unxml/parsers/builders/ArrayParserBuilder.java), you can have Jackson instansiate an `Object` or a `List` as part of the parsing process.

```java
// create a parser that will output an Object instance
ObjectParser<User> userParser = parsing.obj(...).attribute(...).as(User.class);
User user = userParser.apply(xmlDocument);

// create a parser that will output a List of objects
ObjectParser<List<User>> usersParser = parsing.arr(...).as(User.class);
List<User> users = usersParser.apply(xmlDocument);
```

## Example - Parsing an object

#### The input XML string

```xml
<root>
  <id>1</id>
  <title>mytitle</title>
</root>
```

#### Creating the Parser

```java
import com.nerdforge.unxml.Parsing;
import com.nerdforge.unxml.factory.ParsingFactory;
...

public class MyController {
  public ObjectNode getJsonFromXml(String inputXmlString) {
    Parsing parsing = ParsingFactory.getInstance().create(); // (1)
    Document document = parsing.xml().document(inputXmlString); // (2)
    
    Parser<ObjectNode> parser = parsing.obj() // (3)
      .attribute("id", "/root/id", parsing.with(Integer::parseInt)) // (4)
      .attribute("title", "//title") // (5)
      .build(); // (6)

    ObjectNode node = parser.apply(document); // (7)
    return node;
  }
}
```

 1. Uses the [ParsingFactory](src/main/java/com/nerdforge/unxml/factory/ParsingFactory.java) to get an instance of [Parsing](src/main/java/com/nerdforge/unxml/Parsing.java).
 2. Parsing also gives access to an XML-utility object by using the `xml()`-method.
 3. The `Parsers.obj()` returns an [ObjectNodeParserBuilder](src/main/java/com/nerdforge/unxml/parsers/builders/ObjectNodeParserBuilder.java)
 4. The resulting json object gets attribute with key = `id`.
   * The value is first read as the `String` content of the xpath: `/root/id` in the xml.
   * It will apply the [Integer.parseInt](https://docs.oracle.com/javase/8/docs/api/java/lang/Integer.html) method, to the `String`content, and return the attribute as an `Integer`.
 5. The resulting json object gets an attribute with id = `title`, and the value is `String` content on the xpath `//title`.
 6. Creates a [Parser](src/main/java/com/nerdforge/unxml/parsers/Parser.java) that will output an [ObjectNode](http://fasterxml.github.io/jackson-databind/javadoc/2.5/com/fasterxml/jackson/databind/ObjectNode.html).
 7. [Node](https://docs.oracle.com/javase/8/docs/api/index.html?org/w3c/dom/Node.html) ➝ [ObjectNode](http://fasterxml.github.io/jackson-databind/javadoc/2.5/com/fasterxml/jackson/databind/ObjectNode.html)

#### Return Json object

```javascript
{
  "id":1,
  "title":"mytitle"
}
```

## Example - Parsing arrays

#### The input XML string

```xml
<root>
  <entry>
  </entry>
  <entry>
    <list>
      <value>x</value>
      <value>y</value>
    </list>
  </entry>
</root>
```

#### Creating the Parser

```java
public class MyController {
  @Inject private Parsing parsing; // (1)

  public ArrayNode getArrayFromXml(String inputXmlString) {
    Document document = parsing.xml().document(inputXmlString);
    
    Parser<ArrayNode> parser = parsing.arr("/root/entry",
      parsing.arr("list/value")
    ).build(); // (2)

    ArrayNode node = parser.apply(document);
    return node;
  }
}
```

 1. By using [Google Guice](https://github.com/google/guice) you can directly inject a [Parsing](src/main/java/com/nerdforge/unxml/Parsing.java) object into your class. (Remember to `install` the [UnXmlModule](src/main/java/com/nerdforge/unxml/UnXmlModule.java) in your module).
 2. Creates a [Parser&lt;ArrayNode&gt;](src/main/java/com/nerdforge/unxml/parsers/Parser.java), that can map to an [ArrayNode](http://fasterxml.github.io/jackson-databind/javadoc/2.5/com/fasterxml/jackson/databind/node/ArrayNode.html) of [ArrayNode](http://fasterxml.github.io/jackson-databind/javadoc/2.5/com/fasterxml/jackson/databind/node/ArrayNode.html) of `Strings`.
  * The first `arr()` will pick out each `entry` node *(in the xml-file)*.
  * The second `arr()` will pick out each `value` in the `list`.

#### Return Json object

```javascript
[[],["x","y"]]
```

## Example - Complex structures

You can of course combine [Parser<ObjectNode>](src/main/java/com/nerdforge/unxml/parsers/Parser.java) with [Parser<ArrayNode>](src/main/java/com/nerdforge/unxml/parsers/Parser.java) and [predefined parsers](src/main/java/com/nerdforge/unxml/parsers/SimpleParsers.java) to map to more complex structures.

#### The input XML string

```xml
<?xml version="1.0"?>
<feed xmlns="http://www.w3.org/2005/Atom">
  <entry id="1">
    <name>Homer Simpson</name>
    <birthday>1956-03-01</birthday>
    <email xmlns="http://www.w3.org/2007/app">chunkylover53@aol.com</email>
    <phoneNumbers>
      <home>5551234</home>
      <mobile>5555678</mobile>
      <work>5559991</work>
    </phoneNumbers>
  </entry>
</feed>
```

#### Creating the Parser

```java
public class MyController {
  public ArrayNode getUsersFromXml(String inputXmlString) {
    Parsing parsing = ParsingFactory.getInstance(namespaces()).create(); // (1)
    Document input = parsing.xml().document(inputXmlString);
    Parser dateParser = parsing.simple().dateParser(DateTimeFormatter.ofPattern("yyyy-MM-dd")); // (2)

    Parser<ArrayNode> parser = parsing.arr("/a:feed/a:entry", // (3)
      parsing.obj()
        .attribute("id", "@id", parsing.with(Integer::parseInt)) // (4)
        .attribute("name", "a:name")
        .attribute("birthday", "a:birthday", dateParser)
        .attribute("email", "app:email") // (5)
        .attribute("phoneNumbers", parsing.arr("a:phoneNumbers/*", parsing.with(Integer::parseInt))) // (6)
    ).build();
    ArrayNode node = parser.apply(input);
    return node;
  }
  
  private Map<String, String> namespaces(){
    return new HashMap<String, String>(){{
        put("a", "http://www.w3.org/2005/Atom");
        put("app", "http://www.w3.org/2007/app");
    }};
  }
}
```

 1. Creates the instance of `Parsing`, but with XML-namespaces.
 2. Creates a `dateParser` that will parse dates with the format `yyyy-MM-dd`.
 3. Uses the preconfigured namespace `a` to do selections.
 4. The xpath selects on an attribute in the xml
 5. Use the preconfigured namespace `app` to select the email.
 6. We do a xpath selection on a wildcard, to create an array. The `String` contents of the nodes are parsed into `Integers`.

#### Return Json object

```javascript
[{
  "birthday":[1956,3,1], // (1)
  "name":"Homer Simpson",
  "id":1,
  "email":"chunkylover53@aol.com",
  "phoneNumbers":[5551234,5555678,5559991]
}]
```

 1. The Jackson library will map a Java [LocalDate](https://docs.oracle.com/javase/8/docs/api/java/time/LocalDate.html) as a JavaScript `Array` of `Numbers`. See the [documentation](https://github.com/FasterXML/jackson-datatype-jsr310).
 
## Functional interface

Since a [Parser](src/main/java/com/nerdforge/unxml/parsers/Parser.java) is a [functional interface](https://docs.oracle.com/javase/8/docs/api/java/lang/FunctionalInterface.html), it can be mapped directly, like this:

```java
Parsing parsing = ParsingFactory.getInstance(namespaces).create();
Parser<ObjectNode> parser = parsing.obj()... // se above for examples
Document document = parsing.xml().document(inputXmlString);

// Apply to an Optional
Optional<ObjectNode> result = Optional.of(document).map(parser); // (1)

// Apply to a Stream
List<Document> documents = ...
List<ObjectNode> results = documents.stream().map(parser).collect(toList()); // (2)
```

 1. Applies the parser directly without doing a `Optional.of(document).map(parser::apply)`
 2. Shorthand for `documents.stream().map(parser::apply).collect(toList())`
