# Customer Custom Fields Data Model

### Functionality
Supports the creation of custom fields for all customers with DROPDOWN/TEXTBOX types.
Repository level methods are provided to retrieve customer's current custom field values and historical values for a custom field.

### Technologies
Using java + springboot + jpa
Spring Data is using an embedded in-memory db and automatically creates the schema, based on the repository entities provided
![Alt text](src/main/resources/docs/customer-customfields-model.jpg?raw=true "Data model diagram")

### Execution
Execute mvn clean install and/or
Run CustomRepositoryTest unit test to exercise all supported scenarios



