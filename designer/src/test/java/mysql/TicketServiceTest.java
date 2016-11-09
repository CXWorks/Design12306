package mysql;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import impl.MySQLProvider;
import init.Reader;
import service.TicketService;

/*
  Design12306/service/TicketServiceTest.java
  @author cxworks
  2016-11-09
*/

public class TicketServiceTest {
	TicketService ticketService;
	String[] destination;
	Calendar calendar;
	@Before
	public void setUp() throws Exception {
		calendar=Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, 3);
		ticketService=new MySQLProvider();
		Reader reader =new Reader();
		List<String[]> src=reader.getRoutes();
		for (String[] strings : src) {
			if (strings[0].equalsIgnoreCase("G41")) {
				destination=strings;
				break;
			}
		}
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		int start=0;
		while(start==0||start==destination.length){
			start=new Double(Math.random()*destination.length).intValue();
		}
		int end=0;
		while(end<=start||end==destination.length){
			end=new Double(Math.random()*destination.length).intValue();
		}
		System.out.println(ticketService.queryTrain(destination[start], destination[end], calendar));
		
	}

}
