
package es.onebox.fcb.datasources.peoplesoft.wsdl.tresoreria;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ReferenciesType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ReferenciesType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="referencia" type="{http://fcbarcelona.cat/servei/Finances/ServeiTresoreria/v01/types}ReferenciaType" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReferenciesType", propOrder = {
    "referencia"
})
public class ReferenciesType {

    @XmlElement(required = true)
    protected List<ReferenciaType> referencia;

    /**
     * Gets the value of the referencia property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the referencia property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getReferencia().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ReferenciaType }
     * 
     * 
     */
    public List<ReferenciaType> getReferencia() {
        if (referencia == null) {
            referencia = new ArrayList<ReferenciaType>();
        }
        return this.referencia;
    }

}
