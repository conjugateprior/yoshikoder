# Dictionaries

Yoshikoder projects can have an associated content analysis
dictionary. A dictionary is a tree structure of categories each of
which may contain patterns. Patterns are strings that can match single
words in a document. Categories simply organize them in a hierarchical way.

## Working with categories and patterns

You can add, edit and remove categories and patterns using
**Dictionary>Add Category** and **Dictionary>Add Pattern**
**Dictionary>Edit Entry** and **Dictionary>Remove Entry**, or by using
the toolbar icons.  You can also move categories and patterns around
the dictionary by drag and drop.  

If you have a lot of patterns to add, use the **Dictionary>Add
Patterns** function to add them all at once.  You'll get a text window
into which you can paste as many words as you like.  Whatever you
paste will be split up and added as new patterns within the category
you have selected.

## Pattern matching

Patterns may contain the wildcard character `*` which means 'zero or
more other letters'. Hence `shout*` matches 'shout',
'shouting', and 'shouted'. The star can appear in any part of the
string or more than once.

Matching is *not* case sensitive, so 'Shout' and 'shout are both matched by the pattern `shout`.

Although patterns cannot be duplicated within a category, wildcarding
may nevertheless allow double counting. You can check for this by
using the highlighting and concordance functions.

## Opening and saving dictionaries

Dictionaries can be saved and loaded independently of projects using **Dictionary>Open** and **Dictionary>Save As**. When you open a new dictionary it replaces the old one, so don't forget to save a copy if you want to use it again. 

**Dictionary>Open** supports two dictionary formats: the native
 Yoshikoder format and VBPro.

The native Yoshikoder dictionary format appears in files ending in
'.ykd'. Web browsers sometimes also append '.xml' when they download
such files but you can just remove that from the file's name.

VBPro format is a a very simple flat dictionary specification which
surrounds category names with '>>' and '<<' and lists
patterns one per line. For example:

    >>Fruit<<
    pear
    tomato
    >>Vegetables<<
    carrot
    potato

represents a two category dictionary with four patterns.  Hierarchical
structure cannot be represented in this format.

VBPro format was designed so that it is easy to construct dictionaries
 manually in a text editor.

