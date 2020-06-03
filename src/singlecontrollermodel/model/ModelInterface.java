package singlecontrollermodel.model;

//LTSとGameでメソッドは共通のものが多いからInterfaceでまとめておく
import java.util.List;

public interface ModelInterface {
	int getSize();
	void setInitialState(State s);
	State getInitialState();
	State getErrorState();
	List<Transition> getUpdatedPart();//差分のにほひ
	State getState(int i);
	String getName();
}
