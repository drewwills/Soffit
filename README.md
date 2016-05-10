# Apereo Soffit

Soffit is for creating [JSR-286 Java Portlets](https://jcp.org/en/jsr/detail?id=286) when you don't want to do Java Portlet Development.

## Why Would I Want This Component?

You are a Java web application developer.  You need to develop content for [Apereo uPortal](https://www.apereo.org/projects/uportal) or another portal platform that supports Java Portlet Specification 2.x.

You are not excited about doing Java Portlet development [in the traditional way](http://www.theserverside.com/tutorial/JSR-286-development-tutorial-An-introduction-to-portlet-programming) or even using [Spring Portlet MVC](http://docs.spring.io/autorepo/docs/spring/3.2.x/spring-framework-reference/html/portlet.html).  You correctly conclude that the Java Portlet APIs are _large_, _obtuse_, and _actively interfere_ with contemporary web development practices and frameworks that you want to be using.

Apereo Soffit is an approach to producing content that runs in a JSR-286 portlet container.  It isn't another approach to Java Portlet Development _per se_;  it's a way to "mix portlet-ness into" the project you're already developing... the project you want to be working on.

## How Does It Work?

Add Soffit to your Java Web Application.  Provide a `portlet.xml` file in which you define the portlets you want.  Use `.jsp` or `.html` files to create the markup you want to appear in your portlet(s).  That's it!  With every other aspect of your project, simply carry on with what you were doing.  Soffit takes care of the portlet-ness and gets out of your way so you can do development the way that you want to.

### Modern Web User Interfaces

Soffit assumes that you want to develop user interfaces using Javascript and modern frameworks like [React](https://facebook.github.io/react/), [AngularJS](https://angularjs.org/), [Backbone.js](http://backbonejs.org/), _etc_.  Normally a Soffit-based portlet will render one time;  considerations like state changes, transactions, persistance, _etc_. are typically handled with Javascript and REST.

## Minimal Soffit Portlet

Follow these three steps to add portlet-ness to your Java webapp.

### Add Soffit as a dependency to your project

Gradle Example:
```
    compile group: 'org.apereo.portlet', name: 'soffit', version: "1.0.0-SNAPSHOT"
```

Maven Example:
```
    <dependency>
        <groupId>org.apereo.portlet</groupId>
        <artifactId>soffit</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </dependency>
```

### Add Spring to your `web.xml`

If you're already using Spring, you may have some of this configuration (or not need it).

```
    <web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee
             http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd"
             version="3.1">

        <display-name>My Java Web Application</display-name>

        <!--
         | You don't need contextConfigLocation or the ContextLoaderListener if
         | you already have a Spring ApplicationContext in your project -- for
         | example, if you have a typical Spring Boot setup.
         +-->
        <context-param>
            <param-name>contextConfigLocation</param-name>
            <param-value>/WEB-INF/context/*.xml</param-value>
        </context-param>
        <listener>
            <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
        </listener>

        <servlet>
            <servlet-name>ViewRendererServlet</servlet-name>
            <servlet-class>org.springframework.web.servlet.ViewRendererServlet</servlet-class>
            <load-on-startup>1</load-on-startup>
        </servlet>

        <servlet-mapping>
            <servlet-name>ViewRendererServlet</servlet-name>
            <url-pattern>/WEB-INF/servlet/view</url-pattern>
        </servlet-mapping>

    </web-app>
```

### Provide a `portlet.xml` file and define your portlet(s)

```
    <portlet-app xmlns="http://java.sun.com/xml/ns/portlet/portlet-app_2_0.xsd"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://java.sun.com/xml/ns/portlet/portlet-app_2_0.xsd
                                http://java.sun.com/xml/ns/portlet/portlet-app_2_0.xsd"
        version="2.0">

        <portlet>
            <portlet-name>my-portlet</portlet-name>
            <portlet-class>org.springframework.web.portlet.DispatcherPortlet</portlet-class>
            <init-param>
                <name>contextConfigLocation</name>
                <value>classpath:/org/apereo/portlet/soffit/mvc/soffit-portlet.xml</value>
            </init-param>
            <init-param>
                <name>viewsLocation</name>
                <value>/WEB-INF/soffit/my-portlet/</value>
            </init-param>
            <expiration-cache>60</expiration-cache>
            <supports>
                <mime-type>text/html</mime-type>
                <portlet-mode>view</portlet-mode>
            </supports>
            <portlet-info>
                <title>My Portlet</title>
            </portlet-info>
        </portlet>

    </portlet-app>
```

### Provide a `.jsp` file with your markup

`/WEB-INF/soffit/my-portlet/view.jsp`:
```
    <h2>This is my amazing Soffit portlet!</h2>
```

That's it -- try it out!
