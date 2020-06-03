package singlecontrollermodel.model;

import java.util.HashMap;
import java.util.List;

public class Transition extends ModelMaterial
{
	//from(State)->Transtion->to(State)
	//Transitionが出てきたState
	private State from;
	//Transitionが入るState
	private State to;
	//TransitionがControllableかUncontrollableか Transitionが使われたかどうか
	private boolean isControllable, used;
	//?
	private HashMap<String, String> errorStringMap;

	public Transition(String name, State from, State to)
	{
		super(name);
		this.from = from;
		this.to = to;
		this.isControllable = false;
		this.used = false;
	}

	public Transition(String name)
	{
		super(name);
		this.isControllable = false;
	}

	public void setFrom(State from)
	{
		this.from = from;
	}

	public State getFrom()
	{
		return this.from;
	}

	public void setTo(State to)
	{
		this.to = to;
	}

	public State getTo()
	{
		return this.to;
	}

	public void setIsControllable()
	{
		isControllable = true;
	}

	public boolean isControllable()
	{
		return isControllable;
	}

	@Override
	boolean hasNext()
	{
		return false;
	}

	@Override
	//行き先のStateを返す
	ModelMaterial next()
	{
		used = true;
		return this.to;
	}

	@Override
	//Transitionの探索をリセット
	void reset()
	{
		//Transitionが使われたかどうかをリセット
		used = false;
	}

	//要求のどれがエラーしてるか的な？そんな感じ
	public void setErrorStrings(State env, List<State> initReq)
	{
		// TODO Auto-generated method stub
		used = true;
		if(errorStringMap==null)errorStringMap = new HashMap<String, String>();
		String errorString = env.getName(), error = "";
		for(int i=0;i<initReq.size();i++)
		{
			String tmp = initReq.get(i).toString();
			//initReqのstateの名前にERRORが含まれているならば
			if(tmp.contains("ERROR"))
			{
				if(error!="")error = error.concat("&&");
				//String + int +演算子の左辺が文字列なので右辺の数値は文字列に変換される よって文字列＋文字列
				tmp = tmp + i;
				//concat 文字列結合
				error = error.concat(tmp);
			}
		}
		System.out.println("Set error string as" + errorString + " for " + error + "@" + this);
		errorStringMap.put(error, errorString);
	}

	public String getErrorString(String error)
	{
		if(errorStringMap==null || errorStringMap.get(error)==null)
		{
			System.out.println("ERROR: no errorString with " + error + "@" + this + used);
			return null;
		}
		return errorStringMap.get(error);
	}
}
