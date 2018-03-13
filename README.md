# TODO

This list is a mix of features that will be implemented in the app, and some desirable things that might be implemented on the web server, these are denoted _in italics._
Features in essentials are... essential. This will get us good marks but it isn't an excuse to stop there. If we can do more we totally should and thats why I've detailed some extensions here.

## Essentials

### Queuing
- Users can join a queue by selecting a requested time to play and then tapping any Pi.
  - _The web server should respond by by making the Pi flash/buzz_
- When the user is in a queue, the main page of the app should be updated to show:
  - Their current position **or** the time they requested to play.
  - A current estimated wait time.
  - A way to allow them to edit their queue. The user should be allowed to remove themselves from the queue or modify the time they wish to play.
- Messages sent from the server via Firebase Cloud Messaging need to be intercepted and read. The queue position and estimated time should be updated according to this.
- A notification should be sent to the user if their table is ready, telling them which one to go to.
- When the user taps the table a second time, the main page should be updated to include a button to end their game.
  - _When pressed, the web server will be notified that the snooker game has finished_
  - If the user doesn't tap the table within a reasonable time frame, notify them that their game has been forfeited.


## Extensions
### Registration and Login
- Allow users to reset their password
- _Allow users to sign up with an email to reset their passwords_

### Queuing
- When requesting a table, players should be informed of the current price for the table (& if surge pricing is in effect).
- A notification should be sent to the user if their table will be ready soon.
- Payments for the game should be taken by the app when the user taps the table for the second time to start their game.

### Playing
- A notification should be sent to the user if their game has been going on for a long time

### Venues
- Users should be able to favourite a venue. _Ideally this would be stored in the web server_
- There should be a section in the app where users can see all of their favourited venues, with information in card format for each one. 
  - Selecting one of these venues will launch the detailed venue page.


## Done
### Registration and Login
- Allow login as a business / recognise when a business has logged in.
  - Display a 'Add Machine' button in the navigation drawer that allows the user to add a new table at an establishment.
- When logging in, users should update the database with their new firebase ID if applicable. 
- Detect changes to firebase token during runtime.
- Pressing 'back' whilst on the main screen should not take you to the login screen.
- The login screen shouldn't be shown every time on a cold start. The app should remember if you are logged in.

### Venues
- Users should be able to see a map of local venues, plotted using coordinates stored in the web server for all venues.
  - _The web server should allow requests to be made to see all venues within a specific radius of the users current location_
- Clicking on a map pin will open a detailed page for each venue, informing the user about what types of machine they have available, for example.
- Using Google's Places API, we can link venues to their google maps pages, allowing us to show opening hours and more.
