//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.04.02 at 07:43:50 AM CEST 
//


package org.cmdbuild.workflow.river.engine.xpdl.schema.xpdl_10;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element ref="{http://www.wfmc.org/2002/XPDL1.0}No"/>
 *         &lt;element ref="{http://www.wfmc.org/2002/XPDL1.0}Tool" maxOccurs="unbounded"/>
 *         &lt;element ref="{http://www.wfmc.org/2002/XPDL1.0}SubFlow"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "no",
    "tool",
    "subFlow"
})
@XmlRootElement(name = "Implementation")
public class Implementation {

    @XmlElement(name = "No")
    protected No no;
    @XmlElement(name = "Tool")
    protected List<Tool> tool;
    @XmlElement(name = "SubFlow")
    protected SubFlow subFlow;

    /**
     * Gets the value of the no property.
     * 
     * @return
     *     possible object is
     *     {@link No }
     *     
     */
    public No getNo() {
        return no;
    }

    /**
     * Sets the value of the no property.
     * 
     * @param value
     *     allowed object is
     *     {@link No }
     *     
     */
    public void setNo(No value) {
        this.no = value;
    }

    /**
     * Gets the value of the tool property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tool property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTool().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Tool }
     * 
     * 
     */
    public List<Tool> getTool() {
        if (tool == null) {
            tool = new ArrayList<Tool>();
        }
        return this.tool;
    }

    /**
     * Gets the value of the subFlow property.
     * 
     * @return
     *     possible object is
     *     {@link SubFlow }
     *     
     */
    public SubFlow getSubFlow() {
        return subFlow;
    }

    /**
     * Sets the value of the subFlow property.
     * 
     * @param value
     *     allowed object is
     *     {@link SubFlow }
     *     
     */
    public void setSubFlow(SubFlow value) {
        this.subFlow = value;
    }

}
