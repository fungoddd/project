package com.fun.util.baidu;

import lombok.Data;

@Data
public class Distance {

	private String distanceText;
	
	private int distanceNum;
	
	private String durationText;
	
	private int durationNum;
	
	public Distance(){}
	
	public Distance(String distanceText,int distanceNum,String durationText,int durationNum){
		this.distanceText = distanceText;
		this.distanceNum = distanceNum;
		this.durationText = durationText;
		this.durationNum = durationNum;
	}


	
}
