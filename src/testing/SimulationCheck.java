package testing;

import java.io.File;

import org.junit.Test;

public class SimulationCheck {
	String sep=File.separator;
	@Test
	public void test() {
		DirectoryTrackerForSingleWinningRegion dt;
		dt=new DirectoryTrackerForSingleWinningRegion("AutomatedWarehouse");
		File controller =new File("AutomatedWarehouse"+sep+"Controller"+sep+"con.txt");
		File env=new File("AutomatedWarehouse"+sep+"Controller"+sep+"ENV.txt");
		dt.simulationCheck(controller, env);
	}
}
