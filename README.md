#Software component manager

##General information

Software component manager is a product designed to help with overseeing 
modules and components and their versioning.

The overall service is based on a simple Model-View-Controller architecture
where each is represented by a distinct container.

The product is composed of three distinct services: 
- UI/View is an Angular based nginx container. It offers a UI to use with 
the product.
  
- Controller is a Scala container based on an openjdk image. It uses 
  hseeberger/scala-sbt image meant to be used for Scala-based services.
  
- Model is an official postgres container and not much custom work has
been done on this image.

##Container information:

###Angular container

The folder angular contains the un-compiled version of angular service. By using
the dockerfile in this folder, it will be compiled Ahead-of-time and the service
built from it will be copied to the folder angular_dev. This is not fully necessary
when building the service, but helps with de-bugging and quickly re-building the
service.

> The service can be build onto the angular_dev folder with the command: 
> 
>*make update-angular_build*

To make the service truly workable with docker, it must be copied into a server
cabable of running it. This product uses a nginx-docker image. The base-image for
this can be found in the folder angular_dev

> The angular service can be built with the command: 
> 
> *make build-angular_nginx*

After the image has been built, it can be ran using either the docker-compose
found within this product, or can be imported to other systems if needed.

If the docker-compose is used, the angular service is available at the adress:
0.0.0.0:8081. It servers traditional HTTP server that can be used as a GUI for the
rest of the service.

###Scala container

The Scala based container serves as a controller container. It serves a REST API
that offers a simple way to interact with the database of the product. 

>The API is described in openapi format in the file:
> 
> *sw_comp_copenapi.yaml*

The API has both open-ended and more strict endpoints when it comes to the payload
that they expect.

The files needed to build this image are in the folder *scala*.

>Simple breakdown of the .scala files:
>
> - Main: The main body of the scala. HTTP service is started here and the 
>endpoints
> - Models: The models used when inserting to database
> - Client: SQL client used when connecting to the database
> - Retrieve: The functions used to pull data from the database
> - Send: The functions used to insert data into the database
> - Delete: The functions used to delete data from the database
> - Comparison: The functions used to compare different models from 
> the database

There is also the logback.xml, found in /src/main/resources file which 
defines the logging level for the Scala container

###Postgres container

Not much is to be said of this container. It is a pre-built container which
has very little customizing done on it as it works as advertised without
much work.