# The train home

That was a frustrating morning.  I'm going to test it with curl commands for now and come back to that once I have internet access and the ability to diagnose what the hell I'm doing wrong.

Let's give this a go again.

## Some time later...

Well that went by quite quickly.  After a false start with the json parsing library I got the actions format parsing nicely from the curl command line, and you can move the @ around.
If you do ```curl -dactions='[{"character":1,"x":3,"y":3}]'``` then the @ moves to location (3,3).
This pleases me no end of course, since I was  bit nervous about whether it would work or not.

Apparently, it didn't stop there though.  A day of leaving the code alone has mysteriously fixed my non-posting jquery bug, and my post is now working.
I get a weird error, because my formdata is in a weird format, but a quick ```JSON.stringify``` fixes that right up, and we can move the @ symbol from teh execute button.

That's it for tonight, a short one, but tomorrow I'll work on allowing a click to set target, move the @ upon execute, and possibly some monsters.
