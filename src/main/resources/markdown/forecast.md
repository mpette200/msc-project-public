# API Documentation
## GET "/api/forecast?category=x"
Returns the forecast for category `x`.  
Example request URL:
`http://localhost:8080/api/forecast?category=c5d.xlarge__SUSE%20Linux__eu-west-1a`
Example output:
```json
{
  "forecast": {
    "mean": 0.13273106476502233,
    "stdDeviation": 0.04748225739131175,
    "startDate": "2022-09-01T10:54:26.5646917Z",
    "category": "c5d.xlarge__SUSE Linux__eu-west-1a",
    "forecastHorizonDays": 7
  }
}
```

If a category is not provided or an invalid category is provided then return list
of supported categories.  
Example request URL:
`http://localhost:8080/api/forecast`
Example output:
```json
{
  "links": [
    "http://localhost:8080/api/forecast?category=c4.8xlarge__Red%20Hat%20Enterprise%20Linux__eu-west-1b",
    "http://localhost:8080/api/forecast?category=c4.8xlarge__SUSE%20Linux__eu-west-1b",
    "...",
    "...",
    "http://localhost:8080/api/forecast?category=x2gd.8xlarge__SUSE%20Linux__eu-west-1a",
    "http://localhost:8080/api/forecast?category=x2gd.8xlarge__Linux/UNIX__eu-west-1a"
  ]
}
```
