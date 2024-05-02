# Java practical test assignment (Clear Solutions)

## RESTful API for **User** resource

Notes:
1. Since use of database was not necessary and the data persistence layer was not required, users are stored in **users** list in [UserService](src/main/java/org/testtask/clearsolutions/restapi/TestTaskUsersRestApi/service/impl/UserServiceImpl.java);
2. **users** list is filled with initial data using **initialize()** method in [UserService](src/main/java/org/testtask/clearsolutions/restapi/TestTaskUsersRestApi/service/impl/UserServiceImpl.java);
3. [GlobalExceptionHandler](src/main/java/org/testtask/clearsolutions/restapi/TestTaskUsersRestApi/exception/GlobalExceptionHandler.java) handles exceptions;
4. [UserController](src/main/java/org/testtask/clearsolutions/restapi/TestTaskUsersRestApi/controller/UserController.java) responses are in JSON format and include hypermedia links according to HATEOAS principles;
5. Minimum age for user creation is assigned using **user.minimum.age** property in [application.properties](src/main/resources/application.properties).
