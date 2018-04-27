
package tds.testpackage.legacy.model;

import java.math.BigInteger;
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
 *       &lt;attribute name="bpelementid" use="required" type="{http://www.w3.org/2001/XMLSchema}token" />
 *       &lt;attribute name="scaledhi" use="required" type="{http://www.w3.org/2001/XMLSchema}float" />
 *       &lt;attribute name="scaledlo" use="required" type="{http://www.w3.org/2001/XMLSchema}float" />
 *       &lt;attribute name="plevel" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "performancelevel")
public class Performancelevel {

    @XmlAttribute(name = "bpelementid", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String bpelementid;
    @XmlAttribute(name = "scaledhi", required = true)
    protected float scaledhi;
    @XmlAttribute(name = "scaledlo", required = true)
    protected float scaledlo;
    @XmlAttribute(name = "plevel", required = true)
    protected BigInteger plevel;

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
     * Gets the value of the scaledhi property.
     * 
     */
    public float getScaledhi() {
        return scaledhi;
    }

    /**
     * Sets the value of the scaledhi property.
     * 
     */
    public void setScaledhi(float value) {
        this.scaledhi = value;
    }

    /**
     * Gets the value of the scaledlo property.
     * 
     */
    public float getScaledlo() {
        return scaledlo;
    }

    /**
     * Sets the value of the scaledlo property.
     * 
     */
    public void setScaledlo(float value) {
        this.scaledlo = value;
    }

    /**
     * Gets the value of the plevel property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getPlevel() {
        return plevel;
    }

    /**
     * Sets the value of the plevel property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setPlevel(BigInteger value) {
        this.plevel = value;
    }

}
