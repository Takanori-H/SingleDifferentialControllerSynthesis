package testing;

import static org.junit.Assert.*;

import org.junit.Test;

public class DCSUEnvOnKivaSystems {
	DirectoryTrackerForSingleWinningRegion dt;

	@Test
	public void test() {
		fail("まだ実装されていません");
	}

	void doJoinTest(String controller)
	{
		dt = new DirectoryTrackerForSingleWinningRegion("KivaSystems");
		long start=System.currentTimeMillis();
		dt.checkDCSUEnv(controller);
		long stop=System.currentTimeMillis();
		System.out.print("Spending time: "+(stop-start)+"ms");
		return;
	}
}
