package mallik.example.lucene;

import static mallik.example.lucene.UtilException.rethrowFunction;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Searcher {
	private Logger log = LoggerFactory.getLogger(Searcher.class);

	public static void main(String[] args) throws IOException, ParseException {
		new Searcher().search(Searcher.class.getResource("index").getPath(), "avatar");

	}

	private void search(String indexDir, String q) throws IOException, ParseException {
		IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(indexDir)));

		IndexSearcher indexSearcher = new IndexSearcher(reader);
		QueryParser parser = new QueryParser(Version.LUCENE_47, "contents", new StandardAnalyzer(Version.LUCENE_47));
		Query query = parser.parse(q);

		TopDocs hits = indexSearcher.search(query, 10);

		Arrays.stream(hits.scoreDocs).map(rethrowFunction(scoreDoc -> indexSearcher.doc(scoreDoc.doc))).forEach(doc -> {
			log.info(doc.get("filepath"));
			log.info(doc.get("filename"));
		});
		
		reader.close();
	}

}
