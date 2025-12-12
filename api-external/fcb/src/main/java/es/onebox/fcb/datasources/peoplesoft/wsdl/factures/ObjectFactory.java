
package es.onebox.fcb.datasources.peoplesoft.wsdl.factures;

import javax.xml.namespace.QName;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the es.onebox.fcb.datasources.peoplesoft.wsdl.factures package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _FaultServeisFCB_QNAME = new QName("http://fcbarcelona.cat/esquema/Fault/v01", "faultServeisFCB");
    private final static QName _RespostaFacturar_QNAME = new QName("http://fcbarcelona.cat/servei/Finances/Factures/v01/types", "respostaFacturar");
    private final static QName _PeticioFacturar_QNAME = new QName("http://fcbarcelona.cat/servei/Finances/Factures/v01/types", "peticioFacturar");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: es.onebox.fcb.datasources.peoplesoft.wsdl.factures
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link FaultServeisFCBType }
     * 
     */
    public FaultServeisFCBType createFaultServeisFCBType() {
        return new FaultServeisFCBType();
    }

    /**
     * Create an instance of {@link RespostaFacturar }
     * 
     */
    public RespostaFacturar createRespostaFacturar() {
        return new RespostaFacturar();
    }

    /**
     * Create an instance of {@link PeticioFacturar }
     * 
     */
    public PeticioFacturar createPeticioFacturar() {
        return new PeticioFacturar();
    }

    /**
     * Create an instance of {@link ArrayOfLinia }
     * 
     */
    public ArrayOfLinia createArrayOfLinia() {
        return new ArrayOfLinia();
    }

    /**
     * Create an instance of {@link Linia }
     * 
     */
    public Linia createLinia() {
        return new Linia();
    }

    /**
     * Create an instance of {@link GenericResponse }
     * 
     */
    public GenericResponse createGenericResponse() {
        return new GenericResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FaultServeisFCBType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link FaultServeisFCBType }{@code >}
     */
    @XmlElementDecl(namespace = "http://fcbarcelona.cat/esquema/Fault/v01", name = "faultServeisFCB")
    public JAXBElement<FaultServeisFCBType> createFaultServeisFCB(FaultServeisFCBType value) {
        return new JAXBElement<FaultServeisFCBType>(_FaultServeisFCB_QNAME, FaultServeisFCBType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RespostaFacturar }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link RespostaFacturar }{@code >}
     */
    @XmlElementDecl(namespace = "http://fcbarcelona.cat/servei/Finances/Factures/v01/types", name = "respostaFacturar")
    public JAXBElement<RespostaFacturar> createRespostaFacturar(RespostaFacturar value) {
        return new JAXBElement<RespostaFacturar>(_RespostaFacturar_QNAME, RespostaFacturar.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PeticioFacturar }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link PeticioFacturar }{@code >}
     */
    @XmlElementDecl(namespace = "http://fcbarcelona.cat/servei/Finances/Factures/v01/types", name = "peticioFacturar")
    public JAXBElement<PeticioFacturar> createPeticioFacturar(PeticioFacturar value) {
        return new JAXBElement<PeticioFacturar>(_PeticioFacturar_QNAME, PeticioFacturar.class, null, value);
    }

}
