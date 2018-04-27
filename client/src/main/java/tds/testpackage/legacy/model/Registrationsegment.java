
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
 *         &lt;element ref="{}poolproperty" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="position" use="required" type="{http://www.w3.org/2001/XMLSchema}token" />
 *       &lt;attribute name="minitems" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *       &lt;attribute name="maxitems" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *       &lt;attribute name="itemselection" use="required" type="{http://www.w3.org/2001/XMLSchema}token" />
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
    "poolproperty"
})
@XmlRootElement(name = "registrationsegment")
public class Registrationsegment {

    @XmlElement(required = true)
    protected Identifier identifier;
    protected List<Poolproperty> poolproperty;
    @XmlAttribute(name = "position", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String position;
    @XmlAttribute(name = "minitems", required = true)
    protected BigInteger minitems;
    @XmlAttribute(name = "maxitems", required = true)
    protected BigInteger maxitems;
    @XmlAttribute(name = "itemselection", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String itemselection;

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
     * Gets the value of the poolproperty property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the poolproperty property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPoolproperty().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Poolproperty }
     * 
     * 
     */
    public List<Poolproperty> getPoolproperty() {
        if (poolproperty == null) {
            poolproperty = new ArrayList<Poolproperty>();
        }
        return this.poolproperty;
    }

    /**
     * Gets the value of the position property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPosition() {
        return position;
    }

    /**
     * Sets the value of the position property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPosition(String value) {
        this.position = value;
    }

    /**
     * Gets the value of the minitems property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getMinitems() {
        return minitems;
    }

    /**
     * Sets the value of the minitems property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setMinitems(BigInteger value) {
        this.minitems = value;
    }

    /**
     * Gets the value of the maxitems property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getMaxitems() {
        return maxitems;
    }

    /**
     * Sets the value of the maxitems property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setMaxitems(BigInteger value) {
        this.maxitems = value;
    }

    /**
     * Gets the value of the itemselection property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getItemselection() {
        return itemselection;
    }

    /**
     * Sets the value of the itemselection property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setItemselection(String value) {
        this.itemselection = value;
    }

}
