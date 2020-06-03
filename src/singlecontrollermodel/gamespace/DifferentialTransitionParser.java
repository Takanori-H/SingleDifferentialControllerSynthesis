package singlecontrollermodel.gamespace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import singlecontrollermodel.model.Model;
import singlecontrollermodel.model.State;
import singlecontrollermodel.model.Transition;

public class DifferentialTransitionParser
{
	int count;
	int flag;
	GameModel model;
	List<String> transitionRecordsOfModel;//GameのTransitionの記録
	List<State> WRCTmp;
//	List<State> WRCnow;
	List<State> deltaWRC;
	List<State> deltaWRE;
	HashMap<String,State> C;
	Model con;

	public DifferentialTransitionParser(GameModel model)
	{
		this.model = model;
		this.transitionRecordsOfModel = new ArrayList<String>();
		WRCTmp = new ArrayList<State>();
//		WRCnow = new ArrayList<State>();
		C = new HashMap<String, State>();
	}

	public void checkDesignTimeSynthesis()
	{
		DesignTimeSynthesis();
	}

//	/*
	void DesignTimeSynthesis()
	{
		GameModel model = this.model;
		List<State> WRC = IdentifyWR(model);
		generateController(WRC);
		System.out.println("Design Time");
		System.out.println("State数: " + C.size() + ", Transition数: " + CountTransitionNum(C));
		System.out.println();
	}
//	*/
	List<State> IdentifyWR(GameModel model)
	{
		count = 0;
		State er = model.getErrorState();
		er.setIsDead();
		count++;
		for(int i=0;i<er.getFromTransitionNum();i++)pasteDToTransition(er.getFromTransition(i));
		System.out.println("GameState: " + model.getSize());
		System.out.println("count: " + count);
		State initial = model.getInitialState();
		initial.setIsController();
		WRCTmp.add(initial);
		for(int i=0;i<initial.getToTransitionNum();i++)countWRC(initial.getToTransition(i).getTo());
		System.out.println("WRCTmp: " + WRCTmp.size());
//		State tmpS;
//		int count = 0;
//		for(int i=0;i<WRCTmp.size();i++)
//		{
//			tmpS = WRCTmp.get(i);
//			for(int j=0;j<tmpS.getToTransitionNum();j++)
//			{
//				if(tmpS.getToTransition(j).isDead())continue;
//				System.out.println(tmpS.getToTransition(j));
//				count++;
//			}
//		}
//		System.out.println("A number of WRCTmp's Transition is " + count);
		return WRCTmp;
	}

	void pasteDToTransition(Transition m)
	{
		if (m.isDead())
			return;
		// System.out.println("This is dead transition:["+m+"]");//debug
		m.setIsDead();
		this.pasteDToState(m.getFrom());
	}

	void pasteDToState(State m)
	{//バックワードプロパゲーション
		if (m.isDead())
			return;
		boolean dead = true;
		//System.out.println("state "+m);
		for (int i = 0; i < m.getToTransitionNum(); i++)
		{
			Transition t = m.getToTransition(i);
			//System.out.println("  Transition:"+t+" isControllable:"+t.isControllable()+" isDead:"+t.isDead());
			if (!t.isControllable() && t.isDead())
			{//UnControllable Actionで環境側のWinning Regionに繋がるならば
				m.setIsDead();//そのStateはDead
				//WRETmp.add(m);
				count++;
				//System.out.println("Dead state:["+m+"]");//debug
				for (int j = 0; j < m.getFromTransitionNum(); j++)
				{
					pasteDToTransition(m.getFromTransition(j));
				}
				return;
			}
			else if (!t.isDead())
			{//環境側のWinning Regionにつながっていないならば
				dead = false;//環境側のWRにつながっているUnControllableActionがなければok
			}
		}

		if (dead)
		{
			m.setIsDead();
			//WRETmp.add(m);
			count++;
		}
		//System.out.println(" isDead:"+m.isDead());
		if (m.isDead())
		{
			for (int i = 0; i < m.getFromTransitionNum(); i++)
			{
				pasteDToTransition(m.getFromTransition(i));
			}
		}
	}

//	/*
	void countWRC(State tmp)
	{
		if(tmp.isDead() || WRCTmp.contains(tmp))return;
		tmp.setIsController();
		WRCTmp.add(tmp);
		for(int i=0;i<tmp.getToTransitionNum();i++)
		{
			countWRC(tmp.getToTransition(i).getTo());
		}
	}
//	*/

