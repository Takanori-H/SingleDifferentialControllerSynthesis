package testing;

import static org.junit.Assert.*;

import org.junit.Test;

public class DifferentialControllerSynthesisOnKivaSystems {
	DirectoryTrackerForSingleWinningRegion dt;

	@Test
	public void test() {
		fail("まだ実装されていません");
	}

	void doJoinTest(String[] cases)
	{
		dt = new DirectoryTrackerForSingleWinningRegion("KivaSystems");
		dt.checkDesignTimeSynthesis();
		for(int i=0;i<cases.length-1;i++)dt.checkUpdateControllerFromFile(cases[i]);
		long start=System.currentTimeMillis();
		dt.checkUpdateControllerFromFile(cases[cases.length-1]);
		long stop=System.currentTimeMillis();
		System.out.print("Spending time of ");
		for(int i=0;i<cases.length;i++)
		{
			System.out.print(cases[i]+"_");
		}
		System.out.println(":"+(stop-start)+"ms");
	}
}
