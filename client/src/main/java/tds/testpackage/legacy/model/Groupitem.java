
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
 *       &lt;attribute name="blockid" type="{http://www.w3.org/2001/XMLSchema}token" />
 *       &lt;attribute name="groupposition" use="required" type="{http://www.w3.org/2001/XMLSchema}token" />
 *       &lt;attribute name="isfieldtest" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="true"/>
 *             &lt;enumeration value="false"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="responserequired" default="false">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="true"/>
 *             &lt;enumeration value="false"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="formposition" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *       &lt;attribute name="adminrequired" default="false">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="true"/>
 *             &lt;enumeration value="false"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="itemid" use="required" type="{http://www.w3.org/2001/XMLSchema}token" />
 *       &lt;attribute name="isactive" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="true"/>
 *             &lt;enumeration value="false"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "groupitem")
public class Groupitem {

    @XmlAttribute(name = "blockid")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String blockid;
    @XmlAttribute(name = "groupposition", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String groupposition;
    @XmlAttribute(name = "isfieldtest", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String isfieldtest;
    @XmlAttribute(name = "responserequired")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String responserequired;
    @XmlAttribute(name = "formposition")
    protected BigInteger formposition;
    @XmlAttribute(name = "adminrequired")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String adminrequired;
    @XmlAttribute(name = "itemid", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String itemid;
    @XmlAttribute(name = "isactive", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String isactive;

    /**
     * Gets the value of the blockid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBlockid() {
        return blockid;
    }

    /**
     * Sets the value of the blockid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBlockid(String value) {
        this.blockid = value;
    }

    /**
     * Gets the value of the groupposition property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGroupposition() {
        return groupposition;
    }

    /**
     * Sets the value of the groupposition property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGroupposition(String value) {
        this.groupposition = value;
    }

    /**
     * Gets the value of the isfieldtest property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIsfieldtest() {
        return isfieldtest;
    }

    /**
     * Sets the value of the isfieldtest property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIsfieldtest(String value) {
        this.isfieldtest = value;
    }

    /**
     * Gets the value of the responserequired property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResponserequired() {
        if (responserequired == null) {
            return "false";
        } else {
            return responserequired;
        }
    }

    /**
     * Sets the value of the responserequired property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResponserequired(String value) {
        this.responserequired = value;
    }

    /**
     * Gets the value of the formposition property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getFormposition() {
        return formposition;
    }

    /**
     * Sets the value of the formposition property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setFormposition(BigInteger value) {
        this.formposition = value;
    }

    /**
     * Gets the value of the adminrequired property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdminrequired() {
        if (adminrequired == null) {
            return "false";
        } else {
            return adminrequired;
        }
    }

    /**
     * Sets the value of the adminrequired property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdminrequired(String value) {
        this.adminrequired = value;
    }

    /**
     * Gets the value of the itemid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getItemid() {
        return itemid;
    }

    /**
     * Sets the value of the itemid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setItemid(String value) {
        this.itemid = value;
    }

    /**
     * Gets the value of the isactive property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIsactive() {
        return isactive;
    }

    /**
     * Sets the value of the isactive property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIsactive(String value) {
        this.isactive = value;
    }

}
