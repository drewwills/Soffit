# Apereo Soffit

Soffit is a technology for creating content that runs in [Apereo uPortal](https://www.apereo.org/projects/uportal).  It is intended as an alternative to [JSR-286 portlet development](https://jcp.org/en/jsr/detail?id=286).

## Why Would I Want This Component?

You are a Java web application developer.  You are tasked with developing content for [Apereo uPortal](https://www.apereo.org/projects/uportal).

You are not excited about doing Java Portlet development [in the traditional way](http://www.theserverside.com/tutorial/JSR-286-development-tutorial-An-introduction-to-portlet-programming) or even using [Spring Portlet MVC](http://docs.spring.io/autorepo/docs/spring/3.2.x/spring-framework-reference/html/portlet.html).  You correctly conclude that the Java Portlet APIs are _large_, _obtuse_, and _actively interfere_ with contemporary web development practices and frameworks that you want to be using.

Apereo Soffit is an alternative approach to producing content for uPortal that is not based on JSR-286 or the portlet container.

## How Does It Work?

Add Soffit to your Java Web Application.  Use `.jsp` or `.html` files to create the markup you want to appear in your content.  That's it!  With every other aspect of your project, simply carry on with what you were doing.  Soffit allows your content to run in uPortal and gets out of your way so you can do development the way that you want to.

### Modern Web User Interfaces

Soffit assumes that you want to develop user interfaces using Javascript and modern frameworks like [React](https://facebook.github.io/react/), [AngularJS](https://angularjs.org/), [Backbone.js](http://backbonejs.org/), _etc_.  Normally a Soffit component will render one time;  considerations like state changes, transactions, persistance, _etc_. are typically handled with Javascript and REST.

## Minimal Soffit

Follow these three steps to create a minimal Soffit component.

### Add Soffit as a dependency to your project

Gradle Example:

``` gradle
compile group: 'org.apereo.portal', name: 'soffit', version: "1.0.0-SNAPSHOT"
```

Maven Example:

``` xml
<dependency>
    <groupId>org.apereo.portal</groupId>
    <artifactId>soffit</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### Define the SoffitRendererController as a bean within your application

Spring Boot Example:

``` java
@Bean
public SoffitRendererController soffitRendererController() {
    return new SoffitRendererController();
}
```

### Provide a `.jsp` file with your markup

`/WEB-INF/soffit/my-soffit/view.jsp`:

``` jsp
<h2>This is my amazing Soffit!</h2>
```

That's it -- try it out!

You're ready to build and start your soffit.  If you're running locally, you can either use a different port than the portal (e.g. 8090) or deploy your `.war` file to Tomcat beside the portal.

## A note on `tomcat-embed-jasper` for Spring Boot applications

Spring Boot is a great way to build soffits.  The embedded Tomcat environment provided with a Spring Boot application requires the `tomcat-embed-jasper` dependency to leverage JSPs.

Gradle Example:

``` gradle
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
