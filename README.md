# cit-ticketing
CIT-TICKETING : Management of issues 

This repository hosts open source components of Computaceae-IT CE products. cit-ticketing has a micro service Spring boot which allows to create tickets. 

## Installation
```
mvn clean install -Dmaven.test.skip=true
export MAIL=<TESTING MAIL>
export TOKEN_GITHUB=<TOKEN_GITHUB>
export USER_GITHUB=<USER_GITHUB>
export REPOSITORY_GITHUB=<REPOSITORY_GITHUB>
docker-compose up
```
## Documentation
See Javadoc [LINK](https://cjb-geneve.github.io/cit-ticketing/javadoc/)


## Release tags

There will be a git tag for each general
availability release. 