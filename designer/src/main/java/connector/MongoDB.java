package connector;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientOptions.Builder;
import com.mongodb.client.MongoDatabase;

/*
  Design12306/connector/MongoDB.java
  @author cxworks
  2016-11-06
*/

public class MongoDB {
	private MongoDatabase mongoDatabase;
	private void connect() {
		Builder builder = MongoClientOptions.builder();
		builder.maxConnectionLifeTime(500);
		builder.connectTimeout(2000);
		builder.connectionsPerHost(20);
		
		MongoClient mongoClient=new MongoClient("114.55.37.133:15008",builder.build());
		mongoDatabase=mongoClient.getDatabase("work3");
		return ;
	}
	
	public MongoDB() {
		connect();
	}
	public MongoDatabase getMongoDB(){
		return mongoDatabase;
	}
	
	public static void main(String[] args) {
		MongoDB mongoDB=new MongoDB();
		mongoDB.connect();
	}
}
