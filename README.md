# Language Translator

## Installation

##### Maven

```xml
<dependency>
  <groupId>io.werk24</groupId>
  <artifactId>werk24-java-sdk</artifactId>
  <version>1.5.1</version>
</dependency>
```

## Usage

Extract Structured Information from Technical Drawings.

```java

TechreadClient client = new TechreadClient("<license_key>");

// Specify your asks
List<W24Ask> asks = List.of(new W24Ask("TITLE_BLOCK"));

// Process the file and collect the results
Map<String, Object> result = client.readDrawing("<drawingBytes>", asks);

```
