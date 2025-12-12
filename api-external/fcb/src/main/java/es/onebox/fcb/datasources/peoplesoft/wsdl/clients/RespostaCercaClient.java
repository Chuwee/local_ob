
package es.onebox.fcb.datasources.peoplesoft.wsdl.clients;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RespostaCercaClient complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RespostaCercaClient"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://fcbarcelona.cat/servei/Vendes/Clients/v01/types}GenericResponse"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="dadesClient" type="{http://fcbarcelona.cat/servei/Vendes/Clients/v01/types}DadesClient" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RespostaCercaClient", propOrder = {
    "dadesClient"
})
public class RespostaCercaClient
    extends GenericResponse
{

    protected DadesClient dadesClient;

    /**
     * Gets the value of the dadesClient property.
     * 
     * @return
     *     possible object is
     *     {@link DadesClient }
     *     
     */
    public DadesClient getDadesClient() {
        return dadesClient;
    }

    /**
     * Sets the value of the dadesClient property.
     * 
     * @param value
     *     allowed object is
     *     {@link DadesClient }
     *     
     */
    public void setDadesClient(DadesClient value) {
        this.dadesClient = value;
    }

}
