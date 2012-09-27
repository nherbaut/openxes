/*
 * OpenXES
 * 
 * The reference implementation of the XES meta-model for event 
 * log data management.
 * 
 * Copyright (c) 2012 Christian W. Guenther (christian@deckfour.org)
 * 
 * 
 * LICENSE:
 * 
 * This code is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
 * 
 * EXEMPTION:
 * 
 * The use of this software can also be conditionally licensed for
 * other programs, which do not satisfy the specified conditions. This
 * requires an exemption from the general license, which may be
 * granted on a per-case basis.
 * 
 * If you want to license the use of this software with a program
 * incompatible with the LGPL, please contact the author for an
 * exemption at the following email address: 
 * christian@deckfour.org
 * 
 */
package org.deckfour.xes.extension.std;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.info.XGlobalAttributeNameMap;
import org.deckfour.xes.model.XAttributable;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeContinuous;
import org.deckfour.xes.model.XAttributeLiteral;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;

/**
 * This extension provides costs for traces and events.
 * 
 * It defines five attributes:
 * 
 * - cost:total: Contains total cost incurred for a trace or an event. The value
 * represents the sum of all the cost amounts within the element. -
 * cost:currecny: Any valid currency format.
 * 
 * - cost:amount: The value contains the cost amount for a cost driver.
 * 
 * - cost:driver: The value contains the id for the cost driver used to
 * calculate the cost.
 * 
 * - cost:type: The value contains the cost type (e.g., Fixed, Overhead,
 * Materials).
 * 
 * @author H.M.W. Verbeek (h.m.w.verbeek@tue.nl)
 * 
 */
public class XCostExtension extends XExtension {

	/**
	 * 
	 */
	private static final long serialVersionUID = -198168699309921320L;

	/**
	 * Unique URI of this extension.
	 */
	public static final URI EXTENSION_URI = URI
			.create("http://www.xes-standard.org/cost.xesext");
	/**
	 * Keys for the attributes.
	 */
	public static final String KEY_TOTAL = "cost:total";
	public static final String KEY_CURRENCY = "cost:currency";
	public static final String KEY_AMOUNT = "cost:amount";
	public static final String KEY_DRIVER = "cost:driver";
	public static final String KEY_TYPE = "cost:type";
	/**
	 * Attribute prototypes
	 */
	public static XAttributeContinuous ATTR_TOTAL;
	public static XAttributeLiteral ATTR_CURRENCY;
	public static XAttributeContinuous ATTR_AMOUNT;
	public static XAttributeLiteral ATTR_DRIVER;
	public static XAttributeLiteral ATTR_TYPE;

	/**
	 * Singleton instance of this extension.
	 */
	private transient static XCostExtension singleton = new XCostExtension();

	/**
	 * Provides access to the singleton instance.
	 * 
	 * @return Singleton extension.
	 */
	public static XCostExtension instance() {
		return singleton;
	}

	private Object readResolve() {
		return singleton;
	}

	/**
	 * Private constructor
	 */
	private XCostExtension() {
		super("Cost", "cost", EXTENSION_URI);
		XFactory factory = XFactoryRegistry.instance().currentDefault();
		ATTR_TOTAL = factory.createAttributeContinuous(KEY_TOTAL, 0.0, this);
		ATTR_CURRENCY = factory.createAttributeLiteral(KEY_CURRENCY,
				"__INVALID__", this);
		ATTR_AMOUNT = factory.createAttributeContinuous(KEY_AMOUNT, 0.0, this);
		ATTR_DRIVER = factory.createAttributeLiteral(KEY_DRIVER, "__INVALID__",
				this);
		ATTR_TYPE = factory.createAttributeLiteral(KEY_TYPE, "__INVALID__",
				this);
		this.traceAttributes.add((XAttribute) ATTR_TOTAL.clone());
		this.traceAttributes.add((XAttribute) ATTR_CURRENCY.clone());
		this.eventAttributes.add((XAttribute) ATTR_TOTAL.clone());
		this.eventAttributes.add((XAttribute) ATTR_CURRENCY.clone());
		this.eventAttributes.add((XAttribute) ATTR_AMOUNT.clone());
		this.eventAttributes.add((XAttribute) ATTR_DRIVER.clone());
		this.eventAttributes.add((XAttribute) ATTR_TYPE.clone());
		// register aliases
		XGlobalAttributeNameMap.instance().registerMapping(
				XGlobalAttributeNameMap.MAPPING_ENGLISH, KEY_TOTAL,
				"Total Cost");
		XGlobalAttributeNameMap.instance().registerMapping(
				XGlobalAttributeNameMap.MAPPING_ENGLISH, KEY_CURRENCY,
				"Currency of Cost");
		XGlobalAttributeNameMap.instance().registerMapping(
				XGlobalAttributeNameMap.MAPPING_ENGLISH, KEY_AMOUNT,
				"Cost Amount");
		XGlobalAttributeNameMap.instance().registerMapping(
				XGlobalAttributeNameMap.MAPPING_ENGLISH, KEY_DRIVER,
				"Cost Driver");
		XGlobalAttributeNameMap.instance().registerMapping(
				XGlobalAttributeNameMap.MAPPING_ENGLISH, KEY_TYPE, "Cost Type");
	}

