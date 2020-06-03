package testing;

import org.junit.Test;

public class DCSUEnvOnAutomatedWarehouse {
	DirectoryTrackerForSingleWinningRegion dt;

	@Test
	public void test() {
		String cont = "Controller.txt";
		doJoinTest(cont);
	}

	void doJoinTest(String controller)
	{
		dt = new DirectoryTrackerForSingleWinningRegion("AutomatedWarehouse");
		long start=System.currentTimeMillis();
		dt.checkDCSUEnv(controller);
		long stop=System.currentTimeMillis();
		System.out.print("Spending time: "+(stop-start)+"ms");
		return;
	}

}
