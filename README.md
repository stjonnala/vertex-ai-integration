# Vertex AI Integration

This is a Spring Boot application with a sample Hello World controller and Swagger API documentation.

## Prerequisites

- Java 11 or higher
- Maven 3.6 or higher

## Running the Application

To run the application, use the following command:

```bash
mvn spring-boot:run
```

The application will start on port 8080.

## Testing the Controllers

### Hello World Controller

Once the application is running, you can test the Hello World controller by accessing the following endpoint:

```
http://localhost:8080/hello
```

This should return the response: "Hello, World!"

### Alpaca Trading Controller

The application includes an endpoint for placing bracket orders in Alpaca. You can test this endpoint using a tool like Postman or curl:

```
POST http://localhost:8080/api/alpaca/bracket-order
```

Request body example:

```json
{
  "ticker": "AAPL",
  "amount": 3000.00,
  "limitPrice": 150.00,
  "takeProfitPrice": 155.00,
  "stopLossPrice": 145.00
}
```

All fields are required. The `amount` is the dollar amount you want to invest (e.g., $3000, $4000), and the system will calculate how many shares to buy based on the current price. The `limitPrice` is the price at which you want to enter the position, the `takeProfitPrice` is the price at which you want to take profit, and the `stopLossPrice` is the price at which you want to cut losses.

The response will include both the dollar amount invested and the calculated quantity of shares:

```json
{
  "orderId": "b0b6dd9d-8b9b-48a9-ba46-b9d54906e415",
  "takeProfitOrderId": "8624e52d-69e7-4142-a7b1-1ee4d6d3d5f6",
  "stopLossOrderId": "23cb5cad-5c4b-4259-a6e7-66a225f1c6d8",
  "ticker": "AAPL",
  "amount": 3000.00,
  "quantity": 20,
  "status": "ACCEPTED"
}
```

Note: You need to configure your Alpaca API key and secret in the `application.properties` file.

## Swagger API Documentation

The application includes Swagger UI for API documentation and testing. Once the application is running, you can access the Swagger UI at:

```
http://localhost:8080/swagger-ui.html
```

This provides an interactive interface to:
- View all available API endpoints
- Test API endpoints directly from the browser
- View request/response models and parameters
- Download the OpenAPI specification

The raw OpenAPI specification is available at:

```
http://localhost:8080/api-docs
```

## Building the Application

To build the application, use the following command:

```bash
mvn clean package
```

This will create a JAR file in the `target` directory that can be run with:

```bash
java -jar target/vertex-ai-integration-0.0.1-SNAPSHOT.jar
```
