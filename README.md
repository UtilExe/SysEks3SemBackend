[![Build Status](https://travis-ci.com/UtilExe/SysEks3SemBackend.svg?branch=master)](https://travis-ci.com/UtilExe/SysEks3SemBackend)

# SYS Project (3rd Term)

## How to use:

Our project consists of a structure where we have our REST classes, and our DTO's where we have request/response fields. 
- Our ServicePointResource consists of a Parallel endpoint, where we make use of Callable and Futures, which makes multiple external calls on separate threads. 

When clonning the project for use as a template, you must modify the endpoints accordingly, such as changing the external API URL's, from eg. iTunes to XX. 
- If you wish to proceed with a Parallel solution, that makes use of Callable and Future, we recommend checking the method responseWithParallelFetch() in SongResource.java, which currently points to three different (external) API's, which are iTunes, Apiary.io (for lyrics) and Tastedive (for similar artist) API. 

- Regarding our DTO naming, we have named them so the specify which entity/API they represent - for instance SongDTO.java -> the Song.java entity class, ITunesDTO.java -> the iTunes api. 

- We have tested all our REST endpoints that don't depend on external API calls (as testing these doesn't prove if our code is wrong as the apis could be down or having other difficulties which would fail the tests on a false basis.

- Remove/Outcomment the if statement in security.SharedSecret:
#### if(true){
####      return "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA".getBytes();
#### }
The code is there for protection, to set a secret base64 encoding for the signature of the token.
This ensures that our token does not change signature upon development, meaning we don't have to switch the token every X time we deploy/run the project, but should not be used for production.

- The Java class, SetupTestUsers is specified as .gitignore, to makes sure we don't push the users and passwords up to Github. 
Therefore, the class needs to be created manually, if not already done. It should look something like the code in this pastebin: https://pastebin.com/FDn526UD

- We have a class where we store all of our keys, which we consider as "secret", and therefore do not want to push on Github. 
The class is located in utils.Keys.java, and we have put a .gitignore to ensure it does not get pushed up.