	void generateController(List<State> WRC)
	{
		State tmpState, cState, fromState, toState, fromCState, toCState;
		Transition tmpTransition, cTransition;
		C = new HashMap<String, State>();
		for(int i=0;i<WRC.size();i++)
		{
			//状態だけ用意
			tmpState = WRC.get(i);
			cState = new State(tmpState.getName());
			C.put(cState.getName(), cState);
		}
		for(int i=0;i<WRC.size();i++)
		{
			tmpState = WRC.get(i);
			cState = C.get(tmpState.getName());
			for(int j=0;j<tmpState.getFromTransitionNum();j++)
			{
				tmpTransition = tmpState.getFromTransition(j);
				if(!tmpTransition.isDead())
				{
					fromState = tmpTransition.getFrom();//Winning Region上でのTransitionのfromState
					fromCState = C.get(fromState.getName());//コントローラ上でのTransitionのfromState
					toCState = cState;//cStateのfromTransitionのtoStateはcState
					cTransition = new Transition(tmpTransition.getName(), fromCState, toCState);
					if(tmpTransition.isControllable())cTransition.setIsControllable();
					cState.addFromTransition(cTransition);
				}
			}
			for(int j=0;j<tmpState.getToTransitionNum();j++)
			{
				tmpTransition = tmpState.getToTransition(j);
				if(!tmpTransition.isDead())
				{
					//fromState = tmpTransition.getFrom();//Winning Region上でのTransitionのfromState
					//fromCState = C.get(fromState.getName());//コントローラ上でのTransitionのfromState
					fromCState = cState;//cStateのtoTransitionのfromStateはcState
					toState = tmpTransition.getTo();//Winning Region上でのTransitionのtoState
					toCState = C.get(toState.getName());//コントローラ上でのTransitionのtoState
					cTransition = new Transition(tmpTransition.getName(), fromCState, toCState);
					if(tmpTransition.isControllable())cTransition.setIsControllable();
					cState.addToTransition(cTransition);
				}
			}
		}
		//Controllers.put(level,C);
	}

	public int CountTransitionNum(HashMap<String, State> C)
	{
		int count = 0;
		for(int i=0;i<C.size();i++)
		{
			State cState = C.get(WRCTmp.get(i).getName());
			count += cState.getToTransitionNum();
		}
		return count;
	}

	public void checkDifferentialControllerSynthesis()
	{
		long start = System.currentTimeMillis();
		System.out.println(model.getUpdatedPart());
		DifferenceSynthesis();
		long stop = System.currentTimeMillis();
		System.out.println("Spending time of DifferenceSynthesis: "+(stop-start)+"ms");
		System.out.println("Run Time");
		System.out.println("State数: "+C.size()+", "+"Transition数: "+CountTransitionNum(C));
	}

	void DifferenceSynthesis()
	{
		List<State> tmpDeltaWRC = new ArrayList<State>();
		long start = System.currentTimeMillis();
		tmpDeltaWRC = IdentifyUpdateWR(model);
		long stop = System.currentTimeMillis();
		System.out.println("Spending time of IdentifyWR: "+(stop-start)+"ms");
		if(flag == 1)
		{
			System.out.println("There is no controller.");
			return;
		}
		long start2 = System.currentTimeMillis();
		//元のコントローラ内にTransitionが増えていた場合にTransitionを付け加える
		UpdateController(model);
		WRCTmp.addAll(tmpDeltaWRC);
		//差分更新
		generateUpdateController(tmpDeltaWRC);
		long stop2 = System.currentTimeMillis();
//		WRCnow = new ArrayList<>(WRCTmp);
		System.out.println("Spending time of Controller Update: "+(stop2-start2)+"ms");
		System.out.println("Spending time of Synthesis: "+(stop2-start)+"ms");
	}

