
package tds.testpackage.legacy.model;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
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
 *       &lt;sequence>
 *         &lt;element ref="{}identifier"/>
 *         &lt;element ref="{}computationruleparameter" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="bpelementid" use="required" type="{http://www.w3.org/2001/XMLSchema}token" />
 *       &lt;attribute name="computationorder" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "identifier",
    "computationruleparameter"
})
@XmlRootElement(name = "computationrule")
public class Computationrule {

    @XmlElement(required = true)
    protected Identifier identifier;
    protected List<Computationruleparameter> computationruleparameter;
    @XmlAttribute(name = "bpelementid", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String bpelementid;
    @XmlAttribute(name = "computationorder", required = true)
    protected BigInteger computationorder;

    /**
     * Gets the value of the identifier property.
     * 
     * @return
     *     possible object is
     *     {@link Identifier }
     *     
     */
    public Identifier getIdentifier() {
        return identifier;
    }

    /**
     * Sets the value of the identifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link Identifier }
     *     
     */
    public void setIdentifier(Identifier value) {
        this.identifier = value;
    }

    /**
     * Gets the value of the computationruleparameter property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the computationruleparameter property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getComputationruleparameter().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Computationruleparameter }
     * 
     * 
     */
    public List<Computationruleparameter> getComputationruleparameter() {
        if (computationruleparameter == null) {
            computationruleparameter = new ArrayList<Computationruleparameter>();
        }
        return this.computationruleparameter;
    }

    /**
     * Gets the value of the bpelementid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBpelementid() {
        return bpelementid;
    }

    /**
     * Sets the value of the bpelementid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBpelementid(String value) {
        this.bpelementid = value;
    }

    /**
     * Gets the value of the computationorder property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getComputationorder() {
        return computationorder;
    }

    /**
     * Sets the value of the computationorder property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setComputationorder(BigInteger value) {
        this.computationorder = value;
    }

}