	/**
	 * Retrieves the total costs of a trace, if set by this extension's total
	 * attribute.
	 * 
	 * @param trace
	 *            Trace to retrieve total costs for.
	 * @return The requested total costs.
	 */
	public Double extractTotal(XTrace trace) {
		return extractTotalPrivate(trace);
	}

	/**
	 * Retrieves the total costs of an event, if set by this extension's total
	 * attribute.
	 * 
	 * @param event
	 *            Event to retrieve total costs for.
	 * @return The requested total costs.
	 */
	public Double extractTotal(XEvent event) {
		return extractTotalPrivate(event);
	}

	/*
	 * Retrieves the total costs of an element, if set by this extension's total
	 * attribute.
	 * 
	 * @param element Event to retrieve total costs for.
	 * 
	 * @return The requested total costs.
	 */
	private Double extractTotalPrivate(XAttributable element) {
		XAttribute attribute = element.getAttributes().get(KEY_TOTAL);
		if (attribute == null) {
			return null;
		} else {
			return ((XAttributeContinuous) attribute).getValue();
		}
	}

	/**
	 * Assigns any trace its total costs, as defined by this extension's total
	 * attribute.
	 * 
	 * @param trace
	 *            Trace to assign total costs to.
	 * @param total
	 *            The total costs to be assigned.
	 */
	public void assignTotal(XTrace trace, Double total) {
		assignTotalPrivate(trace, total);
	}

	/**
	 * Assigns any event its total costs, as defined by this extension's total
	 * attribute.
	 * 
	 * @param event
	 *            Event to assign total costs to.
	 * @param total
	 *            The total costs to be assigned.
	 */
	public void assignTotal(XEvent event, Double total) {
		assignTotalPrivate(event, total);
	}

	/*
	 * Assigns any element its total costs, as defined by this extension's total
	 * attribute.
	 * 
	 * @param element Element to assign total costs to.
	 * 
	 * @param total The total costs to be assigned.
	 */
	private void assignTotalPrivate(XAttributable element, Double total) {
		if (total != null && total > 0.0) {
			XAttributeContinuous attr = (XAttributeContinuous) ATTR_TOTAL
					.clone();
			attr.setValue(total);
			element.getAttributes().put(KEY_TOTAL, attr);
		}
	}

	/**
	 * Retrieves the cost currency for a trace, if set by this extension's
	 * currency attribute.
	 * 
	 * @param trace
	 *            Trace to retrieve currency for.
	 * @return The requested cost currency.
	 */
	public String extractCurrency(XTrace trace) {
		return extractCurrencyPrivate(trace);
	}

	/**
	 * Retrieves the cost currency for an event, if set by this extension's
	 * currency attribute.
	 * 
	 * @param event
	 *            Event to retrieve currency for.
	 * @return The requested cost currency.
	 */
	public String extractCurrency(XEvent event) {
		return extractCurrencyPrivate(event);
	}

