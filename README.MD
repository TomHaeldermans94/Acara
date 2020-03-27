![Testing](https://github.com/ThomassenMichiel/Acara/workflows/Testing/badge.svg)

# Testing procedures

## Controllers

Controllers can and shall not be tested without a running container. Controllers only receive inputs and direct them 
further, therefore there should be little to no business logic inside of them. While we won't be integration testing 
them either, we won't be just be unit testing either. This is somewhat in the middle of the road.

### How

We will annotate our controllers using
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
```

```@SpringBootTest``` will start up a mocked container on a random port.
```@ActiveProfiles``` will use the matching application.properties file.

### What

The behaviour of the expected path:
* does the method return the specified object?
    * is it a list or a single object? Or even a wrapper?
* does it handle exceptions properly?
* are the responses and status codes correct?
* if using Mockito, are the mocks called the correct amount of times?
* is the coverage of the tested class and methods 100%?

### Which tools

##### Mockito
Self explanatory. This makes us not worry about non-existent things.

##### RestAssured
A great REST-based tool that offers great flexibility and versatility, while being significantly less verbose than 
MockMVC.

## Services

Unit test any and all services you see.

### How
```java
@ExtendWith(MockitoExtension.class)
```

Dependencies are mocked. Don't forget your ```MockitoAnnotations.initMocks(this)```.

### What

Just about the same points as the controllers. These don't generate ResponseEntities fortunately.

### Which tools

#### Mockito
Just this one.

## Pojos, Entitys, Domain-models

These are not explicitly tested unless there's business logic in any of the getters or setters.

## Database

### Full E2E

Database testing is only done when testing everything. This means running the full application with a pre-populated 
testing database. This is one of the latest steps to be taken.
