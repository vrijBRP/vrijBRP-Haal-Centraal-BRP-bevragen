# vrijBRP Haal Centraal BRP Bevragen
API for searching people, including parents, children and partners in the BRP.\
This is an implementation based on version 0.8 of the **Haal Centraal BRP bevragen** API.

## License
Copyright &copy; 2021 Procura BV. \
Licensed under the [EUPL](https://github.com/vrijBRP/vrijBRP/blob/master/LICENSE.md)

## Getting started
_Committing this code to GitHub is a first step towards an open source BRP._ \
_This application could have maven dependencies that might not be publicly available at this moment._\
_It also depends on several components that will become open source later._

## Build

#### Build requirements
- Java 11
- Maven 3
- [Java Code formatting](https://github.com/vrijBRP/vrijBRP/blob/master/CONTRIBUTING.md)
- Eclipse code formatter (**Intellij**)
- Lombok plugin (**optional**)

#### Build commands
`mvn clean package`

### Run the application
Create a run configuration to start `nl.procura.haalcentraal.brp.bevragen.HcBrpBevragenApplication`

#### Run requirements
- running vrijBRP Balie instance

### Docker image
A docker image will become available publically in the near future.

## Links
[API specification VNG realisatie](https://github.com/VNG-Realisatie/Haal-Centraal-BRP-bevragen)
