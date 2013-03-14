# Day 03 - Evening - How productive can we be?

Two train journeys in a row have been pretty good for me, so it feels like this is going well.

This time my main plan is to add monsters to the map.

Before we go there, let me detail what I'd like to achieve and sort out when it'll happen.

One of the things I'd like to try in this roguelike prototype is find out whether a roguelike can support squad behaviour.
I'm sure that those of you who remember Space Hulk and Space Marine and Space Quest kind of games were expecting this,
but for those of you who don't - Suprise!

That means that I've got to work out and prioritise the technical tasks so that I get a bit of time to implement squad
order mechanics.  That's the main reason that I've done the odd actions thing in javascript, I don't want one time unit
to pass for each command, I want to issue commands to multiple members of the squad and then hit execute and have
everything happen at the same time.

However, in order to make that even slightly interesting, we need to have something to fight.

Therefore my main remaining items on my todo list are as follows.

* Monsters exist and move around
* Basic shooting combat system for players, hand to hand for monsters
* Add a second squad member, allow for multiple orders then execute mechanics.

We are already at wednesday night, I've got now, thursday morning, thursday evening, and then I'm at a conference.
I might be able to manage some work friday morning on the train, but it'll be stupidly early.
I'm at the conference friday and saturday, so I'm unlikely to get much done those days, maybe a bit of tidying
but probbaly not a lot else.

therefore I need to achieve those three things in basically three journeys, plus a little bit of debugging time.

Shit.

I guess we'll get cracking on monsters.

## Monsters, Random Placement and AI

So we can already model a number of mobile creatures, our player is one, our monsters can be more.
We can probably fudge the algorithm a bit, but my plan is generate a random number of monsters, and deposit them at
random throughout the map.

That bit is simple, we'll create a variable per monster, so we can track the x and y coordinates, and for now we'll skip
visibility issues, I can handwave that away as you've got futuristic scanners or something.

What we'll want is for the monsters to move upon each update.

My idea for an algorithm for now is to implement a random walk.

    for each monster
        pick a random valid direction
        Move that way


Pretty simple huh?

Of course that will mean that you'll be able to hunt down the aliens pretty easily, since they'll never engage you, but
on the other hand I can probably do that in a few minutes of work.

If we were to improve the AI, I'd have some strategies, and have each mobile keep a list of strategies with priority.
Each strategy has three options, return a new strategy, return a location to move to, yield to the next strategy.
So we'd have strategies like:

* Goal Seeking: Plot direct line to goal, move one step towards it. (Use A* to find the path to follow).
  Once at the goal yield until another goal is set
* Hunting: Scan current room for exits, set a random exit as the goal
* Chasing: Record the current location of the player, if we can see him, plot goal to her current location.
* Running Away: If I'm low on health, find nearest exit and set it as the goal.

I think a few simple strategies could build an interesting AI for creatures.

Anyway, onto the coding

## That went well

Now we have monsters, distributed across the map, and a simple run AI method that moves the monsters around randomly.

Tomorrow we fight!

[Seven-Day-Roguelike](http://github.com/bruntonspall/seven-day-roguelike) is a scala and appengine [7-day-roguelike](http://7drl.org) project by [bruntonspall](http://www.brunton-spall.co.uk)


