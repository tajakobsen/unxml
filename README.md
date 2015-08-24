# unXml

Java 8 Library for mapping XPaths to JSON-attributes.

## Parser

An `Object` that implements the [Parser](src/main/java/com/nerdforge/unxml/parsers/Parser.java)-interface can do the following transformation:

[Node](https://docs.oracle.com/javase/8/docs/api/index.html?org/w3c/dom/Node.html) ➝ [JsonNode](http://fasterxml.github.io/jackson-databind/javadoc/2.5/com/fasterxml/jackson/databind/JsonNode.html)
```java
public interface Parser {
  public JsonNode apply(Node node)
}
```

And since [Document](https://docs.oracle.com/javase/8/docs/api/org/w3c/dom/Document.html) extends [Node](https://docs.oracle.com/javase/8/docs/api/index.html?org/w3c/dom/Node.html), `Document` can also be used as input.

## Functional interface

Since a [Parser](src/main/java/com/nerdforge/unxml/parsers/Parser.java) is a [functional interface](https://docs.oracle.com/javase/8/docs/api/java/lang/FunctionalInterface.html), it can be mapped directly, like this:

```java
Parser parser = ... // se below for examples
Optional<Document> document = Parsers.document(inputXmlString);
Optional<JsonNode> result = document.map(parser); // look ma, no: document.map(parser::apply);

List<Document> documents = ...
List<JsonNode> results = documents.stream().map(parser).collect(toList());
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
import com.nerdforge.unxml.Parsers;
import static com.nerdforge.unxml.Parsers.with;
...

public class MyController {
  public JsonNode getJsonFromXml(String inputXmlString) throws Exception {
    Document document = Parsers.document(inputXmlString);
    
    Parser parser = Parsers.obj() // (1)
      .attribute("id", "/root/id", with(Integer::parseInt)) // (2)
      .attribute("title", "//title") // (3) 
      .build(); // (4)
    
    JsonNode node = parser.apply(document); // (5)
    return node;
  }
}
```

 1. The `Parsers.obj()` returns an [ObjectParserBuilder](src/main/java/com/nerdforge/unxml/parsers/builders/ObjectParserBuilder.java)
 2. The resulting json object gets attribute with key = `id`.
   * The value is first read as the `String` content of the xpath: `/root/id` in the xml.
   * It will apply the [Integer.parseInt](https://docs.oracle.com/javase/8/docs/api/java/lang/Integer.html) method, to the `String`content, and return the attribute as an `Integer`.
 3. The resulting json object gets an attribute with id = `title`, and the value is `String` content on the xpath `//title`.
 4. Creates a [Parser](src/main/java/com/nerdforge/unxml/parsers/Parser.java) that will output a [JsonNode](http://fasterxml.github.io/jackson-databind/javadoc/2.5/com/fasterxml/jackson/databind/JsonNode.html).
 5. [Node](https://docs.oracle.com/javase/8/docs/api/index.html?org/w3c/dom/Node.html) ➝ [JsonNode](http://fasterxml.github.io/jackson-databind/javadoc/2.5/com/fasterxml/jackson/databind/JsonNode.html)

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
import static com.nerdforge.unxml.Parsers.*; // (1)
...

public class MyController {
  public ArrayNode getArrayFromXml(String inputXmlString) throws Exception {
    Document document = document(inputXmlString);
    
    ArrayParser parser = arr("/root/entry", arr("list/value")); // (2)
    ArrayNode node = parser.apply(document);
    return node;
  }
}
```

 1. Using a `static import` on [Parsers](src/main/java/com/nerdforge/unxml/Parsers.java), enables `obj()` and `arr()` to be called directly, with a cleaner and more readable syntax.
 2. Creates an [ArrayParser](src/main/java/com/nerdforge/unxml/parsers/ArrayParser.java), that can map to an [ArrayNode](http://fasterxml.github.io/jackson-databind/javadoc/2.5/com/fasterxml/jackson/databind/node/ArrayNode.html) of [ArrayNodes](http://fasterxml.github.io/jackson-databind/javadoc/2.5/com/fasterxml/jackson/databind/node/ArrayNode.html) of `Strings`.
  * The first `arr()` will pick out each `entry` node *(in the xml-file)*.
  * The second `arr()` will pick out each `value` in the `list`.

#### Return Json object

```javascript
[[],["x","y"]]
```

## Example - Complex structures

You can of course combine [ObjectParsers](src/main/java/com/nerdforge/unxml/parsers/ObjectParser.java), [ArrayParsers](src/main/java/com/nerdforge/unxml/parsers/ArrayParser.java) and [predefined parsers](src/main/java/com/nerdforge/unxml/parsers/SimpleParsers.java) to map to more complex structures.

#### The input XML string

```xml
<root>
  <user id="1">
    <name>Homer Simpson</name>
    <birthday>1956-03-01</birthday>
    <email>chunkylover53@aol.com</email>
    <phoneNumbers>
      <home>5551234</home>
      <mobile>5555678</mobile>
      <work>5559991</work>
    </phoneNumbers>
  </user>
</root>
```

#### Creating the Parser

```java
import static com.nerdforge.unxml.Parsers.*;
...

public class MyController {
  public ArrayNode getUsersFromXml(String inputXmlString) throws Exception {
    Document input = document(inputXmlString);
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    ArrayParser parser = arr("/root/user",
      obj()
        .attribute("id", "@id") // (1)
        .attribute("name", "name")
        .attribute("birthday" , "birthday", with(birthday -> LocalDate.parse(birthday, dateFormatter))) // (2)
        .attribute("email", "email")
        .attribute("phoneNumbers", arr("phoneNumbers/*", with(Integer::parseInt))) // (3)
    );
    ArrayNode node = parser.apply(input);
    return node;
  }
}
```

 1. The xpath selects on an attribute in the xml
 2. We use a [DateTimeFormatter](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html) to parse the value as a [LocalDate](https://docs.oracle.com/javase/8/docs/api/java/time/LocalDate.html).
 3. We do a xpath selection on a wildcard, to create an array. The `String` contents of the nodes are parsed into `Integers`.

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
