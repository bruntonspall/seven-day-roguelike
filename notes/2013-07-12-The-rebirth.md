= 2013-07-12 - The rebirth

So the #7drl team were kind enough to review my prototype as a full game, and the things they liked pretty much blew me away.  I got points for innovation for doing it squad like and doing a scifi themed roguelike.  Obviously the tech was pretty poor, but it inspired me to think about whether I want to bring this project back.

In the meantime I've changed jobs, and still have plenty of commuting time, so I think I'm going to make a stab at bringing the game from a prototype to an initial first release.

In order to feel confident that I ahve a full game, I need to break down some of the targets for what I want to happen, so here's my wishlist for version 1.0

== Must Haves

* Game ending conditions
* Save and reload capability
* Monsters with basic AI (hunting and attacking you)
* Usable UI

== Wants

* Equipment and inventory management
* Random world generation
* AI with walks, clustering and other features
* Better models for shooting, armor etc

There are also a bunch of technology changes and bugs from the prototype that I'd like to get to grips with as well

* Flood fill vision maps
* clientside code rewrite
* keyboard support
* AI
* The world update code

My rough planning is to attempt to dedicate around 4 hours per week (of my 10 hours commuting time) to the project, and at a rough estimate I'd say that there's at least 40 - 80 hours of work left, so I suspect 10-20 weeks time.  That gives me a rough deadline of christmas to have a first playable version, that cna be completed and has the structure necessary to support all the features I want.

The only nagging question in my mind is whether my entire technology stack is wrong.  I built this as a WebApp on AppEngine because I'm very familiar with those technologies as compared to an ascii curses interface.  But web comms is not really the right model for a Roguelike.  Should I change it?
The problem is that changing it will take a non-significant amount of time, but not changing it might cause longer delays.
For now I'm going to focus on backend changes and not worry about the frontend changes, except to avoid preclusing them.

My final thoughts are about the User Interface for controlling a squad.  One of the criticisms in the video review of the game is that having to click on each person, click their target location, then click done means 5 clicks per turn.  If we added 2 or 3 more squad members, it would become tiresome very quickly, especially when moving down an empty corridor, or trying to come back from the end back to the evac pod.
There are a few ideas I've got that we could try:

* Auto select squad members, so you don't have to click a squad member.  So when the turn starts, person a would be highlighted, and you'd give his order, immedietely, person b gets highlighted and you are giving them orders.  You could still click another squadmember to go back to them and change their order, and you'd probably want to have an execute button or action to say you are finished.
* Control only one squad member.  This is a massive change, originally I wanted to control a squad directly, but we could make it that you control the squad captain.  You could select orders like the squad formation, but the team members have an AI that decides how to execute the move you want.  This is harder to implement, because the AI would be harder, and it would remove some of the control that the user feels.

Are there any other ideas that I haven't thought of?