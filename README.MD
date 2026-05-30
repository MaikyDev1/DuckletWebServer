# Ducklet Light Web Server | WIP |

A simple, fast, annotation based web server for light projects. Built on top of java native [HttpServer](https://docs.oracle.com/javase/8/docs/jre/api/net/httpserver/spec/com/sun/net/httpserver/HttpServer.html),
where I added cool features, in a Java Spring style.

> [!CAUTION]
> Please use with caution, the framework is in early alpha; bugs and vulnerabilities may exist.

### Features
- Simple to use **response/requests** models.
- URL parameters (*Tag lines*)
- Boot's fast.
- Consumes minimal resources
- Build-in authentification if needit.

### Known issues/Missing features
- [ ] 🔒 SSL and HTTPS is not supported by the framework. You need to use a proxy for that.
- [ ] 💻 Authentification is not tested thruly.
- [ ] Minimal authentification modes.
- [ ] Only one JSON parser supported

## Usage

1. First get the framework from the binaries or JitPack (For Maven useage).

## Routes

Each java class must have ```@RequestMapping("[mapping]")``` then you have to tell the controler that the class exists. By using ```DuckletController#addRoute(route)```

In each route you need to create the endpoints, they are just a public method that will return a ```DuckletResponse```.
To make an endpoint work, you need to ```@RequestMapping("[mapping]")``` and annotate the function with the desired http method.
```@GetRequest```, ```@PostRequest```, ```@DeleteRequest```, ```@PutRequest```, ```@PatchRequest```.

To get request information you need want to use:

| Annotation                   | Requierd type                | Details                                                                                                                                 |
|------------------------------|------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------|
| @RequestParam(**"name"**)    | **String**                   | This annotation will return into a string a param from the request<br/> (?name=value)                                                   |
| @RequestUrlParam(**"name"**) | **String**                   | This will return into a string an url param. Make sure you have put the right param into RequestMapping _(Example: /test/[id]/example)_ |
| @RequestBody                 | **JsonEntity** or **String** | Will give the body into String format or DuckyJson json format                                                                          |

> [!INFO]
> If the request annotations have nothing to give it will be null.

Here is an example of an Example Route that will have only one endpoint with a url param.

`ExampleRoute.Java`

```java
@RequestMapping("/example")
public class ExampleRoute {

    @RequestMapping("/[id]")
    @GetRequest
    public DuckletResponse example(@RequestUrlParam("") @Re String id) {
        return DuckletResponse.ok("Hello: " + id);
    }

}
```
For the controller to see your route you must provide it.

```java
DuckletController controller = DuckletController.createController(8080, 10);
controller.addRoute(new ExampleRoute());
controller.startController();
```
