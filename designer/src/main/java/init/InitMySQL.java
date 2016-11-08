package init;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import connector.MySQL;

/**
 *  Design12306/init/InitMySQL.java
 *  2016年11月7日 下午8:24:28
 *  @Author cxworks
 *
 */

public class InitMySQL {
	private MySQL mySQL;
	public InitMySQL() {
		mySQL=new MySQL();
		
	}
	private Connection getConnection(){
		try {
			Connection connection=mySQL.getConnection();
			connection.setAutoCommit(true);
			return connection;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		InitMySQL initMySQL=new InitMySQL();
		initMySQL.initTrain_Carriage();
	}
	private int[] getUsefulCaid(int type,int num) {
		String select="SELECT `carriage`.`carriage_id` "
				+ "FROM `carriage` WHERE "
				+ "`carriage`.`carriage_id` NOT IN (SELECT `train_carriage`.`carriage_id` FROM `train_carriage` WHERE 1)"
				+ " AND `carriage`.`stype`=?";
		try (PreparedStatement pStatement=getConnection().prepareStatement(select)){
			pStatement.setInt(1, type);
			ResultSet resultSet=pStatement.executeQuery();
			int[] a=new int[num];
			for (int i = 0; i < num&&resultSet.next(); i++) {
				a[i]=resultSet.getInt(1);
			}
			return a;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
	}

	public void initTrain_Carriage() {
		String sql = "INSERT INTO `train_carriage`(`tid`, `carriage_id`, `t_c_id`) VALUES (?,?,?)";

		Reader reader = new Reader();
		List<String[]> s_t = reader.getRoutes();
		int[] c1id=getUsefulCaid(0, 800);
		int[] c2id=getUsefulCaid(1, 1000);
		int[] c3id=getUsefulCaid(2, 2000);
		int c1=0,c2=0,c3=0;
		try (PreparedStatement pStatement = getConnection().prepareStatement(sql)) {
			for (String[] sa : s_t) {
				
			
				try {
					String tid = sa[0];
					Random random = new Random();
					if (random.nextBoolean()) {
						int num=1;
						// 8 = 1+2+5
						pStatement.setString(1, tid);
						pStatement.setInt(2, c1id[c1++]);
						pStatement.setInt(3, num++);
						pStatement.addBatch();
						//2
						for (int i = 0; i < 2; i++) {
							pStatement.setString(1, tid);
							pStatement.setInt(2, c2id[c2++]);
							pStatement.setInt(3, num++);
							pStatement.addBatch();
						}
						//3
						for (int i = 0; i < 5; i++) {
							pStatement.setString(1, tid);
							pStatement.setInt(2, c3id[c3++]);
							pStatement.setInt(3, num++);
							pStatement.addBatch();
						}
						pStatement.executeBatch();
					} else {
						// 16 = 3+5+8
						int num=1;
						for(int i=0;i<3;i++){
							pStatement.setString(1, tid);
							pStatement.setInt(2, c1id[c1++]);
							pStatement.setInt(3, num++);
							pStatement.addBatch();
						}
						//2
						for (int i = 0; i < 5; i++) {
							pStatement.setString(1, tid);
							pStatement.setInt(2, c2id[c2++]);
							pStatement.setInt(3, num++);
							pStatement.addBatch();
						}
						//3
						for (int i = 0; i < 8; i++) {
							pStatement.setString(1, tid);
							pStatement.setInt(2, c3id[c3++]);
							pStatement.setInt(3, num++);
							pStatement.addBatch();
						}
						pStatement.executeBatch();
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
	
	public void initStation_Train() {
		String sql="INSERT INTO `station_train`(`station`, `tid`, `orderedNum`, `arrive_at`, `leave_at`, `after_day`, `length`) VALUES (?,?,?,?,?,?,?)";
		Reader reader=new Reader();
		List<String[]> s_t=reader.getRoutes();
		try (PreparedStatement pStatement=getConnection().prepareStatement(sql)){
			s_t.stream().forEach(sa->{
				String tid=sa[0];
				Random random=new Random();
				Calendar calendar=Calendar.getInstance();
				calendar.add(Calendar.HOUR, random.nextInt(12));
				int after_day=0;
				double length=0;
				int day=calendar.get(Calendar.DAY_OF_YEAR);
				try {
				for (int i = 1; i < sa.length; i++) {
					int j=1;
					
						pStatement.setString(j++, sa[i]);
						pStatement.setString(j++, tid);
						pStatement.setInt(j++, i);
						pStatement.setTime(j++, new Time(calendar.getTimeInMillis()));
						calendar.add(Calendar.MINUTE, random.nextInt(20));
						//check
						
						pStatement.setTime(j++, new Time(calendar.getTimeInMillis()));
						pStatement.setInt(j++, after_day);
						pStatement.setDouble(j++, length);
						length+=random.nextDouble()*1000;
						calendar.add(Calendar.MINUTE, random.nextInt(100));
						if (day!=calendar.get(Calendar.DAY_OF_YEAR)) {
							day=calendar.get(Calendar.DAY_OF_YEAR);
							after_day++;
						}
						pStatement.addBatch();
				}
				pStatement.executeBatch();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public void initCustomer(){
		String sql="INSERT INTO `customer`(`aid`, `cid`, `cname`, `ctype`, `identity`) VALUES (?,?,?,?,?)";
		try(PreparedStatement pStatement=getConnection().prepareStatement(sql)) {
			Random random=new Random();
			for(int i=1;i<10001;i++){
				int j=1;
				pStatement.setInt(j++, i);
				pStatement.setInt(j++, 1);
				pStatement.setString(j++, "cx"+i+"%"+j);
				double t=random.nextDouble();
				if (t<=0.05){
					pStatement.setInt(j++, 1);
				}else {
					pStatement.setInt(j++, 0);
				}
				pStatement.setString(j++, "4103061999");
				pStatement.addBatch();
			}
			pStatement.executeBatch();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void initAccount() {
		String sql="INSERT INTO `account`(`email`, `password`) VALUES (?,?)";
		try (PreparedStatement pStatement = getConnection().prepareStatement(sql)) {
			for (int i = 0; i < 10000; i++) {
				int j = 1;
				pStatement.setString(j, "cx" + i + "@nju.edu.cn");
				j++;
				pStatement.setString(j, i + "dedad");
				pStatement.addBatch();
			}
			pStatement.executeBatch();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void initCarriage(){
		String sql="INSERT INTO `carriage`(`stype`) VALUES (?)";
		Random random=new Random();
		try (PreparedStatement pStatement = getConnection().prepareStatement(sql)) {
			for (int i = 0; i < 10000; i++) {
				
				double j=random.nextDouble();
				if (j<0.5){
					pStatement.setInt(1, 2);
				}else if (j<0.833) {
					pStatement.setInt(1, 1);
				}else {
					pStatement.setInt(1, 0);
				}
				pStatement.addBatch();
			}
			pStatement.executeBatch();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void initCarriage_Stype_Seats() {
		String sql="INSERT INTO `carriage_stype_seats`(`stype`, `row`, `location`, `price`, `student_price`) VALUES (?,?,?,?,?)";
		try(PreparedStatement preparedStatement=getConnection().prepareStatement(sql)) {
			// business 40 = 4 * 10
			for (int i = 0; i < 10; i++) {
				for (int k = 0; k < 4; k++) {
					int j = 1;
					preparedStatement.setInt(j++, 0);
					preparedStatement.setInt(j++, i + 1);
					preparedStatement.setInt(j++, k);
					preparedStatement.setDouble(j++, 400);
					preparedStatement.setDouble(j++, 400);
					preparedStatement.addBatch();
					
				}
			}
			// 1 st seats 80 = 5 * 16
			for (int i = 0; i < 16; i++) {
				for (int k = 0; k < 5; k++) {
					int j = 1;
					preparedStatement.setInt(j++, 1);
					preparedStatement.setInt(j++, i + 1);
					preparedStatement.setInt(j++, k);
					preparedStatement.setDouble(j++, 200);
					preparedStatement.setDouble(j++, 200);
					preparedStatement.addBatch();
				}
			}
			//2 nd seats 100 = 5 * 20
			for (int i = 0; i < 20; i++) {
				for (int k = 0; k < 5; k++) {
					int j = 1;
					preparedStatement.setInt(j++, 2);
					preparedStatement.setInt(j++, i + 1);
					preparedStatement.setInt(j++, k);
					preparedStatement.setDouble(j++, 100);
					preparedStatement.setDouble(j++, 75);
					preparedStatement.addBatch();
				}
			}
			//no seats 20
			for (int i = 0; i < 20; i++) {
					int j = 1;
					preparedStatement.setInt(j++, 2);
					preparedStatement.setInt(j++, i + 1);
					preparedStatement.setInt(j++, -1);
					preparedStatement.setDouble(j++, 100);
					preparedStatement.setDouble(j++, 75);
					preparedStatement.addBatch();			
			}
			preparedStatement.executeBatch();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		
	}
}
