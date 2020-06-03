package testing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import singlecontrollermodel.gamespace.ConcurrentSystemModelMaker;
import singlecontrollermodel.gamespace.DifferentialTransitionParser;
import singlecontrollermodel.gamespace.GameModel;
import singlecontrollermodel.model.Model;
import singlecontrollermodel.model.State;
import singlecontrollermodel.model.Transition;

public class DirectoryTrackerForSingleWinningRegion {
	private File directory;
	private List<String> reqnamelist;
	private List<String> cas;
	private Model[] reqs;
	private GameModel cm;
	private boolean firstCheck;
	private String sep=File.separator;
	private Model e;
	private Model c;
	private GameModel Game;
	private DifferentialTransitionParser difftp;
	private DifferentialTransitionParser distp;

	public DirectoryTrackerForSingleWinningRegion(String directory)
	{
		this.directory=new File(directory);
		if(this.directory.exists())
		{
			//directory.list() ディレクトリにあるファイルおよびディレクトリを示す文字列の配列を返す
			//Arrays.asList 配列をリストに変換する
			//ArrayList<String>(Arrays.asList...) 要素を初期値でセット
			reqnamelist=new ArrayList<String>(Arrays.asList(this.directory.list()));
			//Controllerディレクトリを除く
			reqnamelist.remove("Controller");
			//reqnamelist 要求のファイル名
			cas=new ArrayList<String>();
		}
		firstCheck=false;
	}

	private File searchEnvInDirectory()
	{
		return new File(directory.getAbsolutePath()+sep+"Controller"+sep+"ENV.txt");
	}

