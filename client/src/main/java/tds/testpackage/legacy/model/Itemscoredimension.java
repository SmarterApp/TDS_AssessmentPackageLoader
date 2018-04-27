
package tds.testpackage.legacy.model;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
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
 *       &lt;sequence>
 *         &lt;element ref="{}itemscoreparameter" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{}property" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="measurementmodel" use="required" type="{http://www.w3.org/2001/XMLSchema}token" />
 *       &lt;attribute name="weight" use="required" type="{http://www.w3.org/2001/XMLSchema}float" />
 *       &lt;attribute name="dimension" type="{http://www.w3.org/2001/XMLSchema}token" />
 *       &lt;attribute name="scorepoints" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "itemscoreparameter",
    "property"
})
@XmlRootElement(name = "itemscoredimension")
public class Itemscoredimension {

    protected List<Itemscoreparameter> itemscoreparameter;
    protected List<Property> property;
    @XmlAttribute(name = "measurementmodel", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String measurementmodel;
    @XmlAttribute(name = "weight", required = true)
    protected float weight;
    @XmlAttribute(name = "dimension")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String dimension;
    @XmlAttribute(name = "scorepoints", required = true)
    protected BigInteger scorepoints;

    /**
     * Gets the value of the itemscoreparameter property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the itemscoreparameter property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getItemscoreparameter().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Itemscoreparameter }
     * 
     * 
     */
    public List<Itemscoreparameter> getItemscoreparameter() {
        if (itemscoreparameter == null) {
            itemscoreparameter = new ArrayList<Itemscoreparameter>();
        }
        return this.itemscoreparameter;
    }

    /**
     * Gets the value of the property property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the property property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProperty().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Property }
     * 
     * 
     */
    public List<Property> getProperty() {
        if (property == null) {
            property = new ArrayList<Property>();
        }
        return this.property;
    }

    /**
     * Gets the value of the measurementmodel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMeasurementmodel() {
        return measurementmodel;
    }

    /**
     * Sets the value of the measurementmodel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMeasurementmodel(String value) {
        this.measurementmodel = value;
    }

    /**
     * Gets the value of the weight property.
     * 
     */
    public float getWeight() {
        return weight;
    }

    /**
     * Sets the value of the weight property.
     * 
     */
    public void setWeight(float value) {
        this.weight = value;
    }

    /**
     * Gets the value of the dimension property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDimension() {
        return dimension;
    }

    /**
     * Sets the value of the dimension property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDimension(String value) {
        this.dimension = value;
    }

    /**
     * Gets the value of the scorepoints property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getScorepoints() {
        return scorepoints;
    }

    /**
     * Sets the value of the scorepoints property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setScorepoints(BigInteger value) {
        this.scorepoints = value;
    }

}
