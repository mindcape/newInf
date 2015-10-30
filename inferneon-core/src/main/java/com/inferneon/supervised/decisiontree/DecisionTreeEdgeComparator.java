package com.inferneon.supervised.decisiontree;

import java.io.Serializable;
import java.util.Comparator;

import com.inferneon.core.ValueComparator;
import com.inferneon.supervised.decisiontree.DecisionTreeEdge.Type;

public class DecisionTreeEdgeComparator  implements Comparator<DecisionTreeEdge>, Serializable{

	@Override
	public int compare(DecisionTreeEdge edge1, DecisionTreeEdge edge2) {
		Type type1 = edge1.getType();
		Type type2 = edge2.getType();
		
		if(type1 != type2){
			if(type1 == Type.PREDICATE){
				return 1;
			}
			
			return -1;			
		}
		
		if(type1 == Type.VALUE){
			return edge1.toString().compareTo(edge2.toString());	
		}
		else{
			PredicateEdge pe1 = edge1.getPredicateEdge();
			if(pe1.isLesserThanThreshold()){
				return -1;
			}
			return 1;			
		}
	}
}