	private void setControllableAction()
	{
		File caFile=new File(directory.getAbsolutePath()+sep+"Controller"+sep+"CA.txt");
		try
		{
			BufferedReader reader =new BufferedReader(new FileReader(caFile));
			String c;
			while((c=reader.readLine())!=null)
			{
				//System.out.println("ControllablAction:"+c);
				cas.add(c);
			}
			reader.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		ConcurrentSystemModelMaker.setControllableAction(cas);
	}

	//TODO
	private Model[] getReqs()
	{
		Model[] reqs = new Model[reqnamelist.size()];
		FSP_FileReader reader;

		for(int i=0;i<reqnamelist.size();i++)
		{
			if (new File(directory.getPath()+sep+reqnamelist.get(i)).isFile())
			{
				reader=new FSP_FileReader(directory.getPath()+sep+reqnamelist.get(i));
//				System.out.println(candidate.get(i));
				reqs[i] = reader.getModel();
			}
		}
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		if(reqs.length==0)System.out.println("no reqirement level file");
		return reqs;
	}

	public void checkDesignTimeSynthesis()
	{
		getCModelFromDirectory();
		//FSP_FileReader reader=new FSP_FileReader(directory.getPath()+sep+"Controller"+sep+controller);//simulate用のController
		difftp = new DifferentialTransitionParser(cm);
		firstCheck=true;
		difftp.checkDesignTimeSynthesis();
	}

	public GameModel getCModelFromDirectory()
	{
		File env;
		reqs = new Model[reqnamelist.size()];
		if((env=searchEnvInDirectory())==null)
		{
			return null;
		}
		setControllableAction();
		FSP_FileReader reader;
		reader = new FSP_FileReader(env);
		e=reader.getModel();
		reqs = getReqs();
		cm = ConcurrentSystemModelMaker.makeConcurrentSystem(e, reqs, cas);
		return cm;
	}

	public void checkUpdateControllerFromFile(String fileName)
	{
		BufferedReader reader=null;
		try
		{
			reader=new BufferedReader(new FileReader(new File(directory.getPath()+sep+"Controller"+sep+fileName)));
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		try
		{
			String temp;
			while((temp=reader.readLine())!=null)
			{
				checkUpdatedController(temp);
			}
			reader.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void checkUpdatedController(String updatedPart)
	{
		if(!modelUpdate(updatedPart))return;
		System.out.println(cm.getUpdatedPart());
		//difftp.setModel(cm);
		difftp.checkDifferentialControllerSynthesis();
	}

	private boolean modelUpdate(String updatedPart)
	{
		if(!firstCheck)
		{
			System.out.println("ERROR: First check have not been done yet");
			return false;
		}
		String[] materials=updatedPart.split(",");
		if(materials.length!=3)
		{
			System.out.println("ERROR:Input type is wong");
			return false;
		}
		ConcurrentSystemModelMaker.modelUpdate(cm, e, reqs, materials[0], materials[1], materials[2]);
		return true;
	}

	//変化した環境のMTSA出力をENV.txtに入れておく
	public void checkDCSUEnv(String controller)
	{
		long start = System.currentTimeMillis();
		//simulate用のControllerを読み込む
		FSP_FileReader reader=new FSP_FileReader(directory.getPath()+sep+"Controller"+sep+controller);
		c=reader.getModel();
		long stop = System.currentTimeMillis();
		System.out.println("Spending time of Reading Simulate Controller: " + (stop-start) + "ms");
		long realstart = System.currentTimeMillis();
		start = System.currentTimeMillis();
		GameModel model = getUpdateCModelFromDirectory();
		stop = System.currentTimeMillis();
		System.out.println("Spending time of Game Create: "+(stop-start)+"ms");
		distp = new DifferentialTransitionParser(model);
		firstCheck = true;
		distp.checkDCSUEnv(c);
		long realstop = System.currentTimeMillis();
		System.out.println("Real Spending time: " + (realstop-realstart) + "ms");
		return;
	}

	public GameModel getUpdateCModelFromDirectory()
	{
		File env;
		reqs = new Model[reqnamelist.size()];
		if((env=searchEnvInDirectory())==null)
		{
			return null;
		}
		setControllableAction();
		long start = System.currentTimeMillis();
		FSP_FileReader reader;
		reader=new FSP_FileReader(env);
		e=reader.getModel();
		reqs=getReqs();
		long stop = System.currentTimeMillis();
		System.out.println("File Reader Time: " + (stop-start) + "ms");
		long start2 = System.currentTimeMillis();
		Game = ConcurrentSystemModelMaker.makeConcurrentSystem(e, reqs,cas);
		long stop2 = System.currentTimeMillis();
		System.out.println("Game Create Time: " + (stop2-start2) + "ms");
		return Game;
	}

	// Extracting model's difference between 2 files.
	public void extractDifference(File before, File after)
	{
		Model m1 = new FSP_FileReader(before).getModel(), m2 = new FSP_FileReader(after).getModel();
		HashMap<String, State> stateMap = new HashMap<String, State>();
		String t = null;
		List<String> l = new ArrayList<String>();
		BufferedWriter f = null;
		if (m1.getSize() != m2.getSize())
		{
			System.out.println("ERROR:These models are not similar");
			return;
		}
		makeStateMapWithoutR(stateMap, m1.getInitialState(), m2.getInitialState());
		for (int i = 0; i < m2.getSize(); i++)
		{
			State s2 = m2.getState(i);
			State s1 = stateMap.get(s2.toString());
			System.out.println("standard" + s2 + "," + s1);
			for (int j = 0; j < s2.getToTransitionNum(); j++)
			{
				t = s2.getToTransition(j).toString();
				if (s1.getToTransition(t) == null)
				{
					System.out.println(s2.getToStateByToTransition(t)+ ","+ stateMap.get(s2.getToStateByToTransition(t).toString()));
					l.add(stateMap.get(s2.toString())+","+t+ ","+stateMap.get(s2.getToStateByToTransition(t).toString()).toString());
				}
			}
		}// */
		if (l.size() == 0)
		{
			System.out.println("ERROR:There is no difference");
		}
		try
		{
			f = new BufferedWriter(new FileWriter(new File(directory.getPath()+sep+"Controller"+sep+t+".txt")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		try
		{
			f.write(l.get(0));
			for (int i = 1; i < l.size(); i++)
			{
				f.newLine();
				f.write(l.get(i));
			}
			f.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		System.out.println(directory.getAbsolutePath());
	}

	// used for extractDifference()
	private void makeStateMapWithoutR(HashMap<String, State> stateMap, State s1, State s2)
	{
		Stack<State> stack = new Stack<State>();
		State c2 = s2;
		stateMap.put(s2.toString(), s1);
		stack.push(c2);
		while (!stack.isEmpty())
		{
			c2 = stack.pop();
			s1 = stateMap.get(c2.toString());
			while (c2.hasNext())
			{
				Transition t2 = (Transition) c2.next(), t1;
				if ((t1 = s1.getToTransition(t2.toString())) != null)
					if (stateMap.get(t2.getTo().toString()) == null)
					{
						if (t1 == t2)
							System.out.println(t1.getTo() + "," + t2.getTo());
						stateMap.put(t2.getTo().toString(), t1.getTo());
						stack.push(t2.getTo());
					}
			}
		}
		System.out.println("model size:" + stateMap.size());
	}

	public void simulationCheck(File controller, File env)
	{
		Model m1 = new FSP_FileReader(controller).getModel();
		Model m2 = new FSP_FileReader(env).getModel();
		State c = m1.getInitialState();
		State e = m2.getInitialState();
		HashMap<String, State> stateMap = new HashMap<String, State>();
		simulationCheck(c, e, stateMap);
	}

	public void simulationCheck(State c, State e, HashMap<String, State> stateMap)
	{
		stateMap.put(c.getName(), e);
		System.out.println("コントローラー, 環境モデル");
		System.out.println(c + ", " + e);
		for(int i=0;i<c.getToTransitionNum();i++)
		{
			checkTransition(c.getToTransition(i), e, stateMap);
			if(i!=c.getToTransitionNum()-1)System.out.println(c + ", " + e);
		}
	}

	public void checkTransition(Transition ct, State e, HashMap<String, State> stateMap)
	{
		if(e.getToTransition(ct.getName())!=null)
		{
			Transition et = e.getToTransition(ct.getName());
			System.out.println(ct.getName());
			checkState(ct.getTo(), et.getTo(), stateMap);
		}
	}

	public void checkState(State c, State e, HashMap<String, State> stateMap)
	{
		if(stateMap.get(c.getName())!=null)
		{
			System.out.println(c + ", " + e);
			System.out.println("探索打ち切り");
			return;
		}
		stateMap.put(c.getName(), e);
		System.out.println(c + ", " + e);
		for(int i=0;i<c.getToTransitionNum();i++)
		{
			checkTransition(c.getToTransition(i), e, stateMap);
			if(i!=c.getToTransitionNum()-1)System.out.println(c + ", " + e);
		}
	}

	public void getENVChange(File controller, File env, File env2)
	{
		Model m1 = new FSP_FileReader(controller).getModel();
		Model m2 = new FSP_FileReader(env).getModel();
		Model m3 = new FSP_FileReader(env2).getModel();
		State c = m1.getInitialState();
		State e1 = m2.getInitialState();
		State e2 = m3.getInitialState();
		HashMap<String, State> stateMap = new HashMap<String, State>();
		HashMap<String, State> stateMap2 = new HashMap<String, State>();
		List<String> changeList = new ArrayList<String>();
		List<String> loopList = new ArrayList<String>();
		List<String> list = new ArrayList<String>();
		Stack<String> stack = new Stack<String>();
		String[] str;
		String part = "";
		int flag = 0;
		getENVChange(e2, e1, stateMap2, list);
		simulationCheck(c, e2, stateMap);
		for(int i=0;i<list.size();i++)
		{
			getChangePart(list.get(i), stateMap2, m3, stack, changeList, loopList, flag);
		}
		for(int i=0;i<changeList.size();i++)
		{
			str = changeList.get(i).split(", ", 0);
			part = str[0] + "(" + stateMap2.get(str[0]) + ")";
			for(int j=2;j<str.length;j+=2)
			{
				part += ", " + str[j-1] + ", " + str[j] + "(" + stateMap2.get(str[j]) + ")";
			}
			changeList.set(i, part);
		}
		for(int i=0;i<changeList.size();i++)
		{
			System.out.println(changeList.get(i));
		}
	}

	public void getENVChange(State e2, State e1, HashMap<String, State> stateMap, List<String> list)
	{
		stateMap.put(e2.getName(), e1);
		for(int i=0;i<e2.getToTransitionNum();i++)
		{
			getTransition(e2.getToTransition(i), e1, stateMap, list);
		}
	}

	public void getTransition(Transition et2, State e1, HashMap<String, State> stateMap, List<String> list)
	{
		if(e1.getToTransition(et2.getName())!=null)
		{
//			System.out.println(et2.getFrom() + ", " + et2.getName());
			Transition et1 = e1.getToTransition(et2.getName());
			getState(et2.getTo(), et1.getTo(), stateMap, list);
		}
		else
		{
			list.add(et2.getFrom().getName() + ", " + et2.getName()+ ", " + et2.getTo());
//			System.out.println(et2.getFrom().getName() + ", " + et2);
		}
	}

	public void getState(State e2, State e1, HashMap<String, State> stateMap, List<String> list)
	{
		if(stateMap.get(e2.getName())!=null)return;
		stateMap.put(e2.getName(), e1);
		for(int i=0;i<e2.getToTransitionNum();i++)
		{
			getTransition(e2.getToTransition(i), e1, stateMap, list);
		}
	}

	public void getChangePart(String part, HashMap<String, State> stateMap, Model m, Stack<String> stack, List<String> changeList, List<String> loopList, int flag)
	{
		String[] str = part.split(", ", 0);
		System.out.println(part);
		if(stateMap.get(str[2])==null)
		{
			stack.push(part);
			for(int i=0;i<m.getState(str[2]).getToTransitionNum();i++)
			{
				getChangePartTransition(stack, m.getState(str[2]).getToTransition(i), stateMap, changeList, loopList, flag);
			}
			stack.pop();
		}
		else
		{
			changeList.add(part);
		}
	}

	public void getChangePartTransition(Stack<String> stack, Transition tr, HashMap<String, State> stateMap, List<String> changeList, List<String> loopList, int flag)
	{
		String part = stack.peek();
//		System.out.println(part);
		stack.push(part + ", " + tr.getName());
		getChangePartState(stack, tr.getTo(), stateMap, changeList, loopList, flag);
	}

	public void getChangePartState(Stack<String> stack, State s, HashMap<String, State> stateMap, List<String> changeList, List<String> loopList, int flag)
	{
		String part = stack.pop();
		String[] str = part.split(", ", 0);
		stack.push(part + ", " + s.getName());
		System.out.println(stack.peek());
		if(Arrays.asList(str).contains(s.getName()))
		{
			int index = Arrays.asList(str).indexOf(s.getName());
			String loop = str[index];
			for(int i = index+1;i<str.length;i++)
			{
				loop += (", " + str[i]);
			}
			loopList.add(loop);
			System.out.println("ループした");
			return;
		}
		if(stateMap.get(s.getName())==null)
		{
			for(int i=0;i<s.getToTransitionNum();i++)
			{
				getChangePartTransition(stack, s.getToTransition(i), stateMap, changeList, loopList, flag);
//				stack.pop();
				if(flag == 0)System.out.println("ポップした " + stack.pop());
				else if(flag == 1)flag = 0;
			}
		}
		else
		{
			System.out.println(s.getName() + "(" + stateMap.get(s.getName()).getName() + ")");
			changeList.add(stack.pop());
			flag = 1;
			System.out.println(flag);
		}
	}
}
