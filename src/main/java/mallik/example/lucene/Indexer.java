package mallik.example.lucene;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.function.Consumer;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Indexer {

	private static final Logger LOGGER = LoggerFactory.getLogger(Indexer.class);

	private IndexWriterConfig config;
	private StandardAnalyzer analyzer;
	private IndexWriter indexWriter;

	private final static File INDEXDIR = new File(Indexer.class.getResource("index").getPath());
	private final static File DATADIR = new File(Indexer.class.getResource("data").getPath());

	public Indexer() throws IOException {
		analyzer = new StandardAnalyzer(Version.LUCENE_47);
		config = new IndexWriterConfig(Version.LUCENE_47, analyzer);
		config.setOpenMode(OpenMode.CREATE_OR_APPEND);
		indexWriter = new IndexWriter(FSDirectory.open(INDEXDIR), config);
	}

	public static void main(String[] args) throws IOException {

		Indexer indexer = new Indexer();

		long start = new Date().getTime();
		int numIndexed = indexer.index(FSDirectory.open(DATADIR));
		long end = new Date().getTime();

		indexer.closeIndexWriter();
		LOGGER.info("Indexing " + numIndexed + " files took " + (end - start) + " milliseconds");
	}
	
	private int index(FSDirectory dataDir) throws IOException {

		Arrays.stream(dataDir.getDirectory().listFiles((file, name) -> (!file.isDirectory() || name.endsWith(".txt"))))
		// .filter(file -> file.getName().endsWith(".txt")) we can do this but
		// by using above improves performance.
				.forEach(unchecked(this::indexFile));
		return indexWriter.numDocs();
	}

	private void indexFile(File file) throws IOException {
		Document doc = getDocumnet(file);
		indexWriter.addDocument(doc);
	}

	private Document getDocumnet(File file) throws IOException {
		Document doc = new Document();

		try {
			doc.add(new TextField("contents", new FileReader(file)));
		} catch (FileNotFoundException e) {
		}
		doc.add(new StringField("filename", file.getName(), Field.Store.YES));
		doc.add(new StringField("filepath", file.getCanonicalPath().toString(), Field.Store.YES));
		return doc;
	}

	private void closeIndexWriter() {
		try {
			indexWriter.close();
		} catch (IOException e) {
		}
	}

	/**
	 * Java 8 doesn't support throwing checked exceptions inside foreach or
	 * any intermediate/ terminal operators. Please refer {@link UtilException} class for all exceptions.
	 * 
	 * @param consumer
	 * @return
	 */
	private <T> Consumer<T> unchecked(CheckedConsumer<T> consumer) {
		return t -> {
			try {
				consumer.accept(t);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		};
	}

	@FunctionalInterface
	private interface CheckedConsumer<T> {
		void accept(T t) throws Exception;
	}

}
