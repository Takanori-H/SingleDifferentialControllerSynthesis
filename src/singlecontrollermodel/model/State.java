package singlecontrollermodel.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class State extends ModelMaterial implements Cloneable
{
	//fromTransition->State->toTransition
	//Stateに入ってくるTransition群
	private List<Transition> fromTransitionList;
	//Stateから出てる次の状態へのTransition
	private HashMap<String, Transition> toTransition;
	//Stateから出ているTransitionのアクションのList
	private List<String> toTransitionNameList;
	//あるStateから出ているTransitionのなかで幾つのTransitionを探索したか
	private int toTransitionPointer;

	public State(String name)
	{
		//ModelMaterialのコンストラクタに引数nameを渡してインスタンス化
		super(name);
		fromTransitionList = new ArrayList<Transition>();
		toTransition = new HashMap<String, Transition>();
		toTransitionNameList = new ArrayList<String>();
	}

	public boolean containsFromTransition(String name)
	{
		for(int i=0;i<fromTransitionList.size();i++)
		{
			Transition tr = fromTransitionList.get(i);
			if(tr.getName()==name)return true;
		}
		return false;
	}

	//あるStateに入るTransitionを加える
	public void addFromTransition(Transition tr)
	{
		fromTransitionList.add(tr);
	}

	public void eraseFromTransition(Transition tr)
	{
		for(int i=0;i<fromTransitionList.size();i++)
		{
			if(tr==fromTransitionList.get(i))
			{
				fromTransitionList.remove(i);
			}
		}
	}

	//あるStateに幾つのTransitionが入ってくるかどうか
	public int getFromTransitionNum()
	{
		return this.fromTransitionList.size();
	}

	//fromTransitionをget
	public Transition getFromTransition(int i) {
		return this.fromTransitionList.get(i);
	}

	//あるStateから出るTransitionのなかにnameのアクションが含まれているか否か
	public boolean containsToTransition(String name)
	{
		return this.toTransitionNameList.contains(name);
	}

	//あるStateから出るTransitionに引数として与えられたTransitionが含まれていなかったらToTransitionに加える
	public void addToTransition(Transition tr)
	{
		if(!containsToTransition(tr.toString()))
		{
			toTransitionNameList.add(tr.toString());
			toTransition.put(tr.toString(), tr);
		}
	}

	public void eraseToTransition(String name)
	{
		toTransition.remove(name);
	}

	public void eraseToTransitionNameList(String name)
	{
		toTransitionNameList.remove(name);
	}

	//あるStateから遷移できるかどうか つまりDead endかどうか
	//遷移できるならtrue 遷移できないならfalse
	Boolean hasToTransitions()
	{
		return !toTransition.isEmpty();
	}

	//あるStateからTransitionによって遷移できるStateを取ってくる
	public State getToStateByToTransition(String tr)
	{
		//getToはTransitionのメソッド
		return this.toTransition.get(tr).getTo();
	}

	//あるStateから幾つのTransitionが出ているか
	public int getToTransitionNum()
	{
		return this.toTransitionNameList.size();
	}

	//toTransitionNameListからname持ってきてHashMapのtoTransitionからTransitionをget
	public Transition getToTransition(int i)
	{
		return this.toTransition.get(this.toTransitionNameList.get(i));
	}

	//NameListを経由せず直接HashMapからget
	public Transition getToTransition(String name)
	{
		return this.toTransition.get(name);
	}

	@Override
	//あるStateで他に道があるかどうか 環境側のwinning regionに入らないような道を探す
	//あるState(状態)で探索してないTransitionがまだあるかどうか
	public boolean hasNext()
	{
		return toTransitionPointer < toTransition.size();
	}

	@Override
	//ある状態で探索し終わったTransitionの次のTransitionをget
	public ModelMaterial next()
	{
		return getToTransition(toTransitionPointer++);
	}

	@Override
	//探索のリセット
	public void reset()
	{
		toTransitionPointer = 0;
	}

	public State getClone()
	{
		State s = null;
		try
		{
			s = (State)this.clone();
			s.reset();
		}
		catch(CloneNotSupportedException e)
		{
			e.printStackTrace();
		}
		return s;
	}

//	@Override
//	public boolean equals(Object other)
//	{
//		if(this == other)
//		{
//			return true;
//		}
//		if (!(other instanceof State))
//		{
//            return false;
//        }
//		State otherState = (State) other;
//        if (this.getName().equals(otherState.getName())) {
//        	System.out.println("QQQQQ");
//            return true;
//        }
//        return false;
//	}
//
//	@Override
//	public int hashCode() {
//        return 0;
//    }
}
