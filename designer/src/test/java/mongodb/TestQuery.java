package mongodb;

import org.testng.annotations.Test;

import impl.MongoProvider;
import impl.MySQLProvider;
import init.Reader;
import service.TicketService;

import java.util.Calendar;
import java.util.List;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;

public class TestQuery {
	TicketService ticketService;
	String[] destination={"G41","北京南","德州东","济南西","曲阜东","蚌埠南","南京南","无锡东","上海虹桥","嘉兴南","杭州东"};
	Calendar calendar;

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
		System.out.println(ticketService.queryTrain(destination[start], destination[end], Calendar.getInstance()));
  }
  @BeforeClass
  public void beforeClass() {
	  	calendar=Calendar.getInstance();
		ticketService=new MongoProvider();
		Reader reader =new Reader();
		List<String[]> src=reader.getRoutes();
		for (String[] strings : src) {
			System.out.println(strings[0]);
			if (strings[0].equalsIgnoreCase("G41")) {
				
				destination=strings;
				break;
			}
		}
  }


}
