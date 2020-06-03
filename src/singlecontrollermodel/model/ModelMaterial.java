package singlecontrollermodel.model;

//StateやTransitionのもと
//今のところ抽象クラスである必要性がない
//抽象メソッドを使っていないため
public abstract class ModelMaterial
{
	//StateやTransitionを識別するための変数
	protected String name;
	//環境側のWinningRegionに含まれているか否か．trueなら含まれており，falseなら含まれていない．
	protected boolean isDead;
	//Controllerか否か．
	private boolean isController;

	protected ModelMaterial(String name)
	{
		this.name = name;
		isDead = false;
		isController = false;
	}

	@Override
	//どっちか必要ない?
	public String toString()
	{
		return name;
	}

	public String getName()
	{
		return name;
	}

	//環境側のWinningRegionか否かに関して
	public boolean isDead()
	{
		return isDead;
	}

	public void setIsDead()
	{
		isDead = true;
	}

	public void eraseIsDead()
	{
		isDead = false;
	}

	//コントローラーか否かに関して
	public boolean isController()
	{
		return isController;
	}

	public void setIsController()
	{
		isController = true;
	}

	public void eraseIsController()
	{
		isController = false;
	}

	//最後の要素に到達していたらfalse，それ以外はtrueを返す．
	abstract boolean hasNext();
	//次の要素を取得する．
	abstract ModelMaterial next();
	abstract void reset();

//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime * result + ((name == null) ? 0 : name.hashCode());
//		return result;
//	}
//
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (obj == null)
//			return false;
//		if (!(obj instanceof ModelMaterial))
//			return false;
//		ModelMaterial other = (ModelMaterial) obj;
//		if (name == null) {
//			if (other.name != null)
//				return false;
//		} else if (!name.equals(other.name))
//			return false;
//		System.out.println("FFFF");
//		return true;
//	}
}
