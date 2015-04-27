pandoc -t html -f markdown --template template.html FAQ.md > ../onlinehelp/FAQ.html
pandoc -t html -f markdown --template template.html Dictionary.md > ../onlinehelp/Dictionary.html
pandoc -t html -f markdown --template template.html Documents.md > ../onlinehelp/Documents.html
pandoc -t html -f markdown --template template.html Projects.md > ../onlinehelp/Projects.html
pandoc -t html -f markdown --template template.html HighlightingandConcordances.md > ../onlinehelp/HighlightingandConcordances.html
pandoc -t html -f markdown --template template.html Reports.md > ../onlinehelp/Reports.html
pandoc -t html -f markdown --template template.html -V entrypage index.md > ../onlinehelp/index.html
pandoc -t html -f markdown --template template.html Tokenization.md > ../onlinehelp/Tokenization.html
