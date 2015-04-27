# Frequently asked questions

### Why does my dictionary report have only one category in it?

Probably because when you ran the report you had just one category
selected in the interface.  The application used not to care what you
had selected, but now it does.  To get the old behavior back, select
the dictionary root before you run reports.

### The document text cannot be found?

Probably the orginal document has moved from where it was when you
added it to the project.  Remove the phantom document references and
re-add them to the project.

### Can I match multiple word with patterns?

Not yet.  Sorry.

### I can't figure out what encoding my documents are in

The easiest way to figure out what encoding a document is in is to
open it in a browser and play with the encoding menu (usually found
under 'View' or a similarly named menu) until it looks right.  Mac and
Unix users can also use the 'file' command at the shell prompt.
Everybody can preview and convert documents between encodings en masse
using
[Reencoder](http://sourceforge.net/projects/reencoder/).

### What happened to scores?

They went away.  It was always hard to provide sensible semantics for
them - should they be averaged within category or summed, or weighted
by document proportions?  Scores are now something you do to a
dictionary report after it's made.

