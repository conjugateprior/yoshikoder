# Documents

Yoshikoder deals with plain text documents.  If you have documents in
other formats, e.g. Word, or PDF you should convert them first.  (See
'Converting to plain text' below).

When you add documents to a project, e.g. using **Documents>Add
Documents** their names appear in the documents list.  Note that when
you add a document to a project you are only adding a reference to it.
This has two implications:

First, if you close the application and then move or delete the
original document, Yoshikoder will not be able to find it again when
it re-opens.  If that happens you should remove the dangling reference
from the document list and re-add the document from its new location.

Second, when you remove a document from the Yoshikoder you do not
affect the original file.  It is still there in your filesystem.

To remove documents from a project, selectthem and use
**Documents>Remove Documents**

You can also export a copy of any document in one of two Unicode
formats using the **Documents>Export** menu item.

Documents have a location in the file system; a title, by which they
are known within a project; an encoding, which records what character
set the document file uses; and a locale, which indicates what
language the document is in.  These are set to default values
specified in the Preferences when documents are added, but can be
altered later using **Documents>Edit Document**.

Note that altering the file encoding is not recommended unless the
current one has generated nonsense in the text window.  Altering the
locale will have little if any effect on tokenisation in Western
languages.

## Converting to plain text

Most applications will allow you to 'save as' text.  If you have a lot
of documents it may be easier to use a tool to convert them in one go.
The [YKConverter](http://www.williamlowe.net/software/#ykconverter)
might be useful for this.

If, after conversion to plain text you find strange looking characters
in your documents when you add them this is a sign that Yoshikoder's
expected encoding (sometimes called 'character set') is not same as
the document's encoding.  If this happens you should determine the
documents' encoding, set the Yoshikoder's default encoding
appropriately in the Preferences, and re-add the documents.

## Batch conversion

Alternatively you can convert the documents into the Yoshikoder's
default encoding before adding them (provided you are confident that
it will be able to represent all the characters in the documents) The
[Reencoder](http://sourceforge.net/projects/reencoder/) might be
helpful for this.
