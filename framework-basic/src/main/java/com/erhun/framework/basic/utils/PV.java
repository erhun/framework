package com.erhun.framework.basic.utils;

/**
 * 
 * @author weichao<gorilla@aliyun.com>
 *
 */
public final class PV {
	
	private static final Object[] NONE = new Object[0];
	
	public final String name;

	public Object value;
	
	private PV(String n, Object v) {
		this.name = n;
		this.value = v;
	}

	/**
	 *
	 * @param n key
	 * @param v value
	 * @return
	 */
	public static final PV nv(String n, Object v) {
		return new PV(n, v);
	}


	public static final Object[] none() {
		return NONE;
	}
	
	public static final PV[] of(Object... values) {
	    
	    if(values.length % 2 != 0){
	        throw new IllegalArgumentException("parameter 'values' require of key pair");
	    }
	    
	    int size = values.length/2;
	    
	    PV [] pvs = new PV[size];
	    
	    for(int i=0;i<size;i++){
	        PV pv = new PV((String)values[i*2], values[i*2+1]);
	        pvs[i] = pv;
	    }
	    
		return pvs;
	}
	
	public static final <T> T as(Object... values) {
        return (T) values;
    }

	public static final <T> T as(String... values) {
		return (T) values;
	}
}