	/*
	 * Retrieves the cost currency for an element, if set by this extension's
	 * currency attribute.
	 * 
	 * @param event Element to retrieve currency for.
	 * 
	 * @return The requested cost currency.
	 */
	private String extractCurrencyPrivate(XAttributable element) {
		XAttribute attribute = element.getAttributes().get(KEY_CURRENCY);
		if (attribute == null) {
			return null;
		} else {
			return ((XAttributeLiteral) attribute).getValue();
		}
	}

	/**
	 * Assigns any trace its cost currency, as defined by this extension's
	 * currency attribute.
	 * 
	 * @param trace
	 *            Trace to assign cost currency to.
	 * @param currency
	 *            The currency to be assigned.
	 */
	public void assignCurrency(XTrace trace, String currency) {
		assignCurrencyPrivate(trace, currency);
	}

	/**
	 * Assigns any event its cost currency, as defined by this extension's
	 * currency attribute.
	 * 
	 * @param event
	 *            Event to assign cost currency to.
	 * @param currency
	 *            The currency to be assigned.
	 */
	public void assignCurrency(XEvent event, String currency) {
		assignCurrencyPrivate(event, currency);
	}

	/*
	 * Assigns any element its cost currency, as defined by this extension's
	 * currency attribute.
	 * 
	 * @param element Element to assign cost currency to.
	 * 
	 * @param total The currency to be assigned.
	 */
	private void assignCurrencyPrivate(XAttributable element, String currency) {
		if (currency != null && currency.trim().length() > 0) {
			XAttributeLiteral attr = (XAttributeLiteral) ATTR_CURRENCY.clone();
			attr.setValue(currency);
			element.getAttributes().put(KEY_CURRENCY, attr);
		}
	}

	/**
	 * Retrieves the cost amount for an attribute, if set by this extension's
	 * amount attribute.
	 * 
	 * @param attribute
	 *            Attribute element to retrieve cost amount for.
	 * @return The requested cost amount.
	 */
	public Double extractAmount(XAttribute attribute) {
		XAttribute attr = attribute.getAttributes().get(KEY_AMOUNT);
		if (attr == null) {
			return null;
		} else {
			return ((XAttributeContinuous) attr).getValue();
		}
	}

	/**
	 * Retrieves a map containing all cost amounts for all descending attributes
	 * of a trace.
	 * 
	 * For example, the XES fragment:
	 * 
	 * <pre>
	 * <trace>
	 *     <string key="a" value="">
	 *         <float key="cost:amount" value="10.00"/>
	 *         <string key="b" value="">
	 *         	  <float key="cost:amount" value="20.00"/>
	 *         </string>
	 *         <string key="c" value="">
	 *         	  <float key="cost:amount" value="30.00"/>
	 *         </string>
	 *     </string>
	 *     <string key="b" value="">
	 *        <float key="cost:amount" value="15.00"/>
	 *     </string>
	 *     <string key="c" value="">
	 *        <float key="cost:amount" value="25.00"/>
	 *     </string>
	 * </trace>
	 * </pre>
	 * 
	 * should result into the following:
	 * 
	 * <pre>
	 * [[[a] 10.00] [[a b] 20.00] [[a c] 30.00] [[b] 15.00] [[c] 25.00]]
	 * </pre>
	 * 
	 * @param trace
	 *            Trace to retrieve all cost amounts for.
	 * @return Map from all descending keys to cost amounts.
	 */
	public Map<List<String>, Double> extractAmounts(XTrace trace) {
		return extractAmountsPrivate(trace);
	}

