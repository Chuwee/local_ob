
package es.onebox.fcb.datasources.peoplesoft.wsdl.factures;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * <p>Java class for ArrayOfLinia complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfLinia"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="linia" type="{http://fcbarcelona.cat/servei/Finances/Factures/v01/types}Linia" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfLinia", propOrder = {
    "linia"
})
public class ArrayOfLinia {

    @XmlElement(required = true)
    protected List<Linia> linia;

    /**
     * Gets the value of the linia property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the linia property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLinia().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Linia }
     * 
     * 
     */
    public List<Linia> getLinia() {
        if (linia == null) {
            linia = new ArrayList<Linia>();
        }
        return this.linia;
    }

}
