
package tds.testpackage.legacy.model;

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
 *         &lt;element ref="{}bpref" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{}passageref" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{}poolproperty" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{}itemscoredimension" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="itemtype" use="required" type="{http://www.w3.org/2001/XMLSchema}token" />
 *       &lt;attribute name="filename" use="required" type="{http://www.w3.org/2001/XMLSchema}token" />
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
    "bpref",
    "passageref",
    "poolproperty",
    "itemscoredimension"
})
@XmlRootElement(name = "testitem")
public class Testitem {

    @XmlElement(required = true)
    protected Identifier identifier;
    protected List<Bpref> bpref;
    protected List<Passageref> passageref;
    protected List<Poolproperty> poolproperty;
    protected List<Itemscoredimension> itemscoredimension;
    @XmlAttribute(name = "itemtype", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String itemtype;
    @XmlAttribute(name = "filename", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String filename;

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
     * Gets the value of the bpref property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the bpref property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBpref().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Bpref }
     * 
     * 
     */
    public List<Bpref> getBpref() {
        if (bpref == null) {
            bpref = new ArrayList<Bpref>();
        }
        return this.bpref;
    }

    /**
     * Gets the value of the passageref property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the passageref property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPassageref().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Passageref }
     * 
     * 
     */
    public List<Passageref> getPassageref() {
        if (passageref == null) {
            passageref = new ArrayList<Passageref>();
        }
        return this.passageref;
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
     * Gets the value of the itemscoredimension property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the itemscoredimension property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getItemscoredimension().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Itemscoredimension }
     * 
     * 
     */
    public List<Itemscoredimension> getItemscoredimension() {
        if (itemscoredimension == null) {
            itemscoredimension = new ArrayList<Itemscoredimension>();
        }
        return this.itemscoredimension;
    }

    /**
     * Gets the value of the itemtype property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getItemtype() {
        return itemtype;
    }

    /**
     * Sets the value of the itemtype property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setItemtype(String value) {
        this.itemtype = value;
    }

    /**
     * Gets the value of the filename property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Sets the value of the filename property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFilename(String value) {
        this.filename = value;
    }

}
