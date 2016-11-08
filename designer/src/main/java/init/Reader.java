package init;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/*
  Design12306/init/Reader.java
  @author cxworks
  2016-11-06
*/

public class Reader {
	private final String file_path="routes.txt";
	
	public List<String[]> getRoutes() {
		List<String[]> ans=new ArrayList<>(200);
		File file=new File(file_path);
		if (file.exists()) {
			try (Scanner scanner=new Scanner(file);){
				while (scanner.hasNextLine()) {
					String line=scanner.nextLine();
					String[] array=line.split("( |-)");
					ans.add(array);
				}
				return ans;
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
			
		}
		return null;
	}
	public static void main(String[] args) {
		Reader reader=new Reader();
		reader.getRoutes();
	}
}
