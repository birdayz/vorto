---
layout: documentation
title: Data Type Model DSL Reference
---
{% include base.html %}

## Datatype DSL Reference

This section details the following topics:

[Datatype DSL Syntax](#data-type-model-dsl-reference)

[Datatype DSL Semantics](#the-datatype-dsl)

## Data Type Model DSL Reference

The following code represents the Data Type Model DSL syntax.

    model:entity | enumeration;

    modelReference: 'using' qualifiedName ';' version;

    qualifiedName: id ('.' id)*;

    entity:
       'namespace' qualifiedName
       'version' version
       ('displayname' displayname)?
       ('description' description)?
       ('category' category)?
       (modelReference)*
       'entity' id ('extends' [entity|qualifiedName])? '{'
            (property)*
       '}'
    ;

    enumeration:
       'namespace' qualifiedName
       'version' version
       (modelReference)*
        'enum' id '{'
            (enumLiteral)*
        '}'
    ;

    enumLiteral: id;

    property:
        presence ('multiple')? id 'as' type 
            ('with' '{' propertyAttributes (',' propertyAttributes)* '}')?
            ('<'constraintType [constraintIntervalType] ("," constraintType [constraintIntervalType])*'>')?
            (description)?
    ;

    propertyAttributes: booleanPropertyAttribute | enumLiteralPropertyAttribute;

    booleanPropertyAttribute: ('readable' | 'writable' | 'eventable') ':' (value ?= 'true' | 'false');

    enumLiteralPropertyAttribute: 'measurementUnit' ':' value;

    type : primitiveType | entity | enumeration | complexPrimitiveType;

    primitiveType : 'string' | 'int' | 'float' | 'boolean' | 'dateTime' |
        'double' | 'long' | 'short' | 'base64Binary' | 'byte' ;

    complexPrimitiveType : 'Dictionary' ('[' type ',' type ']')?

    presence : 'mandatory' | 'optional';

    constraintType: 'MIN' | 'MAX' | 'STRLEN' | 'REGEX' ;

    constraintIntervalType: int| signedint| float| datetime| string;

    id: '^'?('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'_'|'0'..'9')*;

    string:  
        '"' ( '\\' ('b'|'t'|'n'|'f'|'r'|'u'|'"'|"'"|'\\') | !('\\'|'"') )* '"' |
        "'" ( '\\' ('b'|'t'|'n'|'f'|'r'|'u'|'"'|"'"|'\\') | !('\\'|"'") )* "'"
    ;

    date:  
        ('0'..'9')('0'..'9')('0'..'9')('0'..'9')('-')('0'..'9')('0'..'9')('-')
        ('0'..'9')('0'..'9')  
    ;

    time:  
        ('0'..'9')('0'..'9')(':')('0'..'9')('0'..'9')(':')('0'..'9')('0'..'9')
        (timezone)?  
    ;

    timezone: 'Z'|(('+'|'-')('0'..'9')('0'..'9')(':')('0'..'9')('0'..'9'))  ;

    datetime: date('T')time;

    int: ('0'..'9')+;

    signedint: '-'INT;

    float: int('.'int)?;

    version: int('.' int)*('-'id)?;

## The Datatype DSL

An [**Entity**](#entity) element defines a new non-primitive data type that can be referenced by the function block. It contains [**Properties**](#properties) which are either of the [**Primitive**](#primitive-types) type, another [**Entity**](#entity), or a [**Dictionary**](#dictionary) type. It can also *inherit* from existing entities.

An [**Enum**](#enum) is an enumeration of values of the same type similar to an *enum* in Java.

### Entity

An Entity consist of 

- General metadata parameters (`namespace, version, displayname, description, category`).
- Optional references to other entities or enums
- And a list of [**Properties**](#properties)

**Syntax**

Refer to entity in [Datatype DSL Syntax](#data-type-model-dsl-reference).

**Usage**

<table class="table table-bordered">
<thead>
  <tr>
    <th>Entity element</th>
    <th>Mandatory</th>
    <th>Description</th>
    <th>Type</th>
  </tr>
  </thead>
  <tbody>
  <tr>
    <td>&lt;entity_name&gt;</td>
    <td>Y</td>
    <td>A descriptive name of the entity.</td>
    <td>String (identifier, without spaces)</td>
  </tr>
  <tr>
    <td>extends &lt;other_entity_name&gt;</td>
    <td></td>
    <td>The entity extends the property &lt;other_entity_name&gt;.</td>
    <td></td>
  </tr>
  <tr>
    <td>&lt;optional&nbsp;|&nbsp;mandatory&gt;</td>
    <td></td>
    <td>Declares if the property is optional or mandatory.</td>
    <td></td>
  </tr>
  <tr>
    <td>multiple</td>
    <td></td>
    <td>Defines if the property can have more than one value.</td>
    <td></td>
  </tr>
  <tr>
    <td>&lt;property_name&gt;</td>
    <td>Y</td>
    <td>The name of the property.</td>
    <td>String (identifier, without spaces)</td>
  </tr>
  <tr>
    <td>as &lt;type&gt;</td>
    <td>Y</td>
    <td>The data type of the property.</td>
    <td>Supported types:
    <ul>
      <li>Primitive Types </li>
      <li>Another Entity</li>
      <li>Dictionary</li>
    </ul>
  </td>
  </tr>
  <tr>
    <td>&lt;description&gt;</td>
    <td></td>
    <td>Extra information about the entity.</td>
    <td>String enclosed in quotation marks</td>
  </tr>
  </tbody>
</table>

**Example**

    entity Color {  
      mandatory red as int <MIN 0, MAX 255> "The red component of a color"  
      mandatory green as int <MIN 0, MAX 255> "The green component of a color"    
      mandatory blue as int <MIN 0, MAX 255> "The blue component of a color"  
    }

#### Properties

A property consist of 

- An optional *presence* indicator which can either be `mandatory` or `optional`
- An optional `multiple` which indicates if the property is an array
- A required *name* as a string and a required *type* which can either be a [**Primitive**](#primitive-types) type,
another [**Entity**](#entity), or a [**Dictionary**](#dictionary). Both are bridged by the keyword `as`.
- An optional [**Attributes**](#attributes) (measurementUnit, readable, writable, eventable)
- An optional [**Constraint**](#constraints) (MIN, MAX, STRLEN, REGEX)
- An optional *Description*, a string which can be used to document the property.

**Example**
  
    isAlive as boolean
    
    mandatory multiple lidarMeasurements as float "The measurements of the LIDAR in one scan"
    
    optional engineTemperature as float with { measurementUnit: Temperature.Celsius } <MIN 0, MAX 3000> "The engine temperature"

#### Primitive Types

The following are the primitive types supported in Vorto

- `string`
- `int`
- `float`
- `boolean`
- `dateTime`
- `double`
- `long`
- `short`
- `base64Binary`
- `byte`

#### Dictionary

A *Dictionary* is a type that contains a *Key* and a *Value*. It is declared with the keyword `Dictionary`
followed by an optional bracket (`[ .. ]`) if you wish to indicate the *type* of its *key* and *value*. The *type* can
be a *primitive* type, an *Entity* type, an *Enum* or another *Dictionary*.

**Example**
  
    mandatory lookupTable as Dictionary[string, Color] "Lookup table conversion for color"

    mandatory lookupRGBTable as Dictionary[Color, RGB] "Lookup table conversion for color from description to RGB"

    mandatory temperatureLabelConversion as Dictionary[int, string] "map of temperature to description"

    mandatory noKeyAndValueTypeMap as Dictionary "example of a map with no key and value types"

#### Attributes

An attribute can be added by the keyword `with` followed by the attributes in between *curly* (`{ .. }`) braces. As of now,
the following attributes are supported

- `measurementUnit` - Indicates a measurement unit of the property. It is an *Enum* value. For example, if the property is a temperature, the measurementUnit could be *Temperature.celsius* declared in the *Temperature* enumeration.
- `readable` - Indicates if the property could be read.
- `writable` - Indicates if the property could be written to.
- `eventable` - Indicates if the property generates events.

**Example**

    mandatory engineTemperature as float 
      with { measurementUnit: Temperature.Celsius, readable: true, writable: true, eventable: false } 
      "The engine temperature"

#### Constraints

A *constraint* can be added by putting it in between `<` and `>` symbols and consist of the constraint name (e.g `MIN') and
the value of that constraint.

The following constraints are supported

- `MIN` 
- `MAX` 
- `STRLEN`
- `REGEX`

**Example**

    mandatory red as int <MIN 0, MAX 255> "The red component of a color"    

### Enum

An Enum is an enumeration of values of the same type

**Syntax**

Refer to entity in [Datatype DSL Syntax](#data-type-model-dsl-reference).

**Usage**

<table class="table table-bordered">
<thead>
  <tr>
    <th>Entity element</th>
    <th>Mandatory</th>
    <th>Description</th>
    <th>Type</th>
  </tr>
  </thead>
  <tbody>
  <tr>
    <td>&lt;enum_name&gt;</td>
    <td>Y</td>
    <td>A descriptive name of the enum.</td>
    <td>String (identifier, without spaces)</td>
  </tr>
  <tr>
    <td>{</td>
    <td>Y</td>
    <td>Delimiter of the literal list (comma separated literals).</td>
    <td></td>
  </tr>
  <tr>
    <td>&lt;literal&gt;</td>
    <td>Y</td>
    <td>A descriptive name of an enum value.</td>
    <td>String (identifier, without spaces)</td>
  </tr>
  <tr>
    <td>}</td>
    <td>Y</td>
    <td>The name of the property.</td>
    <td></td>
  </tr>
  </tbody>
</table>

**Example**

    // definition of an enum type
    enum Gender{
      Male, Female, NA
    }

    // usage as a field
    mandatory sex as Gender

<table class="table table-bordered">
	<tbody>
  <tr>
    <td><i class="fa fa-info-circle info-note"></i></td>
    <td>To find out more about which language elements are available when writing a data type DSL, have a look at the respective DSL Reference. </br>
    Use the keyboard shortcut <span style="font-variant:small-caps;">Ctrl + Space</span> to get a list of available DSL elements, that can be applied in the current scope you are in.</td>
  </tr>
	</tbody>
</table>
