package init;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import org.bson.Document;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.CreateCollectionOptions;

import connector.MongoDB;

/**
 *  Design12306/init/InitMongo.java
 *  2016年11月11日 下午10:09:11
 *  @Author cxworks
 *
 */

public class InitMongo {
	private MongoDB mongoDB;
	
	public InitMongo() {
		mongoDB=new MongoDB();
	}
	public static void main(String[] args) {
		InitMongo initMongo=new InitMongo();
		initMongo.initTrain();

	}
	public void initSeats() {
		MongoDatabase mongoDatabase=mongoDB.getMongoDB();
		Reader reader=new Reader();
		List<String[]> route=reader.getRoutes();
		Random random=new Random();
		for (String[] strings : route) {
			mongoDatabase.createCollection(strings[0]);
			List<Document> documents=new ArrayList<>(15);
			MongoCollection<Document> collection=mongoDatabase.getCollection(strings[0]);
			Calendar calendar=Calendar.getInstance();
			calendar.add(Calendar.HOUR, random.nextInt(10));
			calendar.add(Calendar.MINUTE, random.nextInt(30));
			for(int i=0;i<10;i++){
				
			}
		}
		
	}
	public void initTrain() {
		MongoDatabase mongoDatabase=mongoDB.getMongoDB();
		mongoDatabase.createCollection("train");
		Reader reader=new Reader();
		List<String[]> route=reader.getRoutes();
		List<Document> documents=new ArrayList<>(200);
		MongoCollection<Document> collection=mongoDatabase.getCollection("train");
		for(String[] strings:route){
			Document row=new Document();
			String tid=strings[0];
			row.append("tid", tid);
			row.append("type", "8"); 
			BasicDBList basicDBList=new BasicDBList();
			double length = 0;
			for(int j=1;j<strings.length;j++){
				BasicDBObject basicDBObject=new BasicDBObject();
				basicDBObject.append("orderNum", j);
				basicDBObject.append("station", strings[j]);
				basicDBObject.append("length", length);
				basicDBList.add(basicDBObject);
				length+=Math.random()*1000;
			}
			row.append("station", basicDBList);
			documents.add(row);
		}
		collection.insertMany(documents);
	}
	
	public void initAccount() {
		MongoDatabase mongoDatabase=mongoDB.getMongoDB();
		mongoDatabase.createCollection("account");
		String[] a={"赵","钱","孙","李","周","吴","郑","王"};
		String[] b={"邪","毒","帝","丐","神通","飞","峰","复"};
		List<Document> documents=new ArrayList<>(10005);
		int p=0,q=0;
		for(int i=0;i<10000;i++){
			
			int num=8;
			BasicDBList basicDBList=new BasicDBList();
			for(int j=0;j<num;j++){
				BasicDBObject customer=new BasicDBObject();
				customer.append("cid", j+1);
				customer.append("name", a[(p++)%8]+b[(q++)%8]);
				customer.append("identity", customer.hashCode());
				basicDBList.add(customer);
			}
			Document account=new Document();
			account.append("aid", i+1);
			account.append("email", "cx"+i+"@example.com");
			account.append("password", "tttggg"+i+"frr");
			account.append("customer", basicDBList);
			documents.add(account);
		}
		MongoCollection<Document> collection=mongoDatabase.getCollection("account");
		collection.insertMany(documents);
	}
}
