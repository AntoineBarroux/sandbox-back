# How to build the project

To run this Employee App, you need to clone this repository and the
[Frontend Repository](https://github.com/AntoineBarroux/sandbox-front) within the same directory.

After you successfully cloned the repositories, you have to build the backend application by running the following command within the API project : 
```
./mvnw clean install
```
Warning : you need to have your JAVA_HOME pointing to Java 21. Otherwise, you can just build it from your IDE.

Once the backend application is built, you can just run everything by using the provided docker-compose file :
```
docker-compose up
```

Backend server is hosted on port 8050 and you can find the API documentation here : [API documentation](http://localhost:8050/swagger-ui/index.html#/).
To access it, you of course need to have your API running.

Frontend application runs on port 4200, so you can access it by going to [http://localhost:4200](http://localhost:4200).