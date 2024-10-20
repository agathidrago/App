# App
This project is a Spring Boot application that aggregates search results from multiple search engines like Google and Bing. It leverages REST APIs to query these search providers and return the total number of hits for a given search query.

## Features
- Aggregates search results from Google Custom Search and Bing Search APIs.
- Handles parallel search execution to speed up search queries.
- Custom error handling for better user feedback in case of failures.
- Easy configuration via application.properties.
## Technologies Used
- Java 21: The programming language used for building the application.
- Spring Boot: For dependency injection, RESTful API implementation, and ease of setup.
- RestTemplate: For making HTTP requests to external APIs.
- Maven: Dependency management and project build tool.
## Prerequisites
- Java 21 installed
- Maven installed
- A Google API key and a Bing API key
## Setup
1. Clone the repository
bash
Copy code
git clone https://github.com/your-username/App.git
cd App
2. Set Up API Keys

Add these variables to your environment before running the application
In the application.properties file, update the following environment variables: 
GOOGLE_API_KEY=<your_google_api_key>
BING_API_KEY=<your_bing_api_key>


Alternatively, you can replace the placeholders directly with your keys:

search.google.apiKey=<your_google_api_key>
search.bing.apiKey=<your_bing_api_key>

3. Build the project
Run the following Maven command to build the project:
- mvn clean install

4. Run the Application
- mvn spring-boot:run

The application should now be running on http://localhost:8080.

## API Endpoints
Search API

The main endpoint to aggregate search results:

Request: 
POST /get-hit-number

Example Request:
- curl -X POST "http://localhost:8080/get-hit-number?searchWords=Hello"

Example Response:


{
  "GoogleSearchService": 21000000,
  "BingSearchService": 52000000
}

## Error Handling
Custom exceptions such as SearchServiceException are thrown to handle errors like invalid search queries or API failures. These errors are caught and handled by a global error handler, returning meaningful messages to the client.

## Running Tests
Unit tests have been written for the different services and controllers in the application. To run the tests:

- mvn test
