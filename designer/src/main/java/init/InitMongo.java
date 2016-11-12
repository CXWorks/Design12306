package init;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
	
	int[] seats_count16={120,400,800,160};
	int[] seats_count8={40,160,500,100};
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
			MongoCollection<Document> collection=mongoDatabase.getCollection(strings[0]);
			Calendar calendar=Calendar.getInstance();
			calendar.add(Calendar.DAY_OF_YEAR, -1);
			calendar.add(Calendar.HOUR, random.nextInt(10));
			calendar.add(Calendar.MINUTE, random.nextInt(30));
			double type=Math.random();
			for(int i=0;i<10;i++){
				Document document=new Document();
				document.append("date", new Date(calendar.getTimeInMillis()));
				for(int k=0;k<4;k++){
					BasicDBList list=new BasicDBList();
					for (int j = 1; j < strings.length-1; j++) {
						if (type<0.01) {
							list.add(seats_count16[k]);
						}else {
							list.add(seats_count8[k]);
						}
					}
					document.append("stype_"+Integer.toString(k), list);
				}
				calendar.add(Calendar.DAY_OF_YEAR,1);
				BasicDBList ticket=new BasicDBList();
				if (type<0.1) {
					//16
					//commercial
					for(int p=1;p<4;p++){
						for(int q=1;q<=10;q++){
							for(int l=0;l<4;l++){
								BasicDBObject object=new BasicDBObject();
								object.append("stype", "0");
								object.append("t_c_id", p);
								object.append("row", q);
								object.append("location", l);
								object.append("ticket", Integer.MAX_VALUE);
								ticket.add(object);
							}
						}
					}
					//first
					for(int p=4;p<9;p++){
						for(int q=1;q<=16;q++){
							for(int l=0;l<5;l++){
								BasicDBObject object=new BasicDBObject();
								object.append("stype", "1");
								object.append("t_c_id", p);
								object.append("row", q);
								object.append("location", l);
								object.append("ticket", Integer.MAX_VALUE);
								ticket.add(object);
							}
						}
					}
					//second&stand
					for(int p=9;p<17;p++){
						for(int q=1;q<=20;q++){
							for(int l=0;l<6;l++){
								BasicDBObject object=new BasicDBObject();
								
								object.append("t_c_id", p);
								object.append("row", q);
								object.append("location", l);
								object.append("ticket", Integer.MAX_VALUE);
								
								if (l==5) {
									object.append("stype", "3");
								}else {
									object.append("stype", "2");
								}
								
								ticket.add(object);
							}
						}
					}
				}else {
					//8
					//commercial
					for(int p=1;p<2;p++){
						for(int q=1;q<=10;q++){
							for(int l=0;l<4;l++){
								BasicDBObject object=new BasicDBObject();
								object.append("stype", "0");
								object.append("t_c_id", p);
								object.append("row", q);
								object.append("location", l);
								object.append("ticket", Integer.MAX_VALUE);
								ticket.add(object);
							}
						}
					}
					//first
					for(int p=2;p<4;p++){
						for(int q=1;q<=16;q++){
							for(int l=0;l<5;l++){
								BasicDBObject object=new BasicDBObject();
								object.append("stype", "1");
								object.append("t_c_id", p);
								object.append("row", q);
								object.append("location", l);
								object.append("ticket", Integer.MAX_VALUE);
								ticket.add(object);
							}
						}
					}
					//second&stand
					for(int p=4;p<9;p++){
						for(int q=1;q<=20;q++){
							for(int l=0;l<6;l++){
								BasicDBObject object=new BasicDBObject();
								
								object.append("t_c_id", p);
								object.append("row", q);
								object.append("location", l);
								object.append("ticket", Integer.MAX_VALUE);
								
								if (l==5) {
									object.append("stype", "3");
								}else {
									object.append("stype", "2");
								}
								
								ticket.add(object);
							}
						}
					}
				}
				document.append("ticket", ticket);
				collection.insertOne(document);
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
			double length = 0;
			for(int j=1;j<strings.length;j++){
				BasicDBObject basicDBObject=new BasicDBObject();
				basicDBObject.append("orderNum", j);
				basicDBObject.append("length", length);
				row.append(strings[j], basicDBObject);
				length+=Math.random()*1000;
			}
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
