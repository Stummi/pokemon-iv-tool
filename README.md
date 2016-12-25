# Pokemon IV Calculator

Here you will find a Pokemon IV Calculator written in Java which can be used on command line.

Please note that this is more a proof of concept and far from complete. While I believe the application works correctly, I won't make any guarantees about that.
Also the CLI frontend is a bit cumbersome right now. It's just improvised to make the libraries functionality accessible easily

## Building

To build the tools, you need maven and a current JDK 8. Clone this repository, then type

``` 
mvn package
``` 

and you will end up with a runnable jar file under target. You may want to put it on a known place and add a shell scipt like this in your searchpath ($PATH)

```
#!/bin/sh
exec java -jar ~/local/opt/ivtool.jar "$@"
```

This allows you to invoke the tool in your shell with the given name

## Usage

Assuming you put the tool in your PATH with the name ivtool you can invoke the tool in your shell with these arguments

```
ivtool <pokemon-Id> [cp=<val>] [hp=<val>] [stardust=<val>] [ivsumrating=<0..3|BEST|GOOD|BAD|WORST>] [beststat=<A,D,S>][bestivrating=<0..3|BEST|GOOD|BAD|WORST>]
```

*pokemon-Id* is either the numeric Pokemon Number (starting at 1 for Bulbasaur) or the english name. Just a part of the name is allowed too, as long its not ambigous
*cp*, *hp* and *stardust* are the corresponding values you can see on your pokemon

*ivsumrating* maps the first sentence in your pokemons appraisal, and can be either BEST, GOOD, BAD or WORST (Lookup the sentences if you don't know which means what).

*beststat* is a comaseparated list of the stats which are mentioned as "the best ones" in the appraisal, where 'A' stands for attack, 'D' for defense and 'S' for stamina.

*bestivrating* is the second rating sentence you see in the appraisal, with the same meanings as for ivsumrating

Every parameter except the pokemon id can be omitted, which will just be assumed to not be known then. Omitting everything would give you every single possible combination

## Example and Output
Lets start with a example:

```
$ ivtool pinsir cp=268 hp=33 stardust=400 ivsumrating=bad bestivrating=good beststat=D
Results for your Pinsir: 

== Stats ranges ==
      attack:   8 -  10
     defense:  13 -  14
     stamina:   0 -   2
  perfection:  53

== 5 possible combinations ==
   level|  attack| defense| stamina| perfect
--------------------------------------------
     4,0|      10|      14|       0|      53
     4,0|      10|      13|       1|      53
     4,0|       9|      14|       1|      53
     4,0|       9|      13|       2|      53
     4,0|       8|      14|       2|      53

== Upgrade path ==
5 x [cp=268; hp=33; stardust=400]
  2 x [cp=307; hp=36; stardust=400]
    1 x [cp=346; hp=38; stardust=600] => 9/13/2 - 53%
    1 x [cp=345; hp=38; stardust=600] => 8/14/2 - 53%
  3 x [cp=307; hp=35; stardust=400]
    1 x [cp=346; hp=38; stardust=600] => 10/13/1 - 53%
    1 x [cp=346; hp=37; stardust=600] => 10/14/0 - 53%
    1 x [cp=345; hp=38; stardust=600] => 9/14/1 - 53%
    
worst case upgrade count: 2
$ 
```

Generally the Tool will give you 2 informations:
* The first one is a table of all possible IV combinations, what is what you probably already know from other IV tools.
* The second information will give away a hint about how many Power-Ups you may need to do before knowing the stats for sure.

In this case, after one Power-Up your Pinsir will have a CP-Value of 307 and a HP value of either 36 or 35. 36 means there are two combinations left and 35 means there are 3 combinations left. However, after the second Power-Up you will know the IV-Values for sure

Heres another exmaple
```
$ ivtool mime cp=508 hp=38 ivsumrating=worst beststat=S bestivrating=bad
Results for your Mr. Mime: 

== Stats ranges ==
      attack:   0 -   4
     defense:   1 -   9
     stamina:  10 -  12
  perfection:  33 -  44

== 10 possible combinations ==
   level|  attack| defense| stamina| perfect
--------------------------------------------
    10,0|       4|       2|      10|      36
    10,0|       3|       4|      10|      38
    10,0|       2|       7|      10|      42
    10,0|       1|       9|      10|      44
    10,0|       2|       4|      11|      38
    10,0|       1|       6|      11|      40
    10,0|       0|       9|      11|      44
    10,0|       2|       1|      12|      33
    10,0|       1|       4|      12|      38
    10,0|       0|       6|      12|      40

== Upgrade path ==
10 x [cp=508; hp=38; stardust=1000]
  2 x [cp=534; hp=39; stardust=1000]
    2 x [cp=559; hp=40; stardust=1300]
      2 x [cp=584; hp=41; stardust=1300]
        2 x [cp=610; hp=42; stardust=1300]
          1 x [cp=635; hp=43; stardust=1300] => 1/4/12 - 38%
          1 x [cp=635; hp=42; stardust=1300] => 0/9/11 - 44%
  2 x [cp=533; hp=38; stardust=1000]
    2 x [cp=559; hp=39; stardust=1300]
      2 x [cp=584; hp=40; stardust=1300]
        1 x [cp=609; hp=41; stardust=1300] => 3/4/10 - 38%
        1 x [cp=610; hp=41; stardust=1300] => 1/9/10 - 44%
  4 x [cp=533; hp=39; stardust=1000]
    2 x [cp=559; hp=40; stardust=1300]
      2 x [cp=584; hp=41; stardust=1300]
        1 x [cp=610; hp=42; stardust=1300] => 2/4/11 - 38%
        1 x [cp=609; hp=42; stardust=1300] => 0/6/12 - 40%
    2 x [cp=558; hp=40; stardust=1300]
      2 x [cp=584; hp=41; stardust=1300]
        2 x [cp=609; hp=42; stardust=1300]
          1 x [cp=635; hp=43; stardust=1300] => 2/1/12 - 33%
          1 x [cp=635; hp=42; stardust=1300] => 1/6/11 - 40%
  2 x [cp=534; hp=38; stardust=1000]
    2 x [cp=559; hp=39; stardust=1300]
      2 x [cp=585; hp=40; stardust=1300]
        2 x [cp=610; hp=41; stardust=1300]
          1 x [cp=636; hp=42; stardust=1300] => 2/7/10 - 42%
          1 x [cp=635; hp=42; stardust=1300] => 4/2/10 - 36%

worst case upgrade count: 5
```

You see theres a lot of possible options. The first Power-Up will rule out some them and let you end up with either 2 or 4 options left. However after that you will need up to 4 power ups to finally know the IVs

## Todo

Scratchpad-Like list of things which I may or may not add later
- Localisation, support for pokemon names in other languages
- Nice Frontend, probably as a simple webservice
- Refining, as in as in adding data after an powerup to see less results
