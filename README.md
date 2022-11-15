![banner](banner.png)
# ⚽▶️ Laterball
This is the source for the Laterball football match watchability rating generator running at [laterball.com](http://laterball.com)

## What is Laterball?

I like to watch replays of football  games on demand, but hate having the score spoiled for me, so I always  wanted to know which game is worth spending time watching without  knowing the score.

So I built [laterball.com ↠](http://laterball.com)

It calculates watchability ratings for the latest EPL and Champions League games, without spoilers! 

## How does it work?

The Ktor server returns static HTML to the configured routes (one for each supported league and an about page). When a request is received, the server calculates the ratings for all matches within the past 7 days based on match statistics, events and odds. Match data is fetched from [API-Football](https://www.api-football.com/) and cached to minimise API requests. 

## Twitter bot

A Twitter bot tweets the ratings for 4- and 5-star games (maximum one per kickoff time, maximum one every 4 hours) to [@laterball](https://twitter.com/laterball) 

## Hosting and deployment

This repository contains a GitHub action for deploying to Google App Engine, provided the appropriate secrets are set in the repository. The required secrets are defined in the `application.conf` file. The action runs every time a new tag is pushed to main.

## Future work

### Better "Where to watch" links

Currently, clicking on the ↠ icon next to a match just Googles where to watch the match streaming on demand. Ideally, this would take the user to the official streaming provider for their local region.

### User ratings

Crowdsourcing ratings could improve rating accuracy, and could be weighted against the statistically-determined ratings

### Algorithm improvements

Accounting for:

- xG
- Relative league position/importance of match
- Timing of goals (late winner etc)

# Hosting
Laterball is currently hosted on a single Linode instance.
It makes use of an in-memory cache to reduce DB roundtrips, and a Mongo database to persist data between restarts.
If the application ever needs to support horizontal scaling, a more robust centralised caching mechanism would be required.
