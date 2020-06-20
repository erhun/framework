package org.erhun.framework.rbac.util;


import org.erhun.framework.rbac.entities.FunctionInfo;

import java.util.List;

/**
 * 
 * @author weichao (gorilla@aliyun.com)
 */
public class RBACUtils {

	public static boolean isCreatable( List <FunctionInfo> list ) {

		if(list != null){
			for( FunctionInfo f : list ){
				if("new".equals(f.getCode())){
					return true;
				}
			}
		}

		return false;

	}

	public static boolean isEditable( List <FunctionInfo> list ) {

		if(list != null){
			for( FunctionInfo f : list ){
				if("edit".equals(f)){
					return true;
				}
			}
		}

		return false;

	}

	public static boolean isRemoveable( List <FunctionInfo> list ) {

		if(list != null){
			for( FunctionInfo f : list ){
				if("remove".equals(f)){
					return true;
				}
			}
		}

		return false;

	}

}
