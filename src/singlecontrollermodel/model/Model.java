package singlecontrollermodel.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Model implements ModelInterface
{
	//State型の配列
	State[] states;
	//初期状態とエラー状態
	State initialState,errorState;
	//識別子
	String name;
	HashMap<String,State> map;

	public Model(State[] states,HashMap<String,State> map)
	{
		this.states = states;
		this.map = map;
		//state配列の0番目の要素が初期状態
		this.setInitialState(states[0]);
		//ErrorStateはstateの最後の配列に格納されてる？
		this.setErrorState(states[states.length-1]);
	}

	Model(String name,State[] states,HashMap<String,State> map)
	{
		this.name = name;
		this.states = states;
		this.map = map;
		this.setInitialState(states[0]);
		this.setErrorState(states[states.length-1]);
	}

	public State getState(int i)
	{
		//stateの配列の方からget
		return states[i];
	}

	public State getState(String i)
	{
		//HashMapの方からget
		return map.get(i);
	}

	public int getSize()
	{
		//stateの配列の長さ state数
		return states.length;
	}

	public State getInitialState()
	{
		//初期状態返す
		return this.initialState;
	}

	public void setInitialState(State s)
	{
		//初期状態セット
		this.initialState=s;
	}

	public State getErrorState()
	{
		//エラー状態のget
		return this.errorState;
	}

	public void setErrorState(State s){//エラー状態のセット
		this.errorState=s;
	}

	//Transitionのupdateしか考えていない？
	public List<Transition> getUpdatedPart()
	{
		//Transition型の要素をもつリスト
		return new ArrayList<Transition>();
	}

	public String getName()
	{
		return name;
	}
}
