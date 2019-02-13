package com.fun.util.baidu;

import lombok.Data;

@Data
public class Point {

	private double lng;
	
	private double lat;
	
	private String address;
	
	public Point(){}
	
	public Point(double lat,double lng,String address){
		this.lat = lat;
		this.lng = lng;
		this.address = address;
	}


	
}