	/**
	 * Retrieves a map containing all cost amounts for all descending attributes
	 * of an event.
	 * 
	 * For example, the XES fragment:
	 * 
	 * <pre>
	 * <event>
	 *     <string key="a" value="">
	 *         <float key="cost:amount" value="10.00"/>
	 *         <string key="b" value="">
	 *         	  <float key="cost:amount" value="20.00"/>
	 *         </string>
	 *         <string key="c" value="">
	 *         	  <float key="cost:amount" value="30.00"/>
	 *         </string>
	 *     </string>
	 *     <string key="b" value="">
	 *        <float key="cost:amount" value="15.00"/>
	 *     </string>
	 *     <string key="c" value="">
	 *        <float key="cost:amount" value="25.00"/>
	 *     </string>
	 * </event>
	 * </pre>
	 * 
	 * should result into the following:
	 * 
	 * <pre>
	 * [[[a] 10.00] [[a b] 20.00] [[a c] 30.00] [[b] 15.00] [[c] 25.00]]
	 * </pre>
	 * 
	 * @param event
	 *            Event to retrieve all cost amounts for.
	 * @return Map from all descending keys to cost amounts.
	 */
	public Map<List<String>, Double> extractAmounts(XEvent event) {
		return extractAmountsPrivate(event);
	}

	/*
	 * Retrieves a map containing all cost amounts for all descending attributes
	 * of an element.
	 * 
	 * @param element Element to retrieve all cost amounts for.
	 * 
	 * @return Map from all descending keys to cost amounts.
	 */
	private Map<List<String>, Double> extractAmountsPrivate(
			XAttributable element) {
		Map<List<String>, Double> amounts = new HashMap<List<String>, Double>();
		for (XAttribute attr : element.getAttributes().values()) {
			List<String> keys = new ArrayList<String>();
			keys.add(attr.getKey());
			extractAmountsPrivate(attr, amounts, keys);
		}
		return amounts;
	}

	/*
	 * Fills a map with all cost amounts for all descending attributes of an
	 * element.
	 * 
	 * @param element Element to retrieve all cost amounts for.
	 * 
	 * @param amounts Map with cost amounts found so far.
	 */
	private void extractAmountsPrivate(XAttribute element,
			Map<List<String>, Double> amounts, List<String> keys) {
		Double amount = extractAmount(element);
		if (amount != null) {
			amounts.put(keys, amount);
		}
		for (XAttribute attr : element.getAttributes().values()) {
			List<String> newKeys = new ArrayList<String>(keys);
			newKeys.add(element.getKey());
			extractAmountsPrivate(attr, amounts, newKeys);
		}
	}

	/**
	 * Assigns any attribute its cost amount, as defined by this extension's
	 * amount attribute.
	 * 
	 * @param attribute
	 *            Attribute to assign cost amount to.
	 * @param amount
	 *            The cost amount to be assigned.
	 */
	public void assignAmount(XAttribute attribute, Double amount) {
		if (amount != null && amount > 0.0) {
			XAttributeContinuous attr = (XAttributeContinuous) ATTR_AMOUNT
					.clone();
			attr.setValue(amount);
			attribute.getAttributes().put(KEY_AMOUNT, attr);
		}
	}

	/**
	 * Assigns (to the given trace) multiple amounts given their key lists. The
	 * i-th element in the key list should correspond to an i-level attribute
	 * with the prescribed key. Note that as a side effect this method creates
	 * attributes when it does not find an attribute with the proper key.
	 * 
	 * For example, the call:
	 * 
	 * <pre>
	 * assignAmounts(trace, [[[a] 10.00] [[a b] 20.00] [[a c] 30.00] [[b] 15.00] [[c] 25.00]])
	 * </pre>
	 * 
	 * should result into the following XES fragment:
	 * 
	 * <pre>
	 * <trace>
	 *     <string key="a" value="">
	 *         <float key="cost:amount" value="10.00"/>
	 *         <string key="b" value="">
	 *         	  <float key="cost:amount" value="20.00"/>
	 *         </string>
	 *         <string key="c" value="">
	 *         	  <float key="cost:amount" value="30.00"/>
	 *         </string>
	 *     </string>
	 *     <string key="b" value="">
	 *        <float key="cost:amount" value="15.00"/>
	 *     </string>
	 *     <string key="c" value="">
	 *        <float key="cost:amount" value="25.00"/>
	 *     </string>
	 * </trace>
	 * </pre>
	 * 
	 * @param trace
	 *            Trace to assign the amounts to.
	 * @param amounts
	 *            Mapping from key lists to amounts which are to be assigned.
	 */
	public void assignAmounts(XTrace trace, Map<List<String>, Double> amounts) {
		assignAmountsPrivate(trace, amounts);
	}

