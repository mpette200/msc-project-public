# API to Access Forecasted Prices
## Usage Demonstration

Clone the repository into repo_clone
```bash
git clone https://github.com/Birkbeck/msc-computer-science-project-2021_22-mpette200.git repo_clone
```

Run the pre-built Jar File (requires **Java JDK version 17**)
```bash
java -jar "repo_clone/built-jars/priceapp-0.0.1.jar"
```

Then open a web browser [firefox](https://www.mozilla.org/en-US/firefox/browsers/)
or Postman [postman](https://www.postman.com/)
and visit the following links:
- [http://localhost:8080/](http://localhost:8080/) -
Index Page, which provides links to further documentation
- [http://localhost:8080/docs/forecast.md](http://localhost:8080/docs/forecast.md) -
Documentation of forecast endpoint
- [http://localhost:8080/api/forecast?category=g4ad.4xlarge__Red%20Hat%20Enterprise%20Linux__eu-west-1a](http://localhost:8080/api/forecast?category=g4ad.4xlarge__Red%20Hat%20Enterprise%20Linux__eu-west-1a) -  
Get a forecast for category `g4ad.4xlarge__Red%20Hat%20Enterprise%20Linux__eu-west-1a`
