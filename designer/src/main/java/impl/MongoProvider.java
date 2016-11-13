package impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.UnwindOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.BsonField;

import connector.MongoDB;
import service.AutoResult;
import service.CustomerSeats;
import service.OrderResult;
import service.TicketQuery;
import service.TicketService;
import service.TrainSeats;

/*
  Design12306/impl/MongoProvider.java
  @author cxworks
  2016-11-12
*/

public class MongoProvider implements TicketService {
	private Bson[] stype={new BasicDBObject("$min",""),new BasicDBObject("$min","$s_1"),new BasicDBObject("$min","$s_2"),new BasicDBObject("$min","$s_3")};
	private BsonField[] sfields={new BsonField("s0", stype[0]),new BsonField("s1", stype[1]),new BsonField("s2", stype[2]),new BsonField("s3", stype[3])};
	private MongoDatabase mongodb;
	private MongoDB mongo;
	public MongoProvider() {
		mongo=new MongoDB();
		mongodb=mongo.getMongoDB();
	}
	
	public static void main(String[] args) {
		MongoProvider provider=new MongoProvider();
//		System.out.println(provider.queryTrain("济南西", "上海虹桥", Calendar.getInstance()));
		System.out.println(provider.orderTicket("济南西", "上海虹桥", "G41", Calendar.getInstance(), "商务座", 1, 1,2,3));
//		System.out.println(Integer.toBinaryString(2147483647));
		
	}
	
