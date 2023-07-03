# Demonstration of Eclipse Vert.x, Kotlin, RxJava2 and Kubernetes

This is a chat application, exposing an API and a reactive VueJS interface.
   It provides real-time message deliveries and shows how the Vert.x event bus can be extended to client-side applications and offer a unified message-passing programming model.
   _(Vert.x / RxJava 3 / Kotlin / MongoDB)_

## Building

Building all services should be as simple as:

    ./gradlew clean build

# Deploying

Docker:

    docker-compose -f docker-compose-for-testing.yml up --build && docker-compose -f docker-compose-for-testing.yml down --remove-orphans && docker volume rm $(docker volume ls -q)

Backend API:

    http GET localhost:8080/api/messages
    http POST localhost:8080/api/messages
         With body:   {
                        "author": "Damian",
                        "content": "SUP!"
                       }

## Contributing

Contributions are welcome, please use GitHub pull requests!
