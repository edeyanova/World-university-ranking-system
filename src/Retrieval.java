import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;

public class Retrieval {

	private static void addDocument(IndexWriter indexWriter, String rank, String university, String rating)
			throws IOException {
		Document doc = new Document();
		doc.add(new StringField("rank", rank, Field.Store.YES));
		doc.add(new TextField("university", university, Field.Store.YES));
		doc.add(new StringField("rating", rating, Field.Store.YES));

		indexWriter.addDocument(doc);
	}

	public static void indexFile(String filePath, String indexDirectoryPath) throws IOException, URISyntaxException {
		Directory indexDir = FSDirectory.open(Paths.get(indexDirectoryPath));

		Analyzer analyzer = new StandardAnalyzer();

		IndexWriter writer = new IndexWriter(indexDir, new IndexWriterConfig());
		File file = new File(filePath);
		Scanner input = new Scanner(file);
		while (input.hasNext()) {
			String line = input.nextLine();
			int rankIndex = line.indexOf(" ");
			int ratingIndex = line.lastIndexOf(" ");
			String rank = line.substring(0, rankIndex);
			String university = line.substring(rankIndex + 1, ratingIndex);
			String rating = line.substring(ratingIndex + 1, line.length());

			addDocument(writer, rank, university, rating);
		}
		input.close();
		writer.close();
	}

	public static HashSet<String> search(String queryString, String indexDirectoryPath)
			throws IOException, ParseException, URISyntaxException {
		Directory indexDir = FSDirectory.open(Paths.get(indexDirectoryPath));
		Analyzer analyzer = new StandardAnalyzer();
		IndexReader reader = DirectoryReader.open(indexDir);
		IndexSearcher searcher = new IndexSearcher(reader);
		QueryParser parser = new QueryParser("university", analyzer);
		Query query = parser.parse(queryString);

		TopDocs results = searcher.search(query, 100000);
		ScoreDoc[] hits = results.scoreDocs;
		String result = null;
		HashSet<String> universities = new HashSet<>();
		for (int i = 0; i < hits.length; i++) {
			int docId = hits[i].doc;
			Document document = searcher.doc(docId);

			result = document.get("rank") + " " + document.get("university") + " " + document.get("rating");

			universities.add(result);
		}
		return universities;
	}
}