	/**
	 * Assigns (to the given event) multiple amounts given their key lists. The
	 * i-th element in the key list should correspond to an i-level attribute
	 * with the prescribed key. Note that as a side effect this method creates
	 * attributes when it does not find an attribute with the proper key.
	 * 
	 * For example, the call:
	 * 
	 * <pre>
	 * assignAmounts(event, [[[a] 10.00] [[a b] 20.00] [[a c] 30.00] [[b] 15.00] [[c] 25.00]])
	 * </pre>
	 * 
	 * should result into the following XES fragment:
	 * 
	 * <pre>
	 * <event>
	 *     <string key="a" value="">
	 *         <float key="cost:amount" value="10.00"/>
	 *         <string key="b" value="">
	 *         	  <float key="cost:amount" value="20.00"/>
	 *         </string>
	 *         <string key="c" value="">
	 *         	  <float key="cost:amount" value="30.00"/>
	 *         </string>
	 *     </string>
	 *     <string key="b" value="">
	 *        <float key="cost:amount" value="15.00"/>
	 *     </string>
	 *     <string key="c" value="">
	 *        <float key="cost:amount" value="25.00"/>
	 *     </string>
	 * </event>
	 * </pre>
	 * 
	 * @param event
	 *            Event to assign the amounts to.
	 * @param amounts
	 *            Mapping from key lists to amounts which are to be assigned.
	 */
	public void assignAmounts(XEvent event, Map<List<String>, Double> amounts) {
		assignAmountsPrivate(event, amounts);
	}

	/*
	 * Assigns (to the given element) multiple amounts given their key lists.
	 * The i-th element in the key list should correspond to an i-level
	 * attribute with the prescribed key. Note that as a side effect this method
	 * creates attributes when it does not find an attribute with the proper
	 * key.
	 * 
	 * @param element Element to assign the amounts to.
	 * 
	 * @param amounts Mapping from key lists to amounts which are to be
	 * assigned.
	 */
	private void assignAmountsPrivate(XAttributable element,
			Map<List<String>, Double> amounts) {
		/*
		 * Add the proper amount for every key list.
		 */
		for (List<String> keys : amounts.keySet()) {
			assignAmountsPrivate(element, keys, amounts.get(keys));
		}
	}

	/*
	 * Assigns the given amount to the attribute that can be found through the
	 * given key list. The first key corresponds to the highest-level attribute,
	 * whereas the latest key corresponds to the lowest-level attribute.
	 */
	private void assignAmountsPrivate(XAttributable element, List<String> keys,
			Double amount) {
		if (keys.isEmpty()) {
			/*
			 * Key list is empty. Assign amount here if attribute. Else skip.
			 */
			if (element instanceof XAttribute) {
				assignAmount((XAttribute) element, amount);
			}
		} else {
			/*
			 * Key list not empty yet. Step down to the next attribute.
			 */
			String key = keys.get(0);
			List<String> keysTail = keys.subList(1, keys.size());
			XAttribute attr;
			if (element.getAttributes().containsKey(key)) {
				/*
				 * Attribute with given key already exists. Use it.
				 */
				attr = element.getAttributes().get(key);
			} else {
				/*
				 * Attribute with given key does not exist yet.
				 */
				attr = XFactoryRegistry.instance().currentDefault()
						.createAttributeLiteral(key, "", null);
				element.getAttributes().put(key, attr);
				/*
				 * Now it does.
				 */
			}
			/*
			 * Step down.
			 */
			assignAmountsPrivate(attr, keysTail, amount);
		}
	}

	/**
	 * Retrieves the cost driver for an attribute, if set by this extension's
	 * driver attribute.
	 * 
	 * @param attribute
	 *            Attribute element to retrieve cost driver for.
	 * @return The requested cost driver.
	 */
	public String extractDriver(XAttribute attribute) {
		XAttribute attr = attribute.getAttributes().get(KEY_DRIVER);
		if (attr == null) {
			return null;
		} else {
			return ((XAttributeLiteral) attr).getValue();
		}
	}

