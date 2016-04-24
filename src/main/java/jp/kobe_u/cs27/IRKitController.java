package jp.kobe_u.cs27;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import jp.kobe_u.cs27.util.MongoDBUtil;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.bson.Document;

import com.mongodb.client.FindIterable;
import com.mongodb.util.JSON;

/**
 * IRKitを操作するクラス
 * 
 * @author tktk
 *
 */
public class IRKitController {

	// IRKitのIP 外部ファイル化したい
	private final String IRKitIP = "192.168.3.103";
	private final String collectionName = "ir_signals";

	public String learn(String id) {
		String url = "http://" + IRKitIP + "/messages";
		// 前処理: IRKitに保存されているIR信号が空になるまでアクセス
		String result = httpGetRequest(url);
		while (!"".equals(result)) {
			result = httpGetRequest(url);
		}

		// 登録処理: 約3秒 x 10回アクセスして，結果が取得され次第次へ
		for (int i = 0; i < 10; i++) {
			result = httpGetRequest(url);
			// 空でなければDBに保存して，return
			if (!"".equals(result)) {
				System.out.println("comming");
				// DB保存処理
				Document filter = new Document().append("ir_id", id);
				Document set = new Document().append("$set", new Document(
						"ir_info", JSON.parse(result)));
				MongoDBUtil.getInstance()
						.updateOne(collectionName, filter, set);

				// API結果表示用
				Document resultDocument = MongoDBUtil.getInstance().findOne(
						collectionName, filter);
				return resultDocument.toJson();
			}
			// 3秒待つ
			try {
				System.out.println("waiting...");
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				return "{\"msg\":\"" + e.toString() + "\"}";
			}
		}
		return "{\"msg\":\"no signal comming\"}";
	}

	/**
	 * 指定したIDのIR信号をIRKitから送信する
	 * 
	 * @param id
	 * @return
	 */
	public boolean send(String id) {
		// MongoDBからIR信号を探す
		Document query = new Document("ir_id", id);
		try {
			Document irInfo = (Document) MongoDBUtil.getInstance()
					.findOne(collectionName, query).get("ir_info");
			String signalJSON = irInfo.toJson();

			String url = "http://" + IRKitIP + "/messages";
			httpPostRequest(url, signalJSON);

			return true;
		} 
		// IR信号が存在しない場合はfalse
		catch (NullPointerException e) {
			return false;
		}
	}
	
	/**
	 * 登録されているIR信号のリストを取得する
	 * @return
	 */
	public String list(){
		String json = "{\"ir_list\":[";
		FindIterable<Document> irList = MongoDBUtil.getInstance().find(collectionName);
		for(Document doc : irList){
			json = json.concat(doc.toJson()).concat(",");
		}
		json = json.substring(0, json.length()-1);
		return json + "]}";
	}

	/**
	 * 指定したURLにGETでアクセスを行う
	 *
	 * @param requestUrl
	 * @return 結果のbody
	 */
	private String httpGetRequest(String requestUrl) {
		HttpClient httpclient = HttpClients.createDefault();
		HttpGet httpget = new HttpGet(requestUrl);
		httpget.addHeader("X-Requested-With", "curl");

		// Execute and get the response.
		HttpResponse response;
		try {
			response = httpclient.execute(httpget);

			HttpEntity entity = response.getEntity();

			if (entity != null) {
				InputStream instream = entity.getContent();
				try {
					BufferedReader br = new BufferedReader(
							new InputStreamReader(instream));
					StringBuilder builder = new StringBuilder();
					String line;

					while ((line = br.readLine()) != null) {
						builder.append(line);
					}

					String result = builder.toString();
					br.close();
					return result;
				} finally {
					instream.close();
				}
			}
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 指定したURLにJSONをbodyに設定してPOSTでアクセスを行う
	 *
	 * @param requestUrl
	 * @param json
	 * @return
	 */
	private String httpPostRequest(String requestUrl, String json) {
		HttpClient httpclient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(requestUrl);
		httpPost.addHeader("X-Requested-With", "curl");
		try {
			httpPost.setEntity(new StringEntity(json));
		} catch (UnsupportedEncodingException e1) {
			return "";
		}

		// Execute and get the response.
		HttpResponse response;
		try {
			response = httpclient.execute(httpPost);

			HttpEntity entity = response.getEntity();

			if (entity != null) {
				InputStream instream = entity.getContent();
				try {
					BufferedReader br = new BufferedReader(
							new InputStreamReader(instream));
					StringBuilder builder = new StringBuilder();
					String line;

					while ((line = br.readLine()) != null) {
						builder.append(line);
					}

					String result = builder.toString();
					br.close();
					return result;
				} finally {
					instream.close();
				}
			}
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return "";
	}

}
