# TODO:

## Refactors
-

## Features
- make default profile image a square
- sort entries in some fashion (maybe most recent, with special entries, like pics, at top)
- better delete button
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
- add back navigation in toolbar
- add real editnote screen/boxes
- add confirmation on save editnote when you try to add new notetype
- add default entries for pictures and the like
- implement undo button when editing notes
- add custom recyclerview-like chipgroup
- add code obfuscation
- add security in firestore
- add separate account support in firestore

## Bugs
- deleting an entry does not visibly update the list of entries
- changing focus between views while soft keyboard is up does not update softkeyboard listener
- editnote saving does not always update entry type?
- cannot click tag in add tag dialog
- unable to remove tags in ui
