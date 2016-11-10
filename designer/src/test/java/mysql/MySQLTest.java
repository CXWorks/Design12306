package mysql;

import org.testng.annotations.Test;

import impl.MySQLProvider;
import init.Reader;
import service.TicketService;

import java.util.Calendar;
import java.util.List;

import org.testng.annotations.BeforeTest;
public class MySQLTest {
	TicketService ticketService;
	String[] destination={"G41","北京南","德州东","济南西","曲阜东","蚌埠南","南京南","无锡东","上海虹桥","嘉兴南","杭州东"};
	Calendar calendar;
  @Test(invocationCount = 100, threadPoolSize = 50) 
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
		System.out.println(ticketService.queryTrain(destination[start], destination[end], calendar));
		
  }
  @BeforeTest
  public void beforeTest() {
	  	calendar=Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, 2);
		ticketService=new MySQLProvider();
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
