
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
 *       &lt;attribute name="scorelabel" use="required" type="{http://www.w3.org/2001/XMLSchema}token" />
 *       &lt;attribute name="scorename" use="required" type="{http://www.w3.org/2001/XMLSchema}token" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "scoretype")
public class Scoretype {

    @XmlAttribute(name = "scorelabel", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String scorelabel;
    @XmlAttribute(name = "scorename", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String scorename;

    /**
     * Gets the value of the scorelabel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getScorelabel() {
        return scorelabel;
    }

    /**
     * Sets the value of the scorelabel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setScorelabel(String value) {
        this.scorelabel = value;
    }

    /**
     * Gets the value of the scorename property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getScorename() {
        return scorename;
    }

    /**
     * Sets the value of the scorename property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setScorename(String value) {
        this.scorename = value;
    }

}
