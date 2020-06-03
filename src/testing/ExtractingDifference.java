package testing;

import java.io.File;

import org.junit.Test;

public class ExtractingDifference {
	String sep=File.separator;
	@Test
	public void test() {
		DirectoryTrackerForSingleWinningRegion dt;
		//環境変化前と後の差分が取りたいフォルダの名前を書く
		dt=new DirectoryTrackerForSingleWinningRegion("KivaSystems");
		File before =new File("KivaSystems"+sep+"Controller"+sep+"ENV.txt");
		File after=new File("KivaSystems"+sep+"Controller"+sep+"ENV2.txt");
		dt.extractDifference(before, after);
	}
}
