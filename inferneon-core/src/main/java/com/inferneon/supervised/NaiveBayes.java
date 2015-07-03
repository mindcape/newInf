package com.inferneon.supervised;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.inferneon.core.Attribute;
import com.inferneon.core.IInstances;
import com.inferneon.core.Instance;
import com.inferneon.core.Instances;
import com.inferneon.core.Value;

public class NaiveBayes extends Supervised{

	private IInstances instances;
	private List<Attribute> attributes;

	private Map<Value, Double> classProbablities;
	private List<Map<Value, Map<Value, Double>>> attributeClassConditionalProbablities;


	public NaiveBayes(){
		attributeClassConditionalProbablities = new ArrayList<>();	
		classProbablities = new HashMap<>();
	}

	@Override
	public void train(IInstances instances){
		try{
			this.instances = instances;
			this.attributes = instances.getAttributes();

			FrequencyCounts frequencyCounts = instances.getFrequencyCounts();

			Map<Value, Double> targetClassCounts = frequencyCounts.getTargetCounts();

			setClassProbablities(targetClassCounts);

			List<Map<Value, Map<Value, Double>>>  valueAndTargetClassCountList = frequencyCounts.getValueAndTargetClassCount();

			for(Map<Value, Map<Value, Double>> valueAndTargetCounts : valueAndTargetClassCountList){
				Map<Value, Map<Value, Double>> ccps =  getClassConditionalProbablitiesForAttribute(targetClassCounts, valueAndTargetCounts);
				attributeClassConditionalProbablities.add(ccps);
			}
		}
		catch(Exception e){
			// TODO Log here
			e.printStackTrace();
		}
	}

	private Map<Value, Map<Value, Double>> getClassConditionalProbablitiesForAttribute(
			Map<Value, Double> targetClassCounts, Map<Value, Map<Value, Double>> valueAndTargetClassCount) {

		Map<Value, Map<Value, Double>> ccps = new HashMap<>();

		Iterator<Map.Entry<Value,Map<Value,Double>>> iterator = valueAndTargetClassCount.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<Value, Map<Value, Double>> entry = iterator.next();
			Value value = entry.getKey();			 
			Map<Value,Double> classCounts = entry.getValue();

			Set<Value> targetVals = classCounts.keySet();
			Iterator<Value> iter = targetVals.iterator();

			Map<Value, Double> targetClassPrs = new HashMap<>();

			while(iter.hasNext()){
				Value targetVal = iter.next();
				Double count = classCounts.get(targetVal);

				Double totalOccurrencesOfTarget = targetClassCounts.get(targetVal);

				Double pr = (double) count / (double) totalOccurrencesOfTarget;				 
				targetClassPrs.put(targetVal, pr);				 
			}

			ccps.put(value, targetClassPrs);
		}		 

		return ccps;
	}

	private void setClassProbablities(Map<Value, Double> targetClassCounts) {
		Iterator<Map.Entry<Value,Double>>  iter = targetClassCounts.entrySet().iterator();
		while(iter.hasNext()){
			Entry<Value, Double> entry = iter.next();
			Value targetVal = entry.getKey();
			Double count = entry.getValue();

			Double pr = (double) count / (double) instances.size();				 
			classProbablities.put(targetVal, pr);				 
		}
	}

	@Override
	public Value classify(Instance instance) {
		int numAttributes = attributes.size();

		Map<Value, List<Double>> targetClassValuesAndProbablities = new HashMap<>();

		for(int i = 0; i < numAttributes -1; i++){
			Value value = instance.getValue(i);

			Map<Value, Map<Value, Double>> ccpsForAttribute = attributeClassConditionalProbablities.get(i);
			Map<Value, Double> ccps = ccpsForAttribute.get(value);
			if(ccps == null){
				// TODO: Handle this. This value was never seen during training?
			}
			else{

				Iterator<Entry<Value, Double>> iterator = ccps.entrySet().iterator();
				while(iterator.hasNext()){
					Entry<Value, Double> entry = iterator.next();
					Value targetClassValue = entry.getKey();
					Double conditionalProbablity = entry.getValue();

					List<Double> prs = targetClassValuesAndProbablities.get(targetClassValue);
					if(prs == null){
						prs = new ArrayList<>();
						targetClassValuesAndProbablities.put(targetClassValue, prs);
					}
					prs.add(conditionalProbablity);
				}				
			}			
		}

		return targetByNaiveBayesComputation(targetClassValuesAndProbablities);		
	}

	private Value targetByNaiveBayesComputation(Map<Value, List<Double>> targetClassValuesAndProbablities) {

		Value targetValueWithMaxPr = null;
		Double maxPr = null;

		Iterator<Entry<Value, List<Double>>> iterator = targetClassValuesAndProbablities.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<Value, List<Double>> entry = iterator.next();
			Value targetClassValue = entry.getKey();
			List<Double> conditionalProbablities = entry.getValue();

			Double targetClassPriorProbablity = classProbablities.get(targetClassValue);

			Double result = targetClassPriorProbablity;
			for(Double cp : conditionalProbablities){
				result *= cp;
			}

			if(targetValueWithMaxPr == null){
				targetValueWithMaxPr = targetClassValue;
				maxPr = result;
			}
			else{
				if(Double.compare(result, maxPr) > 0){
					maxPr = result;
					targetValueWithMaxPr = targetClassValue;
				}
			}
		}

		return targetValueWithMaxPr;		
	}
}
