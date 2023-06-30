# Demonstration of Eclipse Vert.x, Kotlin, RxJava2 and Kubernetes

This is a chat application, exposing an API and a reactive VueJS interface.
   It provides real-time message deliveries and shows how the Vert.x event bus can be extended to client-side applications and offer a unified message-passing programming model.
   _(Vert.x web / web client / SockJS event bus bridge / RxJava 3 / MongoDB / VueJS)_

The [kubernetes](kubernetes) folder contains resource descriptors and notes for Kubernetes and `minikube`.

## Building

Building all services should be as simple as:

    ./gradlew assemble

While developing a Vert.x service you can have live-reload, as in:

    ./gradlew :zlack:vertxRun

# Deploying

_The following assumes a local testing environment with `minikube`._

Building all Docker images and creating Kubernetes resources can then be done using:

    ./deploy-to-kube.sh

...or calling the Gradle task that delegates to this script:

    ./gradlew deployToKube

Mongo:

    docker-compose -f docker-compose-for-testing.yml up

Backend API:

    http GET localhost:8080/api/messages
    http POST localhost:8080/api/messages author='Damian Lattenero' content='SUP?!'

Docker:

    docker build -t zlack:latest .

## Contributing

Contributions are welcome, please use GitHub pull requests!
