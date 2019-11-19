# Requirements Conformance to Templates

_This service was created as a result of the OpenReq project funded by the European Union Horizon 2020 Research and Innovation programme under grant agreement No 732463._

## Introduction

This service is meant to provide automation for checking requirements conformance to boilerplates/templates. A boilerplate or template organizes the syntactic structure of a requirement statement into some pre-defined slots.

## Technical description

Next sections provide a general overview of the technical details of the conformance to templates service.

### Main functionalities

There are two type of operations (each method has a more extensive description in its own operation box):

    - Main methods
        - InTemplates: Adds the input templates to the service's database and assigns them to a specified organization. The templates must be written following a defined format explained later in this section.
        - OutTemplates: Returns the templates of a specified organization.
        - Conformance: Checks if the input requirements follow at least one of the templates of a specified organization.
    - Auxiliary methods
        - DeleteOrganizationTemplates: Deletes the templates of a specified organization.
        - ClearDatabase: Deletes all data from the database.

The API uses UTF-8 charset. Also, it uses the OpenReq format for input JSONs.

### Templates

A template follows a modified BNF diagram:

    - A template is defined by one or more rules (the rules are defined by an array of strings).
    - The first word of each rule must be written as " <name_of_the_rule> ::= " that defines the name of the rule.
    - The name of the first rule must be main.
    - The first rule must define the structure of the requirement.
    - The other rules should be used to define auxiliary structures.
    - The permitted tags are:
        - plain words (specified with "%").
        - pos tags of the OpenNLP library (specified with "()").
        - sentence tags -NP or VP- (specified with "<>").
        - component special tags:
            - | : OR
            - (all) : ignores the rule
            - <*> : accepts anything that comes after

        
An example (Rupp template):

    - <main> ::= <opt-condition> <np> (md) (vb) <np> | <opt-condition> <np> <modal> %PROVIDE <np> %WITH %THE %ABILITY <infinitive-vp> <np> | <opt-condition> <np> <modal> %BE %ABLE <vp> <np>
    - <conditional-keyword> ::= %IF | %AFTER | %AS %SOON %AS | %AS %LONG %AS
    - <modal> ::= %SHALL | %SHOULD | %WOULD
    - <opt-condition> ::= <conditional-keyword> | (all)
    - <infinitive-vp> ::= %to <vp>


### How to install

Steps to configure the service:

    1. Download the next opennlp models from http://opennlp.sourceforge.net/models-1.5/ and save them in $ServiceDirectory:

        - en-chunker.bin
        - en-pos-maxent.bin
        - en-pos-perceptron.bin

    2. Download and install 8 JDK and last Maven version. 
    
    3. Run the service (Follow the steps explained below)
    
    4. Create the default database file calling the method ClearDatabase exposed in the API i.e. curl -X DELETE 'http://localhost:9409/upc/reqquality/check-conformance-to-templates/ClearDatabase'

Steps to run the service:

    1. Open a terminal and copy-paste "sh run_conformance_to_templates.sh". Wait for an exit like this: upc.req_quality.ConformanceApplication : Started ConformanceApplication in 3.24 seconds

    2. Go to http://localhost:9409/swagger-ui.html#/ to see the swagger generated. You can use the component through the swagger or through http connections to the endpoints indicated in the documentation. 

### How to use it

All requests must be sent to "http://localhost:9409/upc/reqquality/check-conformance-to-templates/".

Check API details [here](https://api.openreq.eu/conformance-to-templates/swagger-ui.html#/).

### Notes for developers

### Sources


## How to contribute

See OpenReq project contribution [guidelines](https://github.com/OpenReqEU/OpenReq/blob/master/CONTRIBUTING.md)

## License

Free use of this software is granted under the terms of the EPL version 2 (EPL2.0).