	List<State> IdentifyUpdateWR(GameModel model)
	{
//		WRCTmp = new ArrayList<State>();
		deltaWRC = new ArrayList<State>();
		deltaWRE = new ArrayList<State>();
		count = 0;
//		WRCTmp = new ArrayList<>(WRCnow);
		flag = 0;
		List<State> delta = new ArrayList<State>();
		//updatePart Gameをアップデートする時に新たにくわわる遷移 fromとtoを見れば新たに加える遷移が見れるかも
		List<Transition> l = model.getUpdatedPart();
		Transition tmp;
		State to, from, tmpS, tmpC;
//		int count = 0;
//		for(int i=0;i<WRCTmp.size();i++)
//		{
//			tmpS = WRCTmp.get(i);
//			for(int j=0;j<tmpS.getToTransitionNum();j++)
//			{
//				if(tmpS.getToTransition(j).isDead())continue;
//				System.out.println(tmpS.getToTransition(j));
//				count++;
//			}
//		}
//		System.out.println("A number of WRCTmp's Transition is " + count);
		//追加された遷移によって新たに環境側のWinning Regionになったゲームの状態をdeltaWRCにいれる
		for(int i=0;i<l.size();i++)
			if(l.get(i).getTo().isDead())
				pasteUpdateDToTransition(l.get(i));
		//simulateチェック
		for(int i=0;i<WRCTmp.size();i++)
			if(WRCTmp.get(i).isDead())
			{
				flag = 1;
				return null;
			}
		//モデルのアップデートによって追加されたコントローラ側のwinning regionの状態を調べる
		//元のコントローラのwinning regionから新たに外に出る遷移を見る
		//元のコントローラのwinning regionから新たに外に出た遷移がつながる状態を集める
		for(int i=0;i<l.size();i++)
		{
			tmp = l.get(i);
			to = tmp.getTo();
			from = tmp.getFrom();
			if(WRCTmp.contains(from) && !WRCTmp.contains(to) && !to.isDead())
			{
				delta.add(to);
				tmpC = C.get(from.getName());
				tmpC.addToTransition(tmp);
			}
		}
		//元のコントローラのwinning regionから一つ外の状態から増えたコントローラのwinning regionを辿っていく
		for(int i=0;i<delta.size();i++)
		{
			tmpS = delta.get(i);
//			deltaWRC.add(tmpS);
			countDeltaWRC(tmpS);
		}
		System.out.println("GameState: "+ model.getSize());
		System.out.println("WRCTmp: " + WRCTmp.size());
		System.out.println("deltaWRC: " + deltaWRC.size());
//		WRCTmp.addAll(deltaWRC);
		System.out.println("WRCTmp: " + WRCTmp.size());
		System.out.println(delta);
//		count = 0;
//		for(int i=0;i<WRCTmp.size();i++)
//		{
//			tmpS = WRCTmp.get(i);
//			for(int j=0;j<tmpS.getToTransitionNum();j++)
//			{
//				if(tmpS.getToTransition(j).isDead())continue;
//				count++;
//			}
//		}
//		System.out.println("A number of WRCTmp's Transition is " + count);
		return deltaWRC;
	}

	void pasteUpdateDToTransition(Transition m)
	{
		if (m.isDead())
			return;
		// System.out.println("This is dead transition:["+m+"]");//debug
		m.setIsDead();
		this.pasteUpdateDToState(m.getFrom());
	}

//	/*
	void pasteUpdateDToState(State m)
	{//バックワードプロパゲーション
		if (m.isDead())
			return;
		boolean dead = true;
		//System.out.println("state "+m);
		for (int i = 0; i < m.getToTransitionNum(); i++)
		{
			Transition t = m.getToTransition(i);
			//System.out.println("  Transition:"+t+" isControllable:"+t.isControllable()+" isDead:"+t.isDead());
			if (!t.isControllable() && t.isDead())
			{//UnControllable Actionで環境側のWinning Regionに繋がるならば
				m.setIsDead();//そのStateはDead
				//System.out.println(m.getName());
//				WRETmp.add(m);
				deltaWRE.add(m);
//				deltaWRC.add(m);
				count++;
				//System.out.println("Dead state:["+m+"]");//debug
				for (int j = 0; j < m.getFromTransitionNum(); j++)
				{
					pasteUpdateDToTransition(m.getFromTransition(j));
				}
				return;
			}
			else if (!t.isDead())
			{//環境側のWinning Regionにつながっていないならば
				dead = false;//環境側のWRにつながっているUnControllableActionがなければok
			}
		}

		if (dead)
		{
			m.setIsDead();
			count++;
//			deltaWRC.add(m);
//			WRETmp.add(m);
			deltaWRE.add(m);
		}
		//System.out.println(" isDead:"+m.isDead());
		if (m.isDead())
		{
			for (int i = 0; i < m.getFromTransitionNum(); i++)
			{
				pasteUpdateDToTransition(m.getFromTransition(i));
			}
		}
	}
//	*/

