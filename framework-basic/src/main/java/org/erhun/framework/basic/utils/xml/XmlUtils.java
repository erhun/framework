package org.erhun.framework.basic.utils.xml;


import org.erhun.framework.basic.utils.collection.ListUtils;
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

		return parseToMap(content, (List) null, nodePathExpressions);

	}

	/**
	 *
	 * @param content
	 * @param listNodes
	 * @param nodePathExpressions
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map parseToMap(String content, List <String> listNodes, String ...nodePathExpressions){

		try{
			Document document = createDocument(content);
			Map returnMap = new HashMap ();
			if(nodePathExpressions != null && nodePathExpressions.length == 0){
				transferToMap(returnMap, document.getRootElement().elements(), listNodes);
			}else {
				for (String expr : nodePathExpressions) {
					String tmp[] = expr.split(":");
					String path = tmp[0];
					String type = null;
					if (tmp.length > 1) {
						type = tmp[1];
					}
					List<Element> selectedNodes = (List) document.selectNodes(path);
					if ("list".equals(type)) {
						returnMap.put(selectedNodes.get(0).getParent().getName(), parseToList(selectedNodes));
					} else {
						transferToMap(returnMap, selectedNodes, listNodes);
					}
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
			Element selectedNode = (Element) document.selectSingleNode(nodePathExpression);
			return parseToList(selectedNode.elements());
		} catch (DocumentException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static List parseToList(List <Element> selectedNodes) {
		return parseToList(selectedNodes, null);
	}

	public static List parseToList(List <Element> selectedNodes, List <String> listNodes) {

		if(!selectedNodes.isEmpty()){
			List returnNodes = new ArrayList(selectedNodes.size());
			Element el = null;
			Iterator <Element> iter = selectedNodes.iterator();
			while (iter.hasNext()) {
				el = iter.next();
				returnNodes.add(transferToMap(new HashMap(), el.elements(), listNodes));
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
	public static Map <String, Object> parseToMap(String content){

		Map <String, Object> returnMap = new HashMap <String, Object> ();

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
	public static Map <String, Object> parseToMap(Map <String, Object> returnMap, Element element) throws DocumentException {


		Iterator <?> iter = element.elements().iterator();

		while(iter.hasNext()){
			Element el = (Element) iter.next();
			if(!el.isTextOnly()){
				parseToMap(returnMap, el);
			}else{
				parseNodeToProperty(returnMap, el);
			}
		}

		parseNodeToProperty(returnMap, element);

		return returnMap;

	}

	/**
	 *
	 * @param nodes
	 * @return
	 */
	private static Map<String, Object> transferToMap(Map <String, Object> result, List nodes, List <String> listNodes) {

		if(nodes == null || nodes.size() == 0){
			return null;
		}

		Iterator iter = nodes.iterator();

		while(iter.hasNext()) {
			Element node = (Element) iter.next();
			if(!node.isTextOnly()){
				String parentPath = getParentPath(node, new StringBuilder());
				if(ListUtils.isNotEmpty(listNodes) && listNodes.contains(parentPath)){
					result.put(node.getName(), parseToList(node.elements(), listNodes));
				}else{
					result.put(node.getName(), transferToMap(new HashMap<>(), node.elements(), listNodes));
				}
			}else{
				parseNodeToProperty(result, node);
			}

		}

		return result;

	}

	private static String getParentPath(Element element, StringBuilder sb){

		sb.insert(0, "/" + element.getName());
		if(!element.isRootElement()){
			getParentPath(element.getParent(), sb);
		}
		return sb.toString();

	}

	/**
	 *
	 * @param result
	 * @param element
	 * @param listNodes
	 * @return
	 */
	private static Map<String, Object> transferToMap(Map <String, Object> result, Element element, List <String> listNodes) {

		if(!element.isTextOnly()){
			result.put(element.getName(), transferToMap(new HashMap<>(), element.elements(), listNodes));
			return result;

		}

		parseNodeToProperty(result, element);

		return result;

	}

	private static void parseNodeToProperty(Map<String, Object> result, Element element) {
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

			// String a  = "<root><status taskid=\"1707071925454569880\" code=\"0\" message=\"success\" time=\"2017-07-07 19:26:57\" linkid=\"F2017070719254201034476\"/><status taskid=\"1707071926154569950\" code=\"0\" message=\"success\" time=\"2017-07-07 19:26:58\" linkid=\"F2017070719261386234639\"/></root>";

			//String a  = "<root><city><name>成都</name><persons><person><name>john</name><attrs><attr><id>1</id></attr><attr><id>2</id></attr></attrs></person><person><name>jack</name><attrs><attr><id>1</id></attr><attr><id>2</id></attr></attrs></person></persons></city></root>";

			String a = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><response><head><method>icbcb2c.order.detail</method><req_sid>20200930030108054</req_sid><version>1.0</version><timestamp>2020-09-30 03:01:08</timestamp><app_key>JU8hHImt</app_key><auth_code>AedceAbyc2LJZkIozm9ZmNHBiVG1mHRv4FC31MOs0i7iYzsj6ktirAvADioslGAS</auth_code><ret_code>0</ret_code><ret_msg>OK</ret_msg><sign>tjYZU/kV9f2/cbr7adX11kegrBcHenjqcaYf6MIH758=</sign></head><body><order_list><order><order_id>0201030IM911327262</order_id><order_modify_time>2020-10-30 14:51:47</order_modify_time><order_status>01</order_status><order_buyer_remark></order_buyer_remark><order_seller_remark></order_seller_remark><merchant2member_remark></merchant2member_remark><order_buyer_id>A20200801011855620</order_buyer_id><order_buyer_username>ww18455551110</order_buyer_username><order_buyer_name></order_buyer_name><order_create_time>2020-10-30 14:51:44</order_create_time><order_amount>120</order_amount><order_credit_amount>0</order_credit_amount><order_coupon_amount>0</order_coupon_amount><order_ewelfare_discount>0</order_ewelfare_discount><credit_liquid_amount>0</credit_liquid_amount><order_other_discount>0</order_other_discount><order_ins_num>0</order_ins_num><ins_fee_mer>0</ins_fee_mer><order_flag_color></order_flag_color><order_channel>2</order_channel><products><product><product_id>0000029261</product_id><product_sku_id>90000000000010010146</product_sku_id><product_code>202007271519</product_code><product_name>谢裕大 黄山毛峰 特一级60克 明前茶 绿茶</product_name><product_number>2</product_number><product_price>50</product_price><product_discount>0</product_discount><refund_process>-</refund_process><refund_num>0</refund_num><product_prop_info>茗茶规格:50g-99g</product_prop_info><tringproducts><tringproduct><product_id>0000029489</product_id><product_sku_id>00000000000000064079</product_sku_id><product_code></product_code><product_name>谢裕大黄山毛峰 高香古峰茶叶礼盒300g 绿茶 高山茶</product_name><product_number>2</product_number><product_price>10</product_price><refund_process>-</refund_process><refund_num>0</refund_num><product_prop_info></product_prop_info></tringproduct></tringproducts><giftproducts/></product></products><invoice><invoice_type></invoice_type><invoice_title>无</invoice_title><invoice_content></invoice_content></invoice><payment><order_pay_time></order_pay_time><pay_custname></pay_custname><order_pay_amount>120</order_pay_amount><order_cash_amount>0</order_cash_amount><order_pay_sys>02</order_pay_sys><order_discount_amount>0</order_discount_amount><order_freight>0</order_freight><pay_serial></pay_serial><coupons/></payment><consignee><consignee_name>图图他</consignee_name><consignee_province>北京</consignee_province><consignee_city>北京市</consignee_city><consignee_district>市辖区</consignee_district><consignee_address>地对地导弹</consignee_address><consignee_zipcode>000000</consignee_zipcode><consignee_total_address>北京北京市市辖区地对地导弹(000000)</consignee_total_address><consignee_mobile>13088885555</consignee_mobile><consignee_phone></consignee_phone><consignee_province_id>119999</consignee_province_id><consignee_city_id>110000</consignee_city_id><consignee_district_id>110100</consignee_district_id><consignee_time>3</consignee_time><consignee_idcardnum></consignee_idcardnum><consignee_email></consignee_email><merDefined1></merDefined1><merDefined2></merDefined2><merDefined3></merDefined3></consignee><discounts><discount><discount_type>13</discount_type><discount_amount>30</discount_amount></discount></discounts><delivery_id></delivery_id><is_ref_suspicious>0</is_ref_suspicious></order></order_list></body></response>";
			System.out.println(XmlUtils.parseToMap(a, Arrays.asList("/response/body/order_list", "/response/body/order_list/order/products", "/response/body/order_list/order/products/product/tringproducts")));


			//System.out.println(parseToMap("<?xml version=\"1.0\" encoding=\"UTF-8\" ?><body><code><a>f</a></code><code><a>c</a></code></body>", "/body/code/a"));
			//System.out.println(parseToMap("<?xml version=\"1.0\" encoding=\"utf-8\"?><root return=\"1002012\" info=\"包规格参数错误:yd.2M\" taskid=\"1706211328108316300\" linkid=\"F2017062113265116225614\"/>"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
