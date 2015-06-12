package com.inferneon.supervised;

import com.inferneon.core.IInstances;
import com.inferneon.core.Instance;
import com.inferneon.core.Value;

public abstract class Supervised {
	
	public abstract void train(IInstances instances) throws Exception;	
	public abstract Value classify(Instance instance);

}
