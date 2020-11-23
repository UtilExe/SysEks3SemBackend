[![Build Status](https://travis-ci.org/dat3startcode/rest-jpa-devops-startcode.svg?branch=master)](https://travis-ci.org/dat3startcode/rest-jpa-devops-startcode)

# CA3 Backend Project

## How to use:

Our project consists of a structure where we have our REST classes, and our DTO's where we have request/response fields. 
- Our ServicePointResource consists of a Parallel endpoint, where we make use of Callable and Futures, which makes multiple external calls on separate threads. 

When clonning the project, you must modify the endpoints accordingly, such as changing the external API URL's, from eg. Postnord to XX. 
- If you wish to proceed with a Parallel solution, that makes use of Callable and Future, we recommend checking the method responseFromExternalServersParallel() in ServicePointResource.java, which currently points to two different (external) API's, which are Postnord and a Weather API. 

- Several of our other endpoints, such as our getMovieReview() method in MovieResource.java, only make 1 request once triggered. Therefore, we did not believe that it would be necessary to make the method parallel (because we only make one external request once the method is triggered). Therefore our method (eg. getMovieReview() is sequental by default, and is running on one thread. As a consequence, the requests currently would wait for each other to finish, if there were multiple requests at once. 

- Regarding our DTO naming, we have made sure to specify whether we use the DTO for a request, or for a response, as the input name need to match with the API. 

- We have tested all our REST endpoints, where we test the method, and check for a statusCode(200) to make sure the request went well. 

- Remove/Outcomment the if statement in security.SharedSecret:
 if(true){
      return "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA".getBytes();
}
The code is there for protection, to set a secret base64 encoding for the signature of the token.
This ensures that our token does not change signature upon development, meaning we don't have to switch the token every X time we deploy/run the project, but should not be used for production.

- The Java class, SetupTestUsers is specified as .gitignore, to makes ure we don't push the users and passwords up to Github. 
Therefore, the class needs to be created manually, if not already done. 

- We have a class where we store all of our keys, which we consider as "secret", and therefore do not want to push on Github. 
The class is located in utils.Keys.java, and we have put a .gitignore to ensure it does not get pushed up.

Our endpoint links with role restrictions too:
- responseFromExternalServersParallel(): api/servicepoints/servicepoints
- getMovieReview(): api/movie/review (user role ONLY)
- getDigitalOceanInfo(): api/digitalocean/admin (admin role only)
- getFromUser(): api/info/user (user role only)
- getFromAdmin(): api/info/admin (admin role only)