	/**
	 * Retrieves a map containing all cost drivers for all descending attributes
	 * of a trace.
	 * 
	 * @see #extractDrivers(XTrace)
	 * 
	 * @param trace
	 *            Trace to retrieve all cost drivers for.
	 * @return Map from all descending keys to cost drivers.
	 */
	public Map<List<String>, String> extractDrivers(XTrace trace) {
		return extractDriversPrivate(trace);
	}

	/**
	 * Retrieves a map containing all cost drivers for all descending attributes
	 * of an event.
	 * 
	 * @see #extractDrivers(XEvent)
	 * 
	 * @param event
	 *            Event to retrieve all cost drivers for.
	 * @return Map from all descending keys to cost drivers.
	 */
	public Map<List<String>, String> extractDrivers(XEvent event) {
		return extractDriversPrivate(event);
	}

	/*
	 * Retrieves a map containing all cost drivers for all descending attributes
	 * of an element.
	 * 
	 * @param element Element to retrieve all cost drivers for.
	 * 
	 * @return Map from all descending keys to cost drivers.
	 */
	private Map<List<String>, String> extractDriversPrivate(
			XAttributable element) {
		Map<List<String>, String> drivers = new HashMap<List<String>, String>();
		for (XAttribute attr : element.getAttributes().values()) {
			List<String> keys = new ArrayList<String>();
			keys.add(attr.getKey());
			extractDriversPrivate(attr, drivers, keys);
		}
		return drivers;
	}

	/*
	 * Fills a map with all cost drivers for all descending attributes of an
	 * element.
	 * 
	 * @param element Element to retrieve all cost drivers for.
	 * 
	 * @param amounts Map with cost drivers found so far.
	 */
	private void extractDriversPrivate(XAttribute element,
			Map<List<String>, String> drivers, List<String> keys) {
		String driver = extractDriver(element);
		if (driver != null) {
			drivers.put(keys, driver);
		}
		for (XAttribute attr : element.getAttributes().values()) {
			List<String> newKeys = new ArrayList<String>(keys);
			newKeys.add(element.getKey());
			extractDriversPrivate(attr, drivers, newKeys);
		}
	}

	/**
	 * Assigns any attribute its cost driver, as defined by this extension's
	 * driver attribute.
	 * 
	 * @param attribute
	 *            Attribute to assign cost driver to.
	 * @param driver
	 *            The cost driver to be assigned.
	 */
	public void assignDriver(XAttribute attribute, String driver) {
		if (driver != null && driver.trim().length() > 0) {
			XAttributeLiteral attr = (XAttributeLiteral) ATTR_DRIVER.clone();
			attr.setValue(driver);
			attribute.getAttributes().put(KEY_DRIVER, attr);
		}
	}

	/**
	 * Assigns (to the given trace) multiple cost drivers given their key lists.
	 * The i-th element in the key list should correspond to an i-level
	 * attribute with the prescribed key. Note that as a side effect this method
	 * creates attributes when it does not find an attribute with the proper
	 * key.
	 * 
	 * @see #assignAmounts(XTrace, Map)
	 * 
	 * @param trace
	 *            Trace to assign the cost drivers to.
	 * @param drivers
	 *            Mapping from key lists to cost drivers which are to be
	 *            assigned.
	 */
	public void assignDrivers(XTrace trace, Map<List<String>, String> drivers) {
		assignDriversPrivate(trace, drivers);
	}

	/**
	 * Assigns (to the given event) multiple cost drivers given their key lists.
	 * The i-th element in the key list should correspond to an i-level
	 * attribute with the prescribed key. Note that as a side effect this method
	 * creates attributes when it does not find an attribute with the proper
	 * key.
	 * 
	 * @see #assignAmounts(XEvent, Map)
	 * 
	 * @param event
	 *            Event to assign the cost drivers to.
	 * @param drivers
	 *            Mapping from key lists to cost drivers which are to be
	 *            assigned.
	 */
	public void assignDrivers(XEvent event, Map<List<String>, String> drivers) {
		assignDriversPrivate(event, drivers);
	}

