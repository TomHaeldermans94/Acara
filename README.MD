![Testing](https://github.com/ThomassenMichiel/Acara/workflows/Testing/badge.svg)

# Testing procedures

## Controllers

Controllers can and shall not be tested without a running container. Controllers only receive inputs and direct them 
further, therefore there should be little to no business logic inside of them. While we won't be integration testing 
them either, we won't be just be unit testing either. This is somewhat in the middle of the road.

### How

We will annotate our controllers using
```java
@WebMvcTest(ControllerToClass.class)
```

It will be necessary to import the following:
```java
@MockBean
@Qualifier("userDetailsServiceImpl")
private UserDetailsService userDetailsService;
@MockBean
private AuthenticationProvider authenticationProvider;
```

Without this, the bare-bone servlet will not start, as it doesn't automatically pick this up.

Please make wise use of the Util-classes, such as creating an entity and mapping it properly.

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

## Test utility classes

Each class will have its own utility class, e.g. ```EventUtil.java```. It will facilitate and organize code reuse 
throughout the tests. All of these methods should be ```static```.

Some of the methods will be:

* a single object
* another object
* a list of objects
    * both wrapper and non-wrapper variants
* a mapper method (code reuse!)
* other helpful methods, such as Base64 encoding and file-reading 

You can add other methods as you see fit.
