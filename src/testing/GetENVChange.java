package testing;

import java.io.File;

import org.junit.Test;

public class GetENVChange {
	String sep=File.separator;
	@Test
	public void test() {
		DirectoryTrackerForSingleWinningRegion dt;
		dt=new DirectoryTrackerForSingleWinningRegion("AutomatedWarehouse");
		File controller =new File("AutomatedWarehouse"+sep+"Controller"+sep+"con.txt");
		File env=new File("AutomatedWarehouse"+sep+"Controller"+sep+"ENV.txt");
		File env2=new File("AutomatedWarehouse"+sep+"Controller"+sep+"ENV2.txt");
		dt.getENVChange(controller, env, env2);
	}

}
