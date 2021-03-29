# Read Me First
expiriments with Spring Boot

# Information

### Building a docker image with 'build packs'
The following guides illustrate how to use some features concretely:

* `./gradlew bootBuildImage`
* `docker run -p8080:8080 bootiful:0.0.1-SNAPSHOT`
* `curl -s localhost:8080/customers | jq`
```
[
    {
        "id": 1,
        "name": "Josh"
    },
    {
        "id": 2,
        "name": "Tanzu"
    },
    {
        "id": 3,
        "name": "David"
    }
]
```
