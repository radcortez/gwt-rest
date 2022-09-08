# Prototype to Support REST calls in GWT Servlets

Run with :`mvn jetty:run`

Try it out with:

```shell 
curl -XPOST http://localhost:8080/rest/getUser -d "" -H 'Content-Type: application/json'
curl -XPOST http://localhost:8080/rest/createUser -d '{"firstName":"Naruto","lastName":"Uzumaki"}' -H 'Content-Type: application/json'
curl -XPOST http://localhost:8080/rest/updateUser -d '{"firstName":"Boruto","lastName":"Uzumaki"}' -H 'Content-Type: application/json'
```
