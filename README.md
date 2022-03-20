## BACKEND TECHNICAL TEST 
#### Spring boot web API (Spring v2.6.4)
***

### Description

backend-technical-test is a Spring based Rest API that allows to manage the orders of the "pilotes" recipe. 

##### API Documentation
- [http://localhost:8081/swagger-ui](http://localhost:8081/swagger-ui "http://localhost:8081/swagger-ui")

### Technical environment
- Java 11.x
- Maven
- Docker
- H2 database 
- Swagger/OpenApi, JJWT, Lombok, MapStruct, Spring security

### Project setup
```
git clone <repo>
```

### Compile and run
```bash
mvn clean install
docker-compose up --build
```

### Features & business rules

- Create an order for a customer by using customer email address
- Update order quantity
- Search orders by customer data
- **Only users with ADMIN authority are allowed to use the search**. To test this feature, you must be logged on by an admin user
 
#### -User authentication endpoint
For test purpose, an admin user is created by default :

username : admin  
password : adminadmin

Admin user credential should be used to login through authenticate endpoint :

POST [http://localhost:8081/api/authenticate](http://localhost:8081/api/authenticate "http://localhost:8081/api/authenticate")

- Request 

```json
{
"username":"admin", 
"password":"adminadmin"
}
```

- Response
 
```json
{
    "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImF1dGgiOiJST0xFX0FETUlOLFJPTEVfVVNFUiIsImV4cCI6MTY0Nzc1MDA5Nn0.ErGc4cv0VyS6234ui0YoWyzbfaw9gTelhO7YrKSgn2dIQMPJBp_LxjCI4lEX_LTf3xcpna8fC4Kx9Q2Jz73-FA"
}
```
#### -Create customer endpoint

To create an order, **a customer account should be created beforehand**. To do so, a register account REST endpoint is provided by the application :

POST [http://localhost:8081/api/register](http://localhost:8081/api/register "http://localhost:8081/api/register")

```json
{
  "login": "customer",
  "password": "customer",
  "firstName": "Customer",
  "lastName": "Test",
  "email": "customer@domain.com",
  "phoneNumber": "+34786577628"
}
```

#### -Create order endpoint
The email of the previously created user is mandatory when creating an Order :

POST [http://localhost:8081/api/v1/orders](http://localhost:8081/api/v1/orders "http://localhost:8081/api/api/v1/orders")
- Request

```json
{
    "email": "customer@domain.com",
    "quantity": 5,
    "street": "Arset El Bilk n 59",
    "postcode": "40000",
    "country": "MA",
    "city": "Marrakesh"
}

```
- Response

```json
{
    "reference": "2-03202022-002",
    "street": "Arset El Bilk n 59",
    "postcode": "40000",
    "city": "Marrakesh",
    "country": "Morocco",
    "quantity": 5,
    "totalPrice": "6.65 EUR",
    "createdAt": "03-20-2022 03:52"
}
```

#### -Update order endpoint
The reference and the quantity are mandatory to update an order :

PUT [http://localhost:8081/api/v1/orders](http://localhost:8081/api/v1/orders "http://localhost:8081/api/api/v1/orders")

- Request

```json
{
"reference":"2-03192022-001",
 "quantity": 5
}
```

#### -Search orders by customer data
Search by email, firstName, lastName and phoneNumber

GET [http://localhost:8081/api/v1/orders?email=cust&firstName=cust&page=0&size=5](http://localhost:8081/api/v1/orders?email=cust&firstName=cust&page=0&size=5 "http://localhost:8081/api/api/v1/orders?email=cust&firstName=cust&page=0&size=5")

- Response

```json
{
    "orders": [
        {
            "reference": "2-03202022-001",
            "street": "street of fighters",
            "postcode": "40000",
            "city": "Marrakesh",
            "country": "Morocco",
            "quantity": 5,
            "totalPrice": "6.65 EUR",
            "createdAt": "03-20-2022 03:21"
        },
    ],
    "totalElements": 1
}
```

## License
[MIT](https://choosealicense.com/licenses/mit/)