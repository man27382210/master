package tools.lucene;

import item.Patent;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.FieldInfo.IndexOptions;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

public class Lucene {
  private Directory index;

  public Directory getIndex() {
    return index;
  }
  
  public void load(List<Patent> list) throws IOException {
    StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_42);
    index = new RAMDirectory();
    IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_42, analyzer);
    IndexWriter w = new IndexWriter(index, config);

    for (Patent p : list) {
      String id = p.getString("patent_id");
      String content = p.getString("abstract") + p.getString("claims") + p.getString("description");
      addDoc(w, id, content);
    }
    w.close();
  }

  private void addDoc(IndexWriter w, String id, String content) throws IOException {
    FieldType fieldType = new FieldType();
    fieldType.setStoreTermVectors(true);
    fieldType.setStoreTermVectorPositions(true);
    fieldType.setIndexed(true);
    fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
    fieldType.setStored(true);

    Document doc = new Document();
    doc.add(new StringField("id", id, Field.Store.YES));
    doc.add(new Field("content", content, fieldType));
    w.addDocument(doc);
  }

}
