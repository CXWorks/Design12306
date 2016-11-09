package mysql;

import org.testng.annotations.Test;

import impl.MySQLProvider;
import init.Reader;
import service.TicketService;

import java.util.Calendar;
import java.util.List;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.BeforeMethod;
public class MySQLTest {
	TicketService ticketService;
	String[] destination;
	Calendar calendar;
  @Test(invocationCount = 1, threadPoolSize = 50) 
  public void f() {
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
  @BeforeMethod
  public void doBeforeMethod() {
  
  	        System.err.println("testClass1: before method");
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

}
