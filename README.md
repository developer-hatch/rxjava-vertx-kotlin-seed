# Demonstration of Eclipse Vert.x, Kotlin, RxJava2 and Kubernetes

This is a chat application, exposing an API and a reactive VueJS interface.
   It provides real-time message deliveries and shows how the Vert.x event bus can be extended to client-side applications and offer a unified message-passing programming model.
   _(Vert.x web / web client / SockJS event bus bridge / RxJava 3 / MongoDB / VueJS)_

## Building

Building all services should be as simple as:

    ./gradlew assemble

While developing a Vert.x service you can have live-reload, as in:

    ./gradlew :zlack:vertxRun

# Deploying

Docker:

    docker-compose -f docker-compose-for-testing.yml up --build && docker-compose -f docker-compose-for-testing.yml down --remove-orphans

Backend API:

    http GET localhost:8080/api/messages
    http POST localhost:8080/api/messages author='Damian Lattenero' content='SUP?!'

## Contributing

Contributions are welcome, please use GitHub pull requests!
