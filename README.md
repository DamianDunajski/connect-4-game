# Connect 4 game

This project is a example implementation of *Connect 4* game, made for the interview purpose.

Current implementation is far from being final product ready for deployment, instead it fulfills functional and technical requirements described in [problem specification](https://github.com/michaeldfallen/coding-tests/blob/master/Connect_4.md).

Current implementation is based on following assumptions:

- players should be able to play the game on different devices and from different locations so for that reason process of initiating the game has been spit into two steps:

    - first player has to create new game represented by unique ID
    - second player can join existing game based on game ID

Know problems:

- each REST service instance operates on it's own list of games which makes scaling difficult (use of sticky sessions might mitigate this problem)
- games are not removed from the list of games in any way which may result in exhaust of the server resources (game timeout feature might solve this problem)

## Components

Project is built from following components:

- [domain](domain/README.md) - domain classes representing game, board and players
- [rest-service](rest-service/README.md) - HTTP service exposing REST API

## Contributing

### Requirements

To work with the project following tools must be installed on the developer machine:

- [Maven](http://maven.apache.org)

### How to build it

Note: This manual assumes that Maven is installed on the developer machine as mentioned above.

To build the project please execute following command:

```
mvn clean verify
```
