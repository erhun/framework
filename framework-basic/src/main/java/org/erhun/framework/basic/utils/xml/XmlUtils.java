package org.erhun.framework.basic.utils.xml;


import org.erhun.framework.basic.utils.string.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.StringReader;
import java.util.*;

/**
 * 
 * @author weichao <gorilla@aliyun.com>
 * @date 2016-1-9
 */
@SuppressWarnings("rawtypes")
public class XmlUtils {


	/**
	 *
	 * @param content
	 * @param nodePathExpressions
	 * @return
	 */
	public static Map parseToMap(String content, String ...nodePathExpressions){

		return parseToMap(content, false, nodePathExpressions);
		
	}

	/**
	 *
	 * @param content
	 * @param hasChildren
	 * @param nodePathExpressions
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map parseToMap(String content, boolean hasChildren, String ...nodePathExpressions){

		try{
			Document document = createDocument(content);
			Map returnMap = new HashMap ();
			for (String path : nodePathExpressions) {
				List <Element> selectedNodes = document.selectNodes(path);
				if(selectedNodes.size()>1){
					returnMap.put(selectedNodes.get(0).getName(), parseToList(selectedNodes));
				}else{
					transferToMap(returnMap, selectedNodes, hasChildren);
				}
			}
			return returnMap;
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
		
	}

	/**
	 *
	 * @param content
	 * @param nodePathExpression
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List parseToList(String content, String nodePathExpression){

		try {
			Document document = createDocument(content);
			List <Element> selectedNodes = document.selectNodes(nodePathExpression);
			return parseToList(selectedNodes);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static List parseToList(List <Element> selectedNodes) throws DocumentException {

		if(!selectedNodes.isEmpty()){
			List returnNodes = new ArrayList(selectedNodes.size());
			Element el = null;
			Iterator <Element> iter = selectedNodes.iterator();
			while (iter.hasNext()) {
				el = iter.next();
				returnNodes.add(transferToMap(new HashMap(), el, true));
			}
			return returnNodes;
		}
		
		return Collections.emptyList();
	}

	/**
	 *
	 * @param content
	 * @return
	 */
	public static Map <String, String> parseToMap(String content){

		Map <String, String> returnMap = new HashMap <String, String> ();
		
		try{
			Document document = createDocument(content);
			parseToMap(returnMap, document.getRootElement());
		}catch(DocumentException ex){
			ex.printStackTrace();
		}
		
		return returnMap;
		
	}

	/**
	 *
	 * @param content
	 * @return
	 * @throws DocumentException
	 */
	public static Document createDocument(String content) throws DocumentException {

		Document document = new SAXReader().read(new StringReader(content));
		
		return document;
		
	}

	/**
	 *
	 * @param returnMap
	 * @param element
	 * @return
	 * @throws DocumentException
	 */
	public static Map <String, String> parseToMap(Map <String, String> returnMap, Element element) throws DocumentException {

		
		Iterator <?> iter = element.elements().iterator();
		
		while(iter.hasNext()){
			Element el = (Element) iter.next();
			if(!el.isTextOnly()){
				parseToMap(returnMap, el);
			}else{
				parseNode(returnMap, el);
			}
		}
		
		parseNode(returnMap, element);
		
		return returnMap;
		
	}

	/**
	 * 
	 * @param nodes
	 * @return
	 */
	private static Map<String, String> transferToMap(Map <String, String> result, List nodes, boolean hasChildren) {
		
		if(nodes == null || nodes.size() == 0){
			return null;
		}
		
		Iterator iter = nodes.iterator();
		
		while(iter.hasNext()) {
			Element node = (Element) iter.next();
			if(hasChildren && !node.isTextOnly()){
				transferToMap(result, node.elements(), true);
			}else{
				parseNode(result, node);
			}
			
		}
		
		return result;
		
	}

	/**
	 *
	 * @param result
	 * @param element
	 * @param hasChildren
	 * @return
	 */
	private static Map<String, String> transferToMap(Map <String, String> result, Element element, boolean hasChildren) {
		
		if(!element.isTextOnly()){
			return transferToMap(result, element.elements(), hasChildren);
		}
		
		parseNode(result, element);
		
		return result;
		
	}

	private static void parseNode(Map<String, String> result, Element element) {
		if(element.attributeCount() > 0){
			Iterator <?> attrIter = element.attributeIterator();
			while(attrIter.hasNext()){
				Attribute attr = (Attribute) attrIter.next();
				result.put(attr.getName(), attr.getValue() != null ? attr.getValue().trim() : null);
			}
		}
		
		if(StringUtils.isNotBlank(element.getText())){
			result.put(element.getName(), element.getTextTrim());
		}
	}
	
	/**
	 * 
	 * @param content
	 * @param nodePathExpression
	 * @return
	 * @throws DocumentException
	 */
	public static String singleValue(String content, String nodePathExpression) {

		Document document;
		
		try {
			document = new SAXReader().read(new StringReader(content));
			List selectedNodes = document.selectNodes(nodePathExpression);
			if(!selectedNodes.isEmpty()){
				return ((Element)selectedNodes.get(0)).getTextTrim();
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		
		return null;
		
	}
	
	public static void main(String[] args) {
		
		try {
			
			String a  = "<root><status taskid=\"1707071925454569880\" code=\"0\" message=\"success\" time=\"2017-07-07 19:26:57\" linkid=\"F2017070719254201034476\"/><status taskid=\"1707071926154569950\" code=\"0\" message=\"success\" time=\"2017-07-07 19:26:58\" linkid=\"F2017070719261386234639\"/></root>";
			
			
			System.out.println(XmlUtils.parseToMap(a, "/root/status"));
			

			//System.out.println(parseToMap("<?xml version=\"1.0\" encoding=\"UTF-8\" ?><body><code><a>f</a></code><code><a>c</a></code></body>", "/body/code/a"));
			//System.out.println(parseToMap("<?xml version=\"1.0\" encoding=\"utf-8\"?><root return=\"1002012\" info=\"包规格参数错误:yd.2M\" taskid=\"1706211328108316300\" linkid=\"F2017062113265116225614\"/>"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
