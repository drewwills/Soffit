# Soffit for Apereo uPortal

Soffit is a technology for creating content that runs in [Apereo uPortal](https://www.apereo.org/projects/uportal).  It is intended as an alternative to [JSR-286 portlet development](https://jcp.org/en/jsr/detail?id=286).  For the present, this technology is developed as an add-on in a separate repo (this one);  in the near future, we hope to include it to the uPortal project directly and depricate this repo.  We will post an announcement here when that happens.

## Why Would I Want This Component?

You are a Java web application developer.  You are tasked with developing content for [Apereo uPortal](https://www.apereo.org/projects/uportal).

You are not excited about doing Java Portlet development [in the traditional way](http://www.theserverside.com/tutorial/JSR-286-development-tutorial-An-introduction-to-portlet-programming) or even using [Spring Portlet MVC](http://docs.spring.io/autorepo/docs/spring/3.2.x/spring-framework-reference/html/portlet.html).  You correctly conclude that the Java Portlet APIs are _large_, _obtuse_, and _actively interfere_ with contemporary web development practices and frameworks that you want to be using.

Apereo Soffit is an alternative approach to producing content for uPortal that is not based on JSR-286 or the portlet container.

## How Does It Work?

Soffit is a strategy for adding content to uPortal.  It defines and manages the relationship between content objects (soffits) and the portal service itself.  Currently there are two required steps for using soffits.

### Step One:  Add Support for Soffit to uPortal

Soffit is a lightweight addition to uPortal;  very few changes are needed.  There is a [pull request in the uPortal Git repo](https://github.com/Jasig/uPortal/pull/665) that contains a branch with all necessary settings.  (*NOTE:*  the Soffit `.jar` file is not "finished" and therefore it is not released into [Maven Central](http://search.maven.org/);  you will need to `$git clone` this project and then `$gradle install` it locally *before* you can build uPortal with Soffit.)

#### Instructions for merging Soffit support

*Important!*  Make sure you clone this project and run `$./gradlew clean install` before performing the steps below.  This step is a prerequisite.

From inside the root of your uPortal repo...

``` bash
// Recommended:  add Soffit technology on a feature branch
$git checkout -b add-soffit

$git remote add drewwills https://github.com/drewwills/uPortal.git

$git fetch drewwills

$git merge drewwills/add-soffit

$ant clean deploy-ear
```

### Step Two:  Develop a Soffit

A soffit's interaction with the portal is HTTP-based.  It is posible to write a soffit in any language or platform that can accept, process, and respond to a connection over HTTP.  At the present time, the creators of Soffit expect to develop soffits mostly with [Java](http://www.oracle.com/technetwork/java/index.html) and [Spring Boot](http://projects.spring.io/spring-boot/).

#### Minimal Soffit Setup Instructions Using Spring Boot

1. Use the [Spring Initializer](https://start.spring.io/) to create a new Spring Boot project with the following settings:
    * Gradle Project (recommended)
    * Packaging=*War* (recommended)
    * Dependencies=*Cache* (recommended) & *Web* (required)
    * Additional dependencies you intend to use
1. Add Soffit as a dependency to your project (see below)
1. Add the `tomcat-embed-jasper` dependency to your project (see below)
1. Add the `@SoffitApplication` annotation to your application class (the one annotated with `@SpringBootApplication`)
1. Create the directory path `src/main/webapp/WEB-INF/soffit/`
1. Choose a name for your soffit and create a directory with that name inside `/soffit/` (above);  recommended:  use only lowercase letters and dashes ('-') in the name
1. Create a `view.jsp` file inside the directory named for your soffit;  add your markup (e.g. `<h2>Hello World!</h2>`)
1. In `src/main/resources/application.properties`, define the `server.port` property and set it to an unused port (like 8090)
1. Run the command `$gradle assemble` to build your application
1. Run the command `$java -jar build/lib/{filename}.war` to start your application

That's it!  You now have a functioning, minimal Soffit application running on `localhost` at `server.port`.

##### Adding the Soffit dependency

Gradle Example:

``` gradle
compile("org.apereo.portal:soffit:0.9.0-SNAPSHOT")
```

Maven Example:

``` xml
<dependency>
    <groupId>org.apereo.portal</groupId>
    <artifactId>soffit</artifactId>
    <version>0.9.0-SNAPSHOT</version>
</dependency>
```

##### Adding the `tomcat-embed-jasper` dependency

Gradle Example:

``` gradle
configurations {
    providedRuntime
}

providedRuntime('org.apache.tomcat.embed:tomcat-embed-jasper')
```

## Publishing your Soffit

Follow these steps to view your soffit in uPortal.

### In the Portlet Manager

* Select _Register New Portlet_
* Choose _Portlet_ in the list of types and click _Continue_
* Select _/uPortal_ and _Soffit Connector_ in the Summary Information screen and click _Continue_ (assumes you have Soffit installed in your portal)
* Enter portlet metadata normally (_e.g._ name, tile, fname, groups, categories, lifecycle state, _etc._)
* Under Portlet Preferences, override the value of `org.apereo.portlet.soffit.connector.SoffitConnectorController.serviceUrl` with the URL of your soffit, _e.g._ `http://localhost:8090/soffit/my-soffit` running independently (outside Tomcat) or `http://localhost:8080/my-porject/soffit/my-soffit` running inside Tomcat
* Click _Save_

## Modern Web User Interfaces

Soffit assumes that you want to develop user interfaces using Javascript and modern frameworks like [React](https://facebook.github.io/react/), [AngularJS](https://angularjs.org/), [Backbone.js](http://backbonejs.org/), _etc_.  Normally a Soffit component will render one time;  considerations like state changes, transactions, persistence, _etc_. are typically handled with Javascript and REST.

## Configuration Options

### Caching

Caching in soffits is available _via_ the standard HTTP header `Cache-Control`.

#### Example

``` http
Cache-Control: public, max-age=300
```

Cache scope may be `public` (shared by all users) or `private` (cached per-user).  Specify `max-age` in seconds.

## Sample Applications

There are several sample applications in [this repo](https://github.com/drewwills/soffit-samples).
