SCALATRON (Hack The Tower version)- Learn Scala With Friends
https://github.com/hackthetower-london-scalatron
Based on source at https://github.com/scalatron/scalatron
This work is licensed under the Creative Commons Attribution 3.0 Unported License.


# READ ME

## About Scalatron

Scalatron is an educational resource for groups of programmers that want to learn more about
the Scala programming language or want to hone their Scala programming skills. It is based on
Scalatron BotWar, a competitive multi-player programming game in which coders pit bot programs
(written in Scala) against each other.

The documentation, tutorial and source code are intended as a community resource and are
in the public domain. Feel free to use, copy, and improve them!


## Quick Start

* clone the Scalatron distribution from https://github.com/hackthetower-london-scalatron/scalatron
* run `sbt dist` to create the distributable project
* in the `dist` folder, run `bin/startServer.sh`. NOTE: sometimes the simulator view looks like a blank screen, resize to redraw.
* this should automatically open a browser and point it to e.g. `http://localhost:8080`
* log in as `Administrator`, create a user account for yourself
* log in as that user, which will take you into a browser-based code editor
* click "Run in Sandbox" to build your bot and run it in a private sandbox game
* click "Publish into Tournament" to build your bot and publish it into the tournament
* once you know your way around, invite some friends for a bot coding tournament and have fun!


## Learning More

* browse into the `/Scalatron/docs/pdf/` directory
* here you will find a collection of useful documents, including the following:
    * `Game Rules`      -- describes the BotWar game state & dynamics
    * `Protocol`        -- describes how server and bots interact for the BotWar game
    * `Player Setup`    -- how set up your local working environment to build bots
    * `Server Setup`    -- how to configure the game server
    * `Tutorial`        -- how to code a bot in Scala


## For Developers

* go to https://github.com/hackthetower-london-scalatron to download the Scalatron source code
* you can also go to http://github.com/scalatron to download the original Scala 2.9 version
* check out developer documentation, in particular the API and the doc on pluggable games
* found a bug? Do a Pull-request :)

