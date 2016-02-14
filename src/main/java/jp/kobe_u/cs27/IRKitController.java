package jp.kobe_u.cs27;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import jp.kobe_u.cs27.util.MongoDBUtil;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.bson.Document;

import com.mongodb.util.JSON;

/**
 * IRKitを操作するクラス
 * @author tktk
 *
 */
public class IRKitController {
	
	// IRKitのIP 外部ファイル化したい
	private final String IRKitIP = "192.168.2.107";
	private final String collectionName = "ir_signals";
	
	public String learn(String id){
		String url = "http://" + IRKitIP + "/messages";
		// 前処理: IRKitに保存されているIR信号が空になるまでアクセス
		String result = httpRequest(url);
		while(!"".equals(result)){
			result = httpRequest(url);
		}
		
		// 登録処理: 約3秒 x 10回アクセスして，結果が取得され次第次へ
		for(int i=0;i<10;i++){
			result = httpRequest(url);
			// 空でなければDBに保存して，return
			if(!"".equals(result)){
				System.out.println("comming");
				// DB保存処理
				Document filter = new Document().append("ir_id", id);
				Document set = new Document().append("$set", new Document("ir_info", JSON.parse(result)));
				MongoDBUtil.getInstance().updateOne(collectionName, filter, set);
				
				// API結果表示用
				Document resultDocument = MongoDBUtil.getInstance().findOne(collectionName, filter);
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
	 * 指定したURLにGETでアクセスを行う
	 *
	 * @param requestUrl
	 * @param accessToken
	 * @return
	 */
	private String httpRequest(String requestUrl) {
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
	 * @param args
	 */
	public static void main(String[] args) {
		//MongoDBUtil.getInstance().insertOne("test", new Document("test", "test"));
		
		IRKitController c = new IRKitController();
		System.out.println(c.learn("aa"));

	}

}