	/*
	 * Assigns (to the given element) multiple cost drivers given their key
	 * lists. The i-th element in the key list should correspond to an i-level
	 * attribute with the prescribed key. Note that as a side effect this method
	 * creates attributes when it does not find an attribute with the proper
	 * key.
	 * 
	 * @param element Element to assign the cost drivers to.
	 * 
	 * @param drivers Mapping from key lists to cost drivers which are to be
	 * assigned.
	 */
	private void assignDriversPrivate(XAttributable element,
			Map<List<String>, String> drivers) {
		/*
		 * Add the proper cost driver for every key list.
		 */
		for (List<String> keys : drivers.keySet()) {
			assignDriversPrivate(element, keys, drivers.get(keys));
		}
	}

	/*
	 * Assigns the given cost driver to the attribute that can be found through
	 * the given key list. The first key corresponds to the highest-level
	 * attribute, whereas the latest key corresponds to the lowest-level
	 * attribute.
	 */
	private void assignDriversPrivate(XAttributable element, List<String> keys,
			String driver) {
		if (keys.isEmpty()) {
			/*
			 * Key list is empty. Assign cost driver here if attribute. Else
			 * skip.
			 */
			if (element instanceof XAttribute) {
				assignDriver((XAttribute) element, driver);
			}
		} else {
			/*
			 * Key list not empty yet. Step down to the next attribute.
			 */
			String key = keys.get(0);
			List<String> keysTail = keys.subList(1, keys.size());
			XAttribute attr;
			if (element.getAttributes().containsKey(key)) {
				/*
				 * Attribute with given key already exists. Use it.
				 */
				attr = element.getAttributes().get(key);
			} else {
				/*
				 * Attribute with given key does not exist yet.
				 */
				attr = XFactoryRegistry.instance().currentDefault()
						.createAttributeLiteral(key, "", null);
				element.getAttributes().put(key, attr);
				/*
				 * Now it does.
				 */
			}
			/*
			 * Step down.
			 */
			assignDriversPrivate(attr, keysTail, driver);
		}
	}

	/**
	 * Retrieves the cost type for an attribute, if set by this extension's type
	 * attribute.
	 * 
	 * @param attribute
	 *            Attribute element to retrieve cost type for.
	 * @return The requested cost type.
	 */
	public String extractType(XAttribute attribute) {
		XAttribute attr = attribute.getAttributes().get(KEY_TYPE);
		if (attr == null) {
			return null;
		} else {
			return ((XAttributeLiteral) attr).getValue();
		}
	}

	/**
	 * Retrieves a map containing all cost types for all descending attributes
	 * of a trace.
	 * 
	 * @see #extractAmounts(XTrace)
	 * 
	 * @param trace
	 *            Trace to retrieve all cost types for.
	 * @return Map from all descending keys to cost types.
	 */
	public Map<List<String>, String> extractTypes(XTrace trace) {
		return extractTypesPrivate(trace);
	}

	/**
	 * Retrieves a map containing all cost types for all descending attributes
	 * of an event.
	 * 
	 * @see #extractAmounts(XEvent)
	 * 
	 * @param event
	 *            Event to retrieve all cost types for.
	 * @return Map from all descending keys to cost types.
	 */
	public Map<List<String>, String> extractTypes(XEvent event) {
		return extractTypesPrivate(event);
	}

	/*
	 * Retrieves a map containing all cost types for all descending attributes
	 * of an element.
	 * 
	 * @param element Element to retrieve all cost types for.
	 * 
	 * @return Map from all descending keys to cost types.
	 */
	private Map<List<String>, String> extractTypesPrivate(XAttributable element) {
		Map<List<String>, String> types = new HashMap<List<String>, String>();
		for (XAttribute attr : element.getAttributes().values()) {
			List<String> keys = new ArrayList<String>();
			keys.add(attr.getKey());
			extractTypesPrivate(attr, types, keys);
		}
		return types;
	}

