package testing;

import org.junit.Test;

public class DifferentialControllerSynthesisOnAutomatedWarehouse {
	DirectoryTrackerForSingleWinningRegion dt;

	@Test
	public void case1test() {
		String[] cases = {"case1.txt"};
		System.out.println("cases: "+1);
		doJoinTest(cases);
	}

	void doJoinTest(String[] cases)
	{
		dt = new DirectoryTrackerForSingleWinningRegion("AutomatedWarehouse");
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