	private Date[] generateRange(Calendar date){
		String startdates=date.get(Calendar.YEAR)+"-"+(date.get(Calendar.MONTH)+1)+"-"+date.get(Calendar.DAY_OF_MONTH);
		date.add(Calendar.DAY_OF_MONTH, 1);
		String enddatee=date.get(Calendar.YEAR)+"-"+(date.get(Calendar.MONTH)+1)+"-"+date.get(Calendar.DAY_OF_MONTH);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat ("yyyy-MM-dd");
		Date[] ans=new Date[2];
		try {
			ans[0]=simpleDateFormat.parse(startdates);
			ans[1]=simpleDateFormat.parse(enddatee);
			return ans;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public AutoResult queryTrain(String src, String target, Calendar date) {
		
		MongoCollection<Document> collection=mongodb.getCollection("train");
		FindIterable<Document> iterable=collection.find(Filters.and(Filters.exists(src),
				Filters.exists(target),
				Filters.where("this."+src+".orderNum < this."+target+".orderNum")));
		MongoCursor<Document> cursor=iterable.iterator();
		Set<String[]> tidset=new HashSet<>(40);
		while (cursor.hasNext()) {
			Document document = cursor.next();
			String[] strings={document.getString("tid"),Integer.toString((int) ((Document)document.get(src)).get("orderNum"))
					,Integer.toString((int) ((Document)document.get(target)).get("orderNum"))};
			tidset.add(strings);
		}
		cursor.close();
		if (tidset.isEmpty()) {
			return new AutoResult();
		}
		//
			Date[] range=generateRange(date);
			Date start=range[0];
			Date end=range[1];
			List<TrainSeats> trainSeats=new ArrayList<>(30);
			for (String[] strings : tidset) {
				int pst=Integer.parseInt(strings[1]);
				int pen=Integer.parseInt(strings[2]);
				MongoCollection<Document> train=mongodb.getCollection(strings[0]);
				 BasicDBList stype_0 = new BasicDBList();
				    stype_0.add("$stype_0");
				    stype_0.add(pst-1);
				    stype_0.add(pen-pst);
				    BasicDBList stype_1 = new BasicDBList();
				    stype_1.add("$stype_1");
				    stype_1.add(pst-1);
				    stype_1.add(pen-pst);
				    BasicDBList stype_2 = new BasicDBList();
				    stype_2.add("$stype_2");
				    stype_2.add(pst-1);
				    stype_2.add(pen-pst);
				    BasicDBList stype_3 = new BasicDBList();
				    stype_3.add("$stype_3");
				    stype_3.add(pst-1);
				    stype_3.add(pen-pst);
				AggregateIterable<Document> iterable2=train.aggregate(Arrays.asList(
						Aggregates.match(Filters.and(Filters.gte("date", start),Filters.lt("date", end))),
						Aggregates.project(Projections.exclude("ticket")),
						Aggregates.project(Projections.fields(
								new BasicDBObject("s_0", new BasicDBObject("$min",new BasicDBObject("$slice", stype_0)) ),new BasicDBObject("s_1",new BasicDBObject("$min", new BasicDBObject("$slice", stype_1)) ),
								new BasicDBObject("s_2",new BasicDBObject("$min", new BasicDBObject("$slice", stype_2)) ),new BasicDBObject("s_3",new BasicDBObject("$min", new BasicDBObject("$slice", stype_3)) )))
						));
				MongoCursor<Document> cursor2=iterable2.iterator();				
				while (cursor2.hasNext()) {
					Document document =  cursor2.next();
					int[] seats=new int[4];
					for(int q=0;q<4;q++){
						
						seats[q]=document.getInteger("s_"+q);
					}
					trainSeats.add(new TrainSeats(strings[0], seats));
				}			
			}
			AutoResult autoResult=new TicketQuery(date, src, target, trainSeats);
			return autoResult;
	}
	private int seats2stype(String seats_type) {
		if (seats_type.contains("商")) {
			return 0;
		}else if (seats_type.contains("一")) {
			return 1;
		}else if(seats_type.contains("二")){
			return 2;
		}else {
			return 3;
		}
	}
	private long generateMask(int s,int e) {
		long origin=new Double(Math.pow(2, e-s)-1).longValue();
		origin=origin<<(s-1);
		System.out.println(Long.toBinaryString(origin));
		return origin;
	}
	@Override
	public AutoResult orderTicket(String src, String target, String tid, Calendar date, String seats_type, int account,
			int... customer) {
		MongoCollection<Document> traincollection=mongodb.getCollection("train");
		FindIterable<Document> iterable=traincollection.find(
				Filters.eq("tid", tid));
		Document num=iterable.first();
		int[] order={((Document)num.get(src)).getInteger("orderNum"),((Document)num.get(target)).getInteger("orderNum")};
		int len=customer.length;
		Date[] range=generateRange(date);
		Date start=range[0];
		Date end=range[1];
		//check
		MongoCollection<Document> collection=mongodb.getCollection(tid);
		Document train=collection.find(Filters.and(Filters.gte("date", start),Filters.lt("date", end))).first();
		List<Integer> array=(List<Integer>) train.get("stype_"+seats2stype(seats_type));
		int min=Integer.MAX_VALUE;
		BasicDBList list=new BasicDBList();
		for(int i=0;i<array.size();i++){
			int t=array.get(i);
			if (i>=order[0]-1&&i<order[1]-1) {
				if (min>t) {
					min=t;
				}
				t-=len;
			}
			list.add(t);
		}
		if (min<len) {
			return new AutoResult();
		}
		//up1 order
		collection.findOneAndUpdate(Filters.and(Filters.gte("date", start),Filters.lt("date", end)),
				new BasicDBObject("$set", new BasicDBObject("stype_"+seats2stype(seats_type), list)) );
		// up2 ticket
		UnwindOptions unwindOptions=new UnwindOptions();
		unwindOptions.includeArrayIndex("index");
		long mask=generateMask(order[0], order[1]);
		AggregateIterable<Document> aggregateIterable=collection.aggregate(Arrays.asList(
				
				Aggregates.match(Filters.and(Filters.gte("date", start),Filters.lt("date", end))),
				Aggregates.unwind("$ticket",unwindOptions),
				Aggregates.match(Filters.bitsAllSet("ticket.ticket", mask)),
				Aggregates.limit(len),
				Aggregates.project(Projections.exclude("date","stype_0","stype_1","stype_2","stype_3"))
				));
		MongoCursor<Document> cursor=aggregateIterable.iterator();
		List<CustomerSeats> seats=new ArrayList<>(len);
		List<Document> temp=new ArrayList<>(len);
		cursor.forEachRemaining(d->{
			Document document=(Document) d.get("ticket");
			temp.add(d);
			seats.add(new CustomerSeats(0, 
					Integer.parseInt((String) document.get("stype")), 
					document.getInteger("t_c_id"), 
					document.getInteger("row"), 
					document.getInteger("location")));
		});
		//update
		temp.stream().forEach(d->{
			Document document=(Document) d.get("ticket");
			long init=document.getInteger("ticket");
			init=init^mask;
			document.replace("ticket", init);
		});
		for (Document document : temp) {
			collection.updateOne(Filters.and(Filters.gte("date", start),Filters.lt("date", end)), 
					new BasicDBObject("$set", new BasicDBObject("ticket."+document.get("index"), document.get("ticket"))));
		}
		for(int i=0;i<customer.length;i++){
			seats.get(i).cid=customer[i];
		}
		OrderResult orderResult=new OrderResult(date, src, target, seats);
//		insertOrder(src, target, tid, date, account, customerSeats);
		return orderResult;
	}
}
