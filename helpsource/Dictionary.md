# Dictionaries

Yoshikoder projects can have an associated content analysis
dictionary.  A dictionary is a tree structure of categories each of
which may contain patterns.  Patterns are strings that can match
single words in a document.  Categories simply organize them.

## Working with categories and patterns

You can add, edit and remove categories and patterns using
**Dictionary>Add Category** and **Dictionary>Add Pattern**
**Dictionary>Edit Entry** and **Dictionary>Remove Entry**, or by using
the toolbar icons.  You can also move categories and patterns around
the dictionary by drag and drop.

## Pattern matching

Patterns may contain the wildcard character `*` which means 'zero or
more other letters'.  Hence `shout*` matches 'shout', 'shouting' and
'shouted'.  The star can appear in any part of the string.

Matching is *not* case sensitive, so 'Shout' and 'shout' both match
the pattern `shout`.

Although patterns cannot be duplicated within a category, wildcarding
may nevertheless allow double counting.  You can check for this by
using the highlighting and concordance functions.

## Adding many patterns

If you already have a list of patterns you need to import you can
select a category in the dictionary and use the text are offered by
**Dictionary>Add Patterns** to insert them all in one go.  This is
more efficient than using **Dictionary>Add Pattern**.  Each white
separated string you provide in the entry field will be treated like a
potential pattern and added, unless it duplicates an entry already in
the category. You can always remove the entries you don't want
afterwards.

## Opening and saving existing dictionaries

Dictionaries can be saved and loaded independently of projects using
**Dictionary>Open** and **Dictionary>Save As**.  When you open a new
dictionary it replaces the old one, so don't forget to save a copy.
**Dictionary>Open** supports two dictionary formats: native and VBPro:

The native Yoshikoder dictionary format is signalled by files ending
in '.ykd'.  Browsers sometimes also append '.xml' for some reason but
you can remove that.

The VBPro format is a a very simple flat dictionary specification
format which surrounds category names with '>>' and '<<' and lists
patterns one per line.  For example:

      >>Fruit<<
    pear
    tomato
    >>Vegetables<<
    carrot
    potato
    
represents a two category dictionary with four patterns.  Hierarchical
structure cannot be represented in this format.

VBPro is designed so that it is easy to construct dictionaries
manually in a text editor.
