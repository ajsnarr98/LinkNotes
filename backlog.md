# TODO:

## Refactors
-

## Backend TODO
- See/update google OAuth consent screen credentials at https://console.developers.google.com/apis/credentials
- (done) enable google sign-in in firebase
- (done) add sha1 fingerprint for signing certificate to project in firebase
- (done) add web application OAuth 2 client id as a string (R.string.server_client_id in donottranslate.xml)
         to the app (see https://developers.google.com/identity/sign-in/android/start-integrating?authuser=2#get_your_backend_servers_oauth_20_client_id)
- Update firestore permissions

## Features
- make default profile image a square
- sort entries in some fashion (maybe most recent, with special entries, like pics, at top)
- better delete button
- display loading indicator in note screen when loading notes from firebase
- allow swipe to top of notes activity to refresh notes shown (in case of remote changes to collection)
- advanced searches
- add search functionality to add tags screen
- add cool animation for moving tags back and forth in add tags dialog
- add ability to delete/rename tags in general somehow
- when adding tags, have confirmation dialog that gives suggestions if it looks like there was a typo
- link to note/entry
- add markdown editing helpers for lists / links / images / headers
- update bold / italic / strike-through markers to unmark/mark with respect to whitespace
- remove markdown helper button for underline (will not support)
- link entries based on note/entry type (ex. tag <-> classes <-> people)
- set default values for a new note on save
- make viewnote screen look nicer
- prompt to save in edit mode on back button hit, if changes were made
- add images to entries
- add default special entry types, like a value bar
- add ability to add custom templates for new notes to start out with?
- add real editnote screen/boxes
- add confirmation on save editnote when you try to add new notetype
- add default entries for pictures and the like
- implement undo button when editing notes
- add custom recyclerview-like chipgroup

## Bugs
- changing focus between views while soft keyboard is up does not update softkeyboard listener
- editnote saving does not always update entry type?
