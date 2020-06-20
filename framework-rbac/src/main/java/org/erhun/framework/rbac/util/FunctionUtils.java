package org.erhun.framework.rbac.util;

import org.erhun.framework.basic.utils.ArrayUtils;
import org.erhun.framework.rbac.RbacFunction;

import java.util.List;

/**
 * 
 * @author weichao (gorilla@aliyun.com) 
 * Date: 2013-12-6
 * Time: 下午6:39:30
 */
public class FunctionUtils {

	
	public static boolean isCreatable(List <RbacFunction> list ){
		
		if(list != null && !list.isEmpty()){
			for ( RbacFunction f : list ) {
				if( "new".equals(f.getCode()) ){
					return true;
				}
			}
		}
		
		return false;
		
	}
	
	public static boolean isEditable(List <RbacFunction> list ){
		
		if(list != null && !list.isEmpty()){
			for ( RbacFunction f : list ) {
				if( "edit".equals(f.getCode()) ){
					return true;
				}
			}
		}
		
		return false;
		
	}
	
	public static boolean isRemoveable(List <RbacFunction> list ){
		
		if(list != null && !list.isEmpty()){
			for ( RbacFunction f : list ) {
				if( "remove".equals(f.getCode()) ){
					return true;
				}
			}
		}

		return false;
		
	}
	
	public static boolean hasFunctions(List <RbacFunction> list, String funCode ){
		
		if(list != null && !list.isEmpty()){
			if(funCode.indexOf("-") > -1){
				for (RbacFunction fun : list) {
					if(funCode.startsWith(fun.getCode())){
						return true;
					}
				}
			}else{
				for (RbacFunction fun : list) {
					if(funCode.equals(fun.getCode())){
						return true;
					}
				}
			}
		}
		
		return false;
		
	}
	
	public static boolean hasFunctions(List <RbacFunction> list, String... funCode ){
		
		if(list != null && !list.isEmpty()){
			for (RbacFunction fun : list) {
				if(ArrayUtils.exists(funCode, fun.getCode())){
					return true;
				}
			}
		}
		
		return false;
		
	}
	
	/*public static List <Function> getCreateFunctions(List <Function> list ){
		
		List <Function> returnList = null;
		
		if(list != null && !list.isEmpty()){
			for (Function fun : list) {
				if("2".equals(fun.getShowMode()) || "4".equals(fun.getShowMode()) || "5".equals(fun.getShowMode()) || "7".equals(fun.getShowMode())){
					if(returnList == null){
						returnList = new ArrayList <Function>();
					}
					returnList.add(fun);
				}
			}
		}
		
		if(returnList == null){
			return Collections.emptyList();
		}
		
		return returnList;
		
	}
	
	public static List <Function> getEditFunctions(List <Function> list ){
		
		List <Function> returnList = null;

		if(list != null && !list.isEmpty()){
			for (Function fun : list) {
				if("3".equals(fun.getShowMode()) || "4".equals(fun.getShowMode()) || "6".equals(fun.getShowMode()) || "7".equals(fun.getShowMode())){
					if(returnList == null){
						returnList = new ArrayList <Function>();
					}
					returnList.add(fun);
				}
			}
		}
		
		if(returnList == null){
			return Collections.emptyList();
		}
		
		return returnList;
		
	}
	
	public static List <Function> getListFunctions(List <Function> list ){
		
		List <Function> returnList = null;
		
		if(list != null && !list.isEmpty()){
			for (Function fun : list) {
				if("1".equals(fun.getShowMode()) || "5".equals(fun.getShowMode()) || "6".equals(fun.getShowMode()) || "7".equals(fun.getShowMode())){
					if(returnList == null){
						returnList = new ArrayList <Function>();
					}
					returnList.add(fun);
				}
			}
		}
		
		if(returnList == null){
			return Collections.emptyList();
		}
		
		return returnList;
		
	}*/
	
}
