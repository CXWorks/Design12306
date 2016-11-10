package impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import connector.MySQL;
import service.CustomerSeats;

/*
  Design12306/impl/OrderTask.java
  @author cxworks
  2016-11-10
*/

public class OrderTask implements Supplier<Integer>,Runnable {
	private Calendar date;
	private String src;
	private String tid;
	private String tar;
	private int account;
	private List<CustomerSeats> customerSeats;
	
	public OrderTask(Calendar date,String tid, String src, String tar, int account, List<CustomerSeats> customerSeats) {
		this.date = date;
		this.tid=tid;
		this.src = src;
		this.tar = tar;
		this.account = account;
		this.customerSeats = customerSeats;
	}


	@Override
	public Integer get() {
		MySQL mySQL=new MySQL();
		
		try (Connection connection=mySQL.getConnection()){
			
			String sql1="INSERT INTO `order`(`oid`, `aid`, `orderAt`, `tid`, `start`, `end`, `startAt`) VALUES (?,?,?,?,?,?,?)";
			PreparedStatement pStatement1=connection.prepareStatement(sql1);
			int j=1;
			String oid=src+tar+account+customerSeats.hashCode();
			pStatement1.setString(j++, oid);
			pStatement1.setInt(j++,account);
			pStatement1.setDate(j++, new Date(Calendar.getInstance().getTimeInMillis()));
			pStatement1.setString(j++, tid);
			pStatement1.setString(j++, src);
			pStatement1.setString(j++, tar);
			pStatement1.setDate(j++, new Date(date.getTimeInMillis()));
			pStatement1.execute();
			String sql2="INSERT INTO `order_ticket`(`oid`, `cid`, `aid`, `stype`, `ctype`, `t_c_id`, `row`, `location`) VALUES (?,?,?,?,?,?,?,?)";
			PreparedStatement pStatement2=connection.prepareStatement(sql2);
			for (CustomerSeats customerSeats2 : customerSeats) {
				j=1;
				pStatement2.setString(j++, oid);
				pStatement2.setInt(j++, customerSeats2.cid);
				pStatement2.setInt(j++, account);
				pStatement2.setInt(j++, customerSeats2.stype);
				pStatement2.setInt(j++, 0);
				pStatement2.setInt(j++, customerSeats2.t_c_id);
				pStatement2.setInt(j++, customerSeats2.row);;
				pStatement2.setInt(j++, customerSeats2.location);
				pStatement2.addBatch();
			}
			pStatement2.executeBatch();
			connection.commit();
			connection.close();
			System.out.println("order inserted");
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
			
		}
		return 0;
	}

	public Integer call() throws Exception {
MySQL mySQL=new MySQL();
		
		try (Connection connection=mySQL.getConnection()){
			
			String sql1="INSERT INTO `order`(`oid`, `aid`, `orderAt`, `tid`, `start`, `end`, `startAt`) VALUES (?,?,?,?,?,?,?)";
			PreparedStatement pStatement1=connection.prepareStatement(sql1);
			int j=1;
			String oid=src+tar+account+customerSeats.hashCode();
			pStatement1.setString(j++, oid);
			pStatement1.setInt(j++,account);
			pStatement1.setDate(j++, new Date(Calendar.getInstance().getTimeInMillis()));
			pStatement1.setString(j++, tid);
			pStatement1.setString(j++, src);
			pStatement1.setString(j++, tar);
			pStatement1.setDate(j++, new Date(date.getTimeInMillis()));
			pStatement1.execute();
			String sql2="INSERT INTO `order_ticket`(`oid`, `cid`, `aid`, `stype`, `ctype`, `t_c_id`, `row`, `location`) VALUES (?,?,?,?,?,?,?,?)";
			PreparedStatement pStatement2=connection.prepareStatement(sql2);
			for (CustomerSeats customerSeats2 : customerSeats) {
				j=1;
				pStatement2.setString(j++, oid);
				pStatement2.setInt(j++, customerSeats2.cid);
				pStatement2.setInt(j++, account);
				pStatement2.setInt(j++, customerSeats2.stype);
				pStatement2.setInt(j++, 0);
				pStatement2.setInt(j++, customerSeats2.t_c_id);
				pStatement2.setInt(j++, customerSeats2.row);;
				pStatement2.setInt(j++, customerSeats2.location);
				pStatement2.addBatch();
			}
			pStatement2.executeBatch();
			connection.commit();
			connection.close();
			System.out.println("order inserted");
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
			
		}
		return 0;
	}


	@Override
	public void run() {
MySQL mySQL=new MySQL();
		
		try (Connection connection=mySQL.getConnection()){
			
			String sql1="INSERT INTO `order`(`oid`, `aid`, `orderAt`, `tid`, `start`, `end`, `startAt`) VALUES (?,?,?,?,?,?,?)";
			PreparedStatement pStatement1=connection.prepareStatement(sql1);
			int j=1;
			String oid=src+tar+account+customerSeats.hashCode();
			pStatement1.setString(j++, oid);
			pStatement1.setInt(j++,account);
			pStatement1.setDate(j++, new Date(Calendar.getInstance().getTimeInMillis()));
			pStatement1.setString(j++, tid);
			pStatement1.setString(j++, src);
			pStatement1.setString(j++, tar);
			pStatement1.setDate(j++, new Date(date.getTimeInMillis()));
			pStatement1.execute();
			String sql2="INSERT INTO `order_ticket`(`oid`, `cid`, `aid`, `stype`, `ctype`, `t_c_id`, `row`, `location`) VALUES (?,?,?,?,?,?,?,?)";
			PreparedStatement pStatement2=connection.prepareStatement(sql2);
			for (CustomerSeats customerSeats2 : customerSeats) {
				j=1;
				pStatement2.setString(j++, oid);
				pStatement2.setInt(j++, customerSeats2.cid);
				pStatement2.setInt(j++, account);
				pStatement2.setInt(j++, customerSeats2.stype);
				pStatement2.setInt(j++, 0);
				pStatement2.setInt(j++, customerSeats2.t_c_id);
				pStatement2.setInt(j++, customerSeats2.row);;
				pStatement2.setInt(j++, customerSeats2.location);
				pStatement2.addBatch();
			}
			pStatement2.executeBatch();
			connection.commit();
			connection.close();
			System.out.println("order inserted");
		} catch (Exception e) {
			e.printStackTrace();
			return ;
			
		}
		return ;
		
	}

}
