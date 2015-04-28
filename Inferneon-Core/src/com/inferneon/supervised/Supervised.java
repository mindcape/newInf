package com.inferneon.supervised;

import com.inferneon.core.Instance;
import com.inferneon.core.Instances;
import com.inferneon.core.Value;
import com.inferneon.core.exceptions.InvalidDataException;

public abstract class Supervised {
	
	public abstract void train(Instances instances);
	public abstract Value classify(Instance instance);

}
