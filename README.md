[![DOI](https://zenodo.org/badge/336198749.svg)](https://zenodo.org/badge/latestdoi/336198749)

# Josch
Josch is a cockpit application that combines schema extraction and checking for JSON Schema 
containment to exploit their interactions. It can be used for schema-less NoSQL document stores,
 but is currently geared for MongoDB. Furthermore, it does not implement schema extraction and 
 checking for JSON Schema containment itself, instead, it uses third-party-tools for these tasks 
 and allows the user to easily switch between them (in the user interface).

Schema-Extraction: Josch analyzes A MongoDB collection and a JSON Schema or a MongoDB validator is 
extracted, that describes the structure of the stored data.

JSON Schema Containment: Josch compares Two JSON Schema documents to check whether the language
 defined by one schema is a superset, superset, equivalent or incomparable to the language defined
 by the other JSON Schema document.

Josch uses Maven to preserve a modular architecture that allows to readily extend Josch by adding 
new tools for schema extraction or JSON Schema containment checking. Even further, other document 
stores can also be added.



## Supported Third-Party-Tools

### JSON Schema Containment Tools

- [jsonsubschema](https://github.com/IBM/jsonsubschema)
- [is-json-schema-subset](https://github.com/haggholm/is-json-schema-subset)

### Schema Extraction Tools

- [Hackolade](https://hackolade.com)
- [json-schema-inferrer](https://github.com/saasquatch/json-schema-inferrer)



## Features 

### Key features
- Extract a JSON Schema using different extraction tools. 
    - Use relative or absolute sampling to extract. 
    - Switch between the extraction tools within the application. 
- Compare two JSON Schemas semantically using different containment tools. 
    - Switch between the tools within the application. 
- Compare two JSON Schemas syntactically and highlight the differences. 
- Store and browse historic schema versions.
    - Add a personal note when storing.
    - Filter JSON Schemas by the date of storing. 
- Validate all or individual documents against a JSON Schema. 
    - Find the documents that do not validate. 
    - Get the amount of valid documents. 
    - Find out why a single document fails validation. 
    
- Load, modify and create a JSON Schema
### Other features 
- Show the available databases and collections of the database server.
- Show random document samples for a given collection. 
  
    - Show all documents if the collection is not too big. 
- Insert a document into the collection. 

    
### MongoDB specific features 
- Extract a MongoDB validator. 
- Generate a MongoDB validator from a given JSON Schema.
- Register a new MongoDB validator at the database with specific validation action and level. 
- Validate all or individual documents against a MongoDB validator. 
    - Find the documents that do not validate. 
    - Get the amount of valid documents.  




## Installation 

Josch is implemented in Java, but the third-party-tools used by Josch requires other compilers.
 These have to be installed and be accessible. 

Some aspects of Josch require *environment variables* (short: variables, EV) to be set. The setting 
of these is dependent on your operating system (OS). Please refer to the manual in order to find out 
how to set and modify them. 

Whenever a `command` is given, please execute it in your OS' shell/terminal. The shell is the command
line interface of your operating system. Please note that the shell has to be restarted after each 
environment variable is set. 

### Josch

Josch uses Java 14 or higher. You can use [OpenJDK](https://jdk.java.net/java-se-ri/14) or 
[Oracle JDK](https://www.oracle.com/java/technologies/javase-downloads.html). 

### jsonsubschema (containment)

To use the JSON Schema Containment tool *jsonsubschema*, the following needs to be installed:

#### Python 3.8

The schema containment checking tool jsonsubschema requires 
[Python 3.8](https://www.python.org/downloads/release/python-385/) or higher.

#### Pipenv

Pipenv creates and manages virtual environments for Python projects. There are two ways to install 
it: Isolated or pragmatic. For further information see the 
[Pipenv documentation](https://pipenv.pypa.io/en/latest/install/#installing-pipenv). 
We do  generally suggest performing an isolated installation, which includes adding Pipenv to 
the `PATH` variable. 

 Josch requires that the location of Pipenv is part of your `PATH` variable, so please ensure 
 that pipenv is accessible from your shell by the command `pipenv --version`. 

#### Setup

Open the cloned directory of this repository and navigate (via your shell) to `tools\JsonSubSchema` 
and execute the command `pipenv install` in order to install all required Python modules. 

You can also move this directory to another place, but please make sure to specify the correct path
 in Josch (settings can be applied in the user interface).

### is-json-schema-subset (containment)

To use the JSON Schema Containment tool *is-json-schema-subset*, the following needs to be installed:

1. [Node.js](https://nodejs.org/en/) JavaScript compiler.
2. [Yarn](https://classic.yarnpkg.com/en/docs/install#windows-stable) package manager.

#### Setup

Open the cloned directory of this repository and navigate (via your shell) to
 `tools/IsJsonSchemaSubset` and execute the command `yarn install` in order to install all required 
 Node modules there. 

You can also move this directory to another place, but please make sure to specify the correct path
 in Josch (settings can be applied in the user interface).

### Hackolade (extraction)
Hackolade is a commercial tool to extract JSON Schema and 
MongoDB validator from the MongoDB database server. In order to use Hackolade with Josch a
 *Professional Edition Licence* is required. Before starting Josch and using Hackolade, it has to be 
installed and set up using the following steps:

1. Start the application and click on `common tasks`. Then click on `Reverse-Engineer target`.
2. Choose `MongoDB` with the according `target version` of your database. Now click the `Create` 
button and finally the `Add` button. 
3. Configure the connection to your database and enter the name that you want. Note that you have to
remember the name and pass it to Josch later on. Confirm the settings by hitting the `save` button.
4. After saving the connection, your database should show up in the list. Hackolade isn't required
anymore and can be closed. 
5. Add the installation path of hackolade to a `hackolade` environment variable and add it to the 
`PATH` variable as well.

### json-schema-inferrer (extraction)

As this is a Java library, it is contained in Josch.

## Execute Josch

Josch is developed as a multi-module Maven Project. You can either use your Java IDE to execute it or 
you can use Maven directly.

##### Build with Maven 

1. Navigate to the `josch` directory of the repository via your shell. It holds a `pom.xml` and the
 submodules. 
2. Execute the command `mvn clean install`. 
3. Navigate to the subdirectory `josch.presentation\josch.presentation.gui\josch.presentation.gui.controller\target`.
4. Execute the command `java -jar josch-1.0-jar-with-dependencies.jar`. For this command to 
   work the Java application has to be on your `PATH` variable. 

#### Build with IDE

Import Josch as a `Maven Project` via IDE and build the Project accordingly. The main class and
 method to launch the application is `josch.presentation.gui.controller.App.main()`.



## Expandability

- Easy integration of new NoSQL document stores that base on JSON data. 
- Easy integration of new schema extraction and containment tools.  
- Different color themes that can be extended upon. 

As Josch is a multi-module Maven Project. It can be extended easily. Extensions can be made in any 
given layer. The implementation of extensions is similar for all layers and extensions except for 
the presentation layer because it has no layer above.

### Extend Josch

In order to extend Josch, you need to create a new Maven submodule in the respective component (`josch.services.<COMPONENT>`). To make your implementation stick to Josch, use the interfaces and abstract classes in the corresponding layer (`josch.<LAYER>.interfaces`).  Each submodule is required to have a `module-info.java` in order to avoid transitive dependencies and needs to be registered in the parent `pom.xml`. Examples can be found in every leaf module, e.g. `josch.services.comparison.jsonsubschema`.

After you have implemented the new module, you have to register it within Josch:

- To make the module selectable in the user interface, register the module as new value in the respective component. These can be found at `josch.model.enums`. E.g. to register a new module for checking containment, add it to `josch.model.EContainmentTools.java`
- To make the module work in Josch internally, register it in the respective factory. Each layer has its own factory (`josch.<LAYER>.factory`). In order to register it, add it to the respective `switch` statement.


Implemented by [@daubersc](https://github.com/daubersc)