	/*
	 * Fills a map with all cost drivers for all descending attributes of an
	 * element.
	 * 
	 * @param element Element to retrieve all cost drivers for.
	 * 
	 * @param amounts Map with cost drivers found so far.
	 */
	private void extractTypesPrivate(XAttribute element,
			Map<List<String>, String> types, List<String> keys) {
		String type = extractDriver(element);
		if (type != null) {
			types.put(keys, type);
		}
		for (XAttribute attr : element.getAttributes().values()) {
			List<String> newKeys = new ArrayList<String>(keys);
			newKeys.add(element.getKey());
			extractTypesPrivate(attr, types, keys);
		}
	}

	/**
	 * Assigns any attribute its cost type, as defined by this extension's type
	 * attribute.
	 * 
	 * @param attribute
	 *            Attribute to assign cost type to.
	 * @param type
	 *            The cost type to be assigned.
	 */
	public void assignType(XAttribute attribute, String type) {
		if (type != null && type.trim().length() > 0) {
			XAttributeLiteral attr = (XAttributeLiteral) ATTR_TYPE.clone();
			attr.setValue(type);
			attribute.getAttributes().put(KEY_TYPE, attr);
		}
	}

	/**
	 * Assigns (to the given trace) multiple cost types given their key lists.
	 * The i-th element in the key list should correspond to an i-level
	 * attribute with the prescribed key. Note that as a side effect this method
	 * creates attributes when it does not find an attribute with the proper
	 * key.
	 * 
	 * @see #assignAmounts(XTrace, Map)
	 * 
	 * @param trace
	 *            Trace to assign the cost types to.
	 * @param types
	 *            Mapping from key lists to cost types which are to be assigned.
	 */
	public void assignTypes(XTrace trace, Map<List<String>, String> types) {
		assignDriversPrivate(trace, types);
	}

	/**
	 * Assigns (to the given event) multiple cost types given their key lists.
	 * The i-th element in the key list should correspond to an i-level
	 * attribute with the prescribed key. Note that as a side effect this method
	 * creates attributes when it does not find an attribute with the proper
	 * key.
	 * 
	 * @see #assignAmounts(XEvent, Map)
	 * 
	 * @param event
	 *            Event to assign the cost types to.
	 * @param types
	 *            Mapping from key lists to cost types which are to be assigned.
	 */
	public void assignTypes(XEvent event, Map<List<String>, String> types) {
		assignTypesPrivate(event, types);
	}

	/*
	 * Assigns (to the given element) multiple cost types given their key lists.
	 * The i-th element in the key list should correspond to an i-level
	 * attribute with the prescribed key. Note that as a side effect this method
	 * creates attributes when it does not find an attribute with the proper
	 * key.
	 * 
	 * @param element Element to assign the cost types to.
	 * 
	 * @param types Mapping from key lists to cost types which are to be
	 * assigned.
	 */
	private void assignTypesPrivate(XAttributable element,
			Map<List<String>, String> types) {
		/*
		 * Add the proper cost driver for every key list.
		 */
		for (List<String> keys : types.keySet()) {
			assignTypesPrivate(element, keys, types.get(keys));
		}
	}

	/*
	 * Assigns the given cost type to the attribute that can be found through
	 * the given key list. The first key corresponds to the highest-level
	 * attribute, whereas the latest key corresponds to the lowest-level
	 * attribute.
	 */
	private void assignTypesPrivate(XAttributable element, List<String> keys,
			String type) {
		if (keys.isEmpty()) {
			/*
			 * Key list is empty. Assign cost type here if attribute. Else skip.
			 */
			if (element instanceof XAttribute) {
				assignType((XAttribute) element, type);
			}
		} else {
			/*
			 * Key list not empty yet. Step down to the next attribute.
			 */
			String key = keys.get(0);
			List<String> keysTail = keys.subList(1, keys.size());
			XAttribute attr;
			if (element.getAttributes().containsKey(key)) {
				/*
				 * Attribute with given key already exists. Use it.
				 */
				attr = element.getAttributes().get(key);
			} else {
				/*
				 * Attribute with given key does not exist yet.
				 */
				attr = XFactoryRegistry.instance().currentDefault()
						.createAttributeLiteral(key, "", null);
				element.getAttributes().put(key, attr);
				/*
				 * Now it does.
				 */
			}
			/*
			 * Step down.
			 */
			assignTypesPrivate(attr, keysTail, type);
		}
	}
}