	void countDeltaWRC(State tmpS)
	{
		if(WRCTmp.contains(tmpS) || deltaWRC.contains(tmpS) || tmpS.isDead())
			return;
		tmpS.setIsController();
		deltaWRC.add(tmpS);
		for(int i=0;i<tmpS.getToTransitionNum();i++)
		{
			countDeltaWRC(tmpS.getToTransition(i).getTo());
		}
	}

	void UpdateController(GameModel model)
	{//元のコントローラ内にTransitionが増えていた場合にコントローラ内にTransitionを増やす
		if(C==null)System.out.println("Controller is NULL.");
		List<Transition> l = model.getUpdatedPart();
		Transition tr, newTransition;
		State from, to, fromC, toC;
//		int count = 0;
		for(int i=0;i<l.size();i++)
		{
			tr = l.get(i);
			from = tr.getFrom();
			to = tr.getTo();
			if(WRCTmp.contains(from) && WRCTmp.contains(to))
			{
				System.out.println("irkvnaofdk");
				fromC = C.get(from.getName());
				toC = C.get(to.getName());
				newTransition = new Transition(tr.getName(), fromC, toC);
				if(tr.isControllable())newTransition.setIsControllable();
				toC.addFromTransition(newTransition);
				from.addToTransition(newTransition);
//				count++;
			}
		}
//		System.out.println("A number of UpdateController's NewTransition is " + count);
	}

	void generateUpdateController(List<State> deltaWRC)
	{
		State tmpS, cS, fromS, toS, fromCS, toCS;
		Transition tmpT, cT;
//		int count = 0;
		//状態だけ用意
		for(int i=0;i<deltaWRC.size();i++)
		{
			tmpS = deltaWRC.get(i);
			cS = new State(tmpS.getName());
			C.put(cS.getName(), cS);
		}
		for(int i=0;i<deltaWRC.size();i++)
		{
			tmpS = deltaWRC.get(i);
			cS = C.get(tmpS.getName());
			for(int j=0;j<tmpS.getFromTransitionNum();j++)
			{
				tmpT = tmpS.getFromTransition(j);
				if(!tmpT.isDead())
				{
					fromS = tmpT.getFrom();
					fromCS = C.get(fromS.getName());
					toCS = cS;
					cT = new Transition(tmpT.getName(), fromCS, toCS);
					if(tmpT.isControllable())cT.setIsControllable();
					cS.addFromTransition(cT);
				}
			}
			for(int j=0;j<tmpS.getToTransitionNum();j++)
			{
				tmpT = tmpS.getToTransition(j);
				if(!tmpT.isDead())
				{
					fromCS = cS;
					toS = tmpT.getTo();
					toCS = C.get(toS.getName());
					cT = new Transition(tmpT.getName(), fromCS, toCS);
					if(tmpT.isControllable())cT.setIsControllable();
					cS.addToTransition(cT);
//					count++;
				}
			}
		}
//		System.out.println("A number of generateUpdateController's NewTransition is " + count);
	}

	public void checkDCSUEnv(Model controller)
	{
		DCSUEnv();
	}

	public void setCon(Model c)
	{
		this.con = c;
	}

	void DCSUEnv()
	{
		long start = System.currentTimeMillis();
		List<State> WRC =  IdentifyWR(model);
		if(WRC.size()==0)
		{
			long stop=System.currentTimeMillis();
			System.out.println("Spending time of IdentifyWR: "+(stop-start)+"ms");
			System.out.println("There is no controller.");
		}
		long start2 = System.currentTimeMillis();
		generateController(WRC);
		long stop = System.currentTimeMillis();
		System.out.println("Spending time of Generate Controller: "+(stop-start2)+"ms");
		System.out.println("Spending time of IdentifyWR+Generate Controller: "+(stop-start)+"ms");
		System.out.println("State数: " + C.size() + ", Transition数: " + CountTransitionNum(C));
	}
}
