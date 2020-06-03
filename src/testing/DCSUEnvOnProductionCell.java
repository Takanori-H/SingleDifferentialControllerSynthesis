package testing;

import static org.junit.Assert.*;

import org.junit.Test;

public class DCSUEnvOnProductionCell {
	DirectoryTrackerForSingleWinningRegion dt;

	@Test
	public void test() {
		fail("まだ実装されていません");
	}

	void doJoinTest(String controller)
	{
		dt = new DirectoryTrackerForSingleWinningRegion("ProductionCell");
		long start=System.currentTimeMillis();
		dt.checkDCSUEnv(controller);
		long stop=System.currentTimeMillis();
		System.out.print("Spending time: "+(stop-start)+"ms");
		return;
	}
}
