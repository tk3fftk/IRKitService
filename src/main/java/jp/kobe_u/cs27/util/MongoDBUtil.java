package jp.kobe_u.cs27.util;

import org.bson.Document;

import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;

/**
 * Mongodbを利用するためのシングルトンUtilityクラス MongoDB 3.x & Driver 3.xに対応
 *
 * @author tktk
 */
public class MongoDBUtil {
	private static MongoClient mongoClient;
	private static MongoDatabase db;

	// mongoserv
	private static final String dbAddr = "192.168.3.102";
	private static final int dbPort = 27017;
	private static final String dbName = "irkit";

	// for singleton
	private static MongoDBUtil singleton = new MongoDBUtil();

	public static MongoDBUtil getInstance() {
		return singleton;
	}

	// コンストラクタ
	private MongoDBUtil() {
		try {
			mongoClient = new MongoClient(dbAddr, dbPort);
			db = mongoClient.getDatabase(dbName);
		} catch (MongoException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 指定したコレクションを取得する
	 * 
	 * @param collectionName
	 * @return
	 */
	public MongoCollection<Document> getCollection(String collectionName) {
		return db.getCollection(collectionName);
	}

	/**
	 * 指定したコレクションにドキュメントを一つ追加する
	 * 
	 * @param collectionName
	 * @param doc
	 */
	public void insertOne(String collectionName, Document doc) {
		MongoCollection<Document> coll = getCollection(collectionName);
		coll.insertOne(doc);
	}

	/**
	 * 指定したコレクションから指定したクエリのドキュメントを一つ取得する
	 * 
	 * @param collectionName
	 * @param doc
	 * @return
	 */
	public Document findOne(String collectionName, Document doc) {
		MongoCollection<Document> coll = getCollection(collectionName);
		return coll.find(doc).first();
	}

	/**
	 * 指定したコレクションのドキュメントを全て取得する
	 * 
	 * @param collectionName
	 * @return
	 */
	public FindIterable<Document> find(String collectionName) {
		MongoCollection<Document> coll = getCollection(collectionName);
		return coll.find();
	}

	/**
	 * 指定したコレクションのドキュメントを一つ更新 なければ追加(upsert)
	 * 
	 * @param collectionName
	 * @param filter
	 * @param set
	 * @return
	 */
	public UpdateResult updateOne(String collectionName, Document filter,
			Document set) {
		MongoCollection<Document> coll = getCollection(collectionName);
		UpdateOptions options = new UpdateOptions().upsert(true);
		return coll.updateOne(filter, set, options);
	}

	/***
	 * 全コレクションを削除する
	 */
	public void drop() {
		for (String collectionName : db.listCollectionNames()) {
			drop(collectionName);
		}
	}

	/***
	 * 指定コレクションを削除する
	 *
	 * @param collectionName
	 */
	public void drop(String collectionName) {
		System.out.println("dropping [" + collectionName + "]");
		DBCollection coll = (DBCollection) db.getCollection(collectionName);
		coll.drop();
	}
}
