package org.erhun.framework.basic.utils;

import org.erhun.framework.basic.utils.string.StringPool;
import org.erhun.framework.basic.utils.string.StringUtils;

import java.util.Map;

/**
 *
 * @author weichao <gorilla@aliyun.com>
 * @date 2017年5月4日
 */
public class MapUtils {


	/**
	 *
	 * @param mp
	 * @return
	 */
	public static String toString( Map <?,?> mp ){

		if( mp != null ){
			StringBuilder sb = new StringBuilder();
			for( Map.Entry <?,?> en : mp.entrySet() ){
				if( sb.length() > 0 ){
					sb.append( ";" );
				}
				sb.append( en.getKey() ).append( ":" ).append( en.getValue() );
			}
			return sb.toString();
		}

		return StringPool.EMPTY;

	}

	public static boolean isEmpty( Map <?, ?> map ) {
		return map == null || map.isEmpty();
	}

	public static boolean isNotEmpty( Map <?, ?> map ) {
		return !isEmpty(map);
	}

	/**
	 * 是否包含空值
	 * @param mp
	 * @return
	 */
	public static boolean isEmptyValue(Map <?,?> mp){

		if(isEmpty(mp)){
			return true;
		}

		for(Map.Entry<?, ?> entry : mp.entrySet()){
			Object obj = entry.getValue();
			if(obj == null || ((obj instanceof String) && StringUtils.isBlank((String)obj))){
				return true;
			}
		}

		return false;

	}
}
