## 0.6.5

We're on Github but otherwise things look very much as before, except 
we now look nice on Retina displays and require Java 1.8 to run.

## 0.6.4

There are lots of small changes in this release.  If you dislike them, you can use the 
previous version (all the file formats are the same).

Here are some, errm, highlights: 

 1. Dictionary reports are either for the current document wich will
    show up in a window, or for the currently selected documents which
    will be constructed and saved directly to file.  You may choose
    the file format (Excel or UTF-8 encoded CSV).  This latter
    replaces the export function on multiple document reports.

 2. Dictionary reports recognize when you have a category or
    subcategory selected and construct only the report for that entry
    and its children.  If a pattern is selected you get a report for
    the containing category.  So be careful what's highlighted in the
    dictionary when you run a report.

 3. Document reports follow the same format: either the currently
    selected document has its words counted or a complete set of word
    counts gets saved to file.  This only happens in UTF-8 CSV format
    becuase Excel has difficulties with large files.  Also, documents
    are rows, not columns (like in dictionary reports).  For counting
    words in lots of documents, you should use a dedicated word
    counter like JFreq that can store them efficiently.  CSV is
    incredibly redundant - you shouldn't run out of memory but you'll
    make very big files.

 4. If multiple documents are selected for a concordance, the
    concordance for each of them is created in the window, with a
    blank space separating the output for different documents.

 5. You can alter the locale, encoding, or title of documents after
    they are in the project using the Edit Document function.  This is
    a bit experimental.

 6. The Yoshikoder now has default settings for locale and encoding in
    the Preferences.  These control what assumptions are made about
    documents that are added.  Also, window width for concordances is
    remembered between projects.

 7. You can still open dictionaries in VBPRO format - just suffix the
    file .vbpro and choose the this format in the Dictionary Open
    function.

 8. You can add large numbers of words to a category at once using a
    palette arrangement.  Just paste your words into it and they'll
    all turn up as patterns in the category you have selected.

Some things are deprecated:

 1. Scores are no longer marked and are going away.  If you need them
    you can apply them to the output of dictionary reports.  The
    semantics for them were, frankly, never entirely clear.

Some things are not quite there yet:

 1. Help pages need an overhaul.  They are incomplete.
