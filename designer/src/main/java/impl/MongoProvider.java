package impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.BasicDBObject;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.BsonField;

import connector.MongoDB;
import service.AutoResult;
import service.TicketService;

/*
  Design12306/impl/MongoProvider.java
  @author cxworks
  2016-11-12
*/

public class MongoProvider implements TicketService {
	
	private Bson[] stype={new BasicDBObject("$min","stype_0"),new BasicDBObject("$min","stype_1"),new BasicDBObject("$min","stype_2"),new BasicDBObject("$min","stype_3")};
	private BsonField[] sfields={new BsonField("0", stype[0]),new BsonField("1", stype[1]),new BsonField("2", stype[2]),new BsonField("3", stype[3])};
	private MongoDatabase mongodb;
	private MongoDB mongo;
	public MongoProvider() {
		mongo=new MongoDB();
		mongodb=mongo.getMongoDB();
	}
	
	public static void main(String[] args) {
		MongoProvider provider=new MongoProvider();
		provider.queryTrain("济南西", "上海虹桥", Calendar.getInstance());
	}
	
	@Override
	public AutoResult queryTrain(String src, String target, Calendar date) {
		String startdates=date.get(Calendar.YEAR)+"-"+(date.get(Calendar.MONTH)+1)+"-"+date.get(Calendar.DAY_OF_MONTH);
		date.add(Calendar.DAY_OF_MONTH, 1);
		String enddatee=date.get(Calendar.YEAR)+"-"+(date.get(Calendar.MONTH)+1)+"-"+date.get(Calendar.DAY_OF_MONTH);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat ("yyyy-MM-dd");
		
		MongoCollection<Document> collection=mongodb.getCollection("train");
		FindIterable<Document> iterable=collection.find(Filters.and(Filters.exists(src),
				Filters.exists(target),
				Filters.where("this."+src+".orderNum < this."+target+".orderNum")));
		MongoCursor<Document> cursor=iterable.iterator();
		Set<String[]> tidset=new HashSet<>(40);
		while (cursor.hasNext()) {
			Document document = cursor.next();
			String[] strings={document.getString("tid"),((Document)document.get(src)).getString("orderNum"),((Document)document.get(src)).getString("orderNum")};
			tidset.add(strings);
		}
		cursor.close();
		if (tidset.isEmpty()) {
			return new AutoResult();
		}
		//
		try {
			Date start=simpleDateFormat.parse(startdates);
			Date end=simpleDateFormat.parse(enddatee);
			for (String[] strings : tidset) {
				int pst=Integer.parseInt(strings[1]);
				int pen=Integer.parseInt(strings[2]);
				MongoCollection<Document> train=mongodb.getCollection(strings[0]);
				AggregateIterable<Document> iterable2=train.aggregate(Arrays.asList(
						Aggregates.match(Filters.and(Filters.gte("date", start),Filters.lt("date", end))),
						Aggregates.project(Projections.fields(Projections.exclude("ticket"),
								Projections.slice("stype_0", pst-1, pen-pst),Projections.slice("stype_1", pst-1, pen-pst),
								Projections.slice("stype_2", pst-1, pen-pst),Projections.slice("stype_3", pst-1, pen-pst))),
						Aggregates.group("$_id", sfields)));
				MongoCursor<Document> cursor2=iterable2.iterator();
				while (cursor2.hasNext()) {
					Document document =  cursor2.next();
					System.out.println(document);
				}
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public AutoResult orderTicket(String src, String target, String tid, Calendar date, String seats_type, int account,
			int... customer) {
		// TODO Auto-generated method stub
		return null;
	}

}
