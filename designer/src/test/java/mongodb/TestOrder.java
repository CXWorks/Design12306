package mongodb;

import org.testng.annotations.Test;

import impl.MongoProvider;
import impl.MySQLProvider;
import init.Reader;
import service.TicketService;

import java.util.Calendar;
import java.util.List;

import org.testng.annotations.BeforeTest;

public class TestOrder {
	TicketService ticketService;
	String[] destination={"G41","北京南","德州东","济南西","曲阜东","蚌埠南","南京南","无锡东","上海虹桥","嘉兴南","杭州东"};
	Calendar calendar;
	static volatile int i=0;
	String[] stypes={"商务座","一等座","二等座","无座"};
	 @Test(invocationCount = 1300, threadPoolSize = 300)
  public void f() {
	  int start=0;
		while(start==0||start==destination.length){
			Double a=new Double(Math.random()*destination.length);
			start=a.intValue();
		}
		int end=0;
		while(end<=start||end==destination.length){
			end=new Double(Math.random()*destination.length).intValue();
		}
		int people = new Double(Math.random()*16).intValue();
		if (people==0) {
			people=1;
		}
		int[] a=new int[people];
		for(int i=0;i<people;i++){
			a[i]=i+1;
		}
		int stype=new Double(Math.random()*4).intValue();
		Calendar day=Calendar.getInstance();
		day.add(Calendar.DAY_OF_YEAR, 1);
		System.out.println(ticketService.orderTicket(destination[start], destination[end], "G41", day, stypes[stype], 5, a));
  }
  @BeforeTest
  public void beforeTest() {
	  calendar=Calendar.getInstance();
//		calendar.add(Calendar.DAY_OF_YEAR, 4);
		ticketService=new MongoProvider();
		Reader reader =new Reader();
		List<String[]> src=reader.getRoutes();
		for (String[] strings : src) {
			if (strings[0].equalsIgnoreCase("G41")) {
				destination=strings;
				break;
			}
		}
  }

}
