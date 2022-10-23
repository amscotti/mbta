# Broad DSP Engineering Interview Take-Home

Take home assignment using the MBTA API to answer a series of questions and to develop a CLI application that lets the user enter any two stops on the subway routes and plot the travel.

## Requirements
This application requires Java and Clojure.

### Java
Tested with version 17 and 11 using GraalVM CE 22.2.0 installed with [sdkman](https://sdkman.io/).

### Clojure
Build using Clojure 1.11 installed using [Install Clojure](https://clojure.org/guides/install_clojure) on clojure.org, for myself, I used Homebrew steps.


## Running
There are different entry points into the application which uses `clj` command along with the `deps.edn`. There's also an option of using Docker for the CLI application.

### Question 1
Running `clj -M:question1` will output the answer to question 1 from `question1.clj`.

```
❯ clj -M:question1
Question 1:
 Red Line, Mattapan Trolley, Orange Line, Green Line B, Green Line C, Green Line D, Green Line E, Blue Line
```

### Question 2
Running `clj -M:question2` will output the answer to question 2 from `question2.clj`.

```
❯ clj -M:question2  
Question 2:

1. The name of the subway route with the most stops as well as a count of its stops.
Name: Green-D Stops: 25

2. The name of the subway route with the fewest stops as well as a count of its stops.
Name: Mattapan Stops: 8

3. A list of the stops that connect two or more subway routes along with the relevant route names for
each of those stops.
Stop: Copley on Green-E, Green-D, Green-C, Green-B routes
Stop: North Station on Orange, Green-E, Green-D routes
Stop: Haymarket on Orange, Green-E, Green-D routes
Stop: Downtown Crossing on Orange, Red routes
Stop: Boylston on Green-E, Green-D, Green-C, Green-B routes
Stop: Ashmont on Mattapan, Red routes
Stop: Lechmere on Green-E, Green-D routes
Stop: Science Park/West End on Green-E, Green-D routes
Stop: Government Center on Blue, Green-E, Green-D, Green-C, Green-B routes
Stop: Kenmore on Green-D, Green-C, Green-B routes
Stop: State on Blue, Orange routes
Stop: Hynes Convention Center on Green-D, Green-C, Green-B routes
Stop: Park Street on Red, Green-E, Green-D, Green-C, Green-B routes
Stop: Arlington on Green-E, Green-D, Green-C, Green-B routes
```

### Question 3
Running `clj -M:question3` will output examples for an answer to question 3 `question3.clj`.

```
❯ clj -M:question3  
Question 3: Examples -

Davis to Kendall/MIT
Path: Davis->Porter->Harvard->Central->Kendall/MIT
Routes: Red

Ashmont to Arlington
Path: Ashmont->Shawmut->Fields Corner->Savin Hill->JFK/UMass->Andrew->Broadway->South Station->Downtown Crossing->Park Street->Boylston->Arlington
Routes: Red, Green-E or Green-D or Green-C or Green-B

Butler to Fenway
Path: Butler->Cedar Grove->Ashmont->Shawmut->Fields Corner->Savin Hill->JFK/UMass->Andrew->Broadway->South Station->Downtown Crossing->Park Street->Boylston->Arlington->Copley->Hynes Convention Center->Kenmore->Fenway
Routes: Mattapan, Red, Green-E or Green-D or Green-C or Green-B, Green-D or Green-C or Green-B, Green-D
```
### Questions
`clj -M:questions` will output all the previous answers in one command using `questions.clj`.

### CLI Application

#### With clj
`clj -M:run` will run the CLI application found in `core.clj`.

Here is the output of the `--help`,

```
❯ clj -M:run --help
  -s, --start START              Starting subway stop
  -d, --destination DESTINATION  Destination subway stop
  -h, --help
```

An example of running a query,

```
❯ clj -M:run -s "Back Bay" -d "Haymarket"
Back Bay to Haymarket
Path: Back Bay->Tufts Medical Center->Chinatown->Downtown Crossing->State->Haymarket
Routes: Orange
```

#### Docker
You can build the image using `docker build . -t mbta`. This will create a container that has a native image created from GraalVM.

There's also a pre-generated docker image at `ghcr.io/amscotti/mbta:main` created from a Github action.

```
❯ docker run ghcr.io/amscotti/mbta:main -s "Back Bay" -d "Haymarket"
Back Bay to Haymarket
Path: Back Bay->Tufts Medical Center->Chinatown->Downtown Crossing->State->Haymarket
Routes: Orange
```

### Unit Test
Unit test can be run by `clj -M:test` and all the test can be found under the `test` folder for which question.

# Design Decisions

## Why Clojure

I've been using Clojure for years on my personal projects and for other code challenges like [Code Wars](https://www.codewars.com) and [Advent of Code](https://github.com/amscotti/aoc_2021). I find that Clojure (and functional programming) has a lot of nice building tools for working with data but overall the experience of [REPL development](https://clojure.org/guides/repl/introduction) makes working on any data  problem a lot easier as I can quickly experiment with the data without saving and rerunning my program. In this case, I was able to call the API once to get the data, and continue to experiment with it in the REPL until I could answer the questions.

I can easily continue talking about Clojure all day, but as this README is already long so, I will stop here. :) 

## Third-party dependencies
* [clj-http](https://github.com/dakrone/clj-http) - HTTP client libraries, similar to Requests for Python.
* [cheshire](https://github.com/dakrone/cheshire) - JSON decoder used by clj-http
* [tools.cli](https://github.com/clojure/tools.cli) - Command line argument parser, used in CLI application.
* [Kaocha](https://github.com/lambdaisland/kaocha) - Test runner, test only dependency
* [tools.build](https://github.com/clojure/tools.build) - Tools for building project, build only dependency

## Structure
* `mbta.clj` - Code for communicating with the MBTA API, used in all questions and `core.clj`
* `core.clj` - CLI application, using functions from `question3.clj`. Application entry point
* `questions.clj` - Entry point that runs code from all the questions
* `question1.clj` - Code associated with question 1
* `question2.clj` - Code associated with question 2
* `question3.clj` - Code associated with question 3, used by `core.clj` in CLI application

# Questions

When working on all the questions, one of the things I kept in mind was the "Hint" from the Instructions, my interpretation of this was to rely more on the data than any of my limited notions about the MBTA. Besides the times I have worked in Boston I've not taken the subway that much, so doing this project I've learned some interesting things about the MBTA.

## Question 1

Looking at the full data provided by the `/routes` end point, I didn't see any better way to filter the data than by "type". This seem to be the same as using `/routes?filter[type]=0,1`, which I picked to use. My thought behind this was there would be less data going over the wire along with the need of having less data in memory just to filter most of it away.

The only reason I could think of to use the full data from just the `/routes` end point is if there was a fear that the API's query language which change at some point and we be unable to filter from the API level. Will be unaffected by this change if we where just using the `/routes` end point.

## Question 2

For this part, I called the `/stops` endpoint using the filtering by route on the API. This gave me all the stops for a route, and allowed me to count the stops to answer part 1, and 2. For part 3, I used Sets to find the stops that intersected with multiple routes.

## Question 3

To answer this question required some additional data structures. Because the nature of the data, graph seem to be appropriate. Out of the graph data structures I know, I picked Adjacency list over Adjacency Matrix because,
* The graph seems to be sparse, and Adjacency List would take less space
* As I'm looping over data for many stops it's faster to add a new node in Adjacency List
* Visually it was easier for me to confirm the data by simply looking at a list
 
I used depth-first seach to be able to find the all paths through the graph and picked the one with a few steps.

To fully answer the question I also need to know what routes the stops were on, for this I created a lookup table using a Map with the stops being the key, and the value a list of routes. This let me link the stops to the routes. 