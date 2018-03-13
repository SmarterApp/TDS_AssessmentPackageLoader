
package tds.testpackage.legacy.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="outvalue" use="required" type="{http://www.w3.org/2001/XMLSchema}token" />
 *       &lt;attribute name="invalue" use="required" type="{http://www.w3.org/2001/XMLSchema}token" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "conversiontuple")
public class Conversiontuple {

    @XmlAttribute(name = "outvalue", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String outvalue;
    @XmlAttribute(name = "invalue", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String invalue;

    /**
     * Gets the value of the outvalue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOutvalue() {
        return outvalue;
    }

    /**
     * Sets the value of the outvalue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOutvalue(String value) {
        this.outvalue = value;
    }

    /**
     * Gets the value of the invalue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInvalue() {
        return invalue;
    }

    /**
     * Sets the value of the invalue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInvalue(String value) {
        this.invalue = value;
    }

}
