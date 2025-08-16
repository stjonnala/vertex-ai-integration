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

The application includes endpoints for placing bracket orders in Alpaca.

#### Standard Bracket Order

You can test this endpoint using a web browser or a tool like Postman or curl:

```
GET http://localhost:8080/api/alpaca/bracket-order?ticker=AAPL&amount=3000.00&limitPrice=150.00&takeProfitPrice=155.00&stopLossPrice=145.00
```

All parameters are passed as query parameters in the URL. All parameters are required:

- `ticker`: The ticker symbol of the stock to trade (e.g., AAPL, MSFT)
- `amount`: The dollar amount you want to invest (e.g., $3000, $4000). The system will calculate how many shares to buy based on the current price.
- `limitPrice`: The price at which you want to enter the position
- `takeProfitPrice`: The price at which you want to take profit
- `stopLossPrice`: The price at which you want to cut losses

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

#### Limit Bracket Order with Cents-Based Profit and Loss

This endpoint allows you to place a limit bracket order with profit and loss specified in cents above and below the limit price:

```
POST http://localhost:8080/api/alpaca/limit-order?ticker=AAPL&amount=3000.00&limitPrice=150.00&profitCents=50&lossCents=50
```

All parameters are passed as query parameters in the URL. All parameters are required:

- `ticker`: The ticker symbol of the stock to trade (e.g., AAPL, MSFT)
- `amount`: The dollar amount you want to invest (e.g., $3000, $4000). The system will calculate how many shares to buy based on the current price.
- `limitPrice`: The limit price for the buy order
- `profitCents`: The number of cents above the limit price for take profit (e.g., 50 means $0.50 above the limit price)
- `lossCents`: The number of cents below the limit price for stop loss (e.g., 50 means $0.50 below the limit price)

The response will be a detailed text message with information about the order:

```
Successfully placed limit bracket order for AAPL with $3000.00 investment.
Current Price: $150.00
Limit Buy Price: $150.00
Shares Purchased: 20 (approx. $3000.00)
Take Profit Price: $150.50 ($10.00 profit, 0.33% higher)
Stop Loss Price: $149.50 ($10.00 loss, 0.33% lower)
Order ID: b0b6dd9d-8b9b-48a9-ba46-b9d54906e415
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
