# Price Forecasting API
Welcome to the AWS Spot Price Forecasting API.

List of documentation:
- [/](/) - Index Page
- [/docs/index.md](/docs/index.md) - Documentation Index
- [/docs/api.md](/docs/api.md) - Documentation for api endpoint
- [/docs/forecast.md](/docs/forecast.md) - Documentation for forecasting endpoint.

Summary of API endpoints:
- [/api](/api) - Get links to endpoints underneath `api` endpoint.
- [/api/forecast](/api/forecast) - Get forecast for given category or list supported categories.

Additional debugging endpoints:  
These are only activated if the profile `debug-routes` is set active in the `application.properties`
file:
```properties
# exposes extra routes for debugging
# should be commented out for production
spring.profiles.active=debug-routes
```
- [/api/__debug/classpath](/api/__debug/classpath) - List all files accessible on the classpath
- [/api/__debug/missing](/api/__debug/missing) - Throw a MissingServletRequestParameterException
- [/api/__debug/baseException](/api/__debug/baseException) - Throw a BasePriceAppException
