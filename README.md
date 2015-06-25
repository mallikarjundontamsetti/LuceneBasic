# LuceneBasic
lucene search using java 8 lambda

This example is taken from the lucene in action book and added few changes according to the lucene version 4.7. 
Used java 8 lambda's this is completely for learning purpuse. Suggestions on performance and logic are invited.

Palce few .txt files inside data folder. 
<h4>Problem in the code: </h4>
used addDocuments to add the documents to lucene so when you run the index class this will not check the existing documents, use updateDocuments to check the existing documents. And haven't added unique ID, to filter the doc info.
