# Tokenization 

You can add your own tokenizers for particular languages using the
Tokenizers tab in the Preferences.  If you want to build your *own*
tokenizer, read on.

## Interfaces

Tokenizers are java classes that implement the interface 'Tokenizer'

      public interface Tokenizer {
        public TokenList getTokens(String txt) throws TokenizationException;
        public Locale[] getLocales();
    }
   
You can compile against the Yoshikoder codebase from sourceforge.
Here 'TokenList' trivially extends 'java.util.List'.  The 'Token'
interface is

      public interface Token {
        public String getText();
        public int getStart();
        public int getEnd();
    }

A 'Token' has some text, the token itself, and a span of
characters. In a given document if a token spans the third through
fifth character, then 'getStart' should return 2 (counting from 0),
and 'getEnd' should return 5.

## Implementations

The jar file is available that provides these interfaces and some
default implementations: 'TokenImpl', 'TokenListImpl', and
'TokenizationException'.  You should link to this jar when you compile
your code and include it in deployments.

## Deployment

Tokenizers should be deployed as Jar files. There must be a properties
file at the top level of the Jar called 'tokenizer.properties'. The
contents must include 'name' - the short name of the tokenizer,
'classname' - the fully qualified name of the 'Tokenizer'
implementation, and 'locales' - a space separated list of java
locales, specified in the same manner as the 'toString' method on
'java.util.Locale'.  A 'description' is optional.  A sample
'tokenizer.properties' file is shown below:

      name=Simp. Chinese
    description=Simplified Chinese Tokenizer
    # Specify [lang] as well as [lang]_[country] locales where necessary
    locales=zh_CN zh
    classname=com.mandarintools.SimplifiedChineseTokenizer
    