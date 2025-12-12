
package es.onebox.fcb.datasources.peoplesoft.wsdl.clients;

import javax.xml.namespace.QName;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the es.onebox.fcb.datasources.peoplesoft.wsdl.clients package. 
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
    private final static QName _RespostaAltaClient_QNAME = new QName("http://fcbarcelona.cat/servei/Vendes/Clients/v01/types", "respostaAltaClient");
    private final static QName _RespostaCercaClient_QNAME = new QName("http://fcbarcelona.cat/servei/Vendes/Clients/v01/types", "respostaCercaClient");
    private final static QName _RespostaModificacioClient_QNAME = new QName("http://fcbarcelona.cat/servei/Vendes/Clients/v01/types", "respostaModificacioClient");
    private final static QName _RespostaValidacioNif_QNAME = new QName("http://fcbarcelona.cat/servei/Vendes/Clients/v01/types", "respostaValidacioNif");
    private final static QName _PeticioModificacioClient_QNAME = new QName("http://fcbarcelona.cat/servei/Vendes/Clients/v01/types", "peticioModificacioClient");
    private final static QName _PeticioAltaClient_QNAME = new QName("http://fcbarcelona.cat/servei/Vendes/Clients/v01/types", "peticioAltaClient");
    private final static QName _PeticioCercaClient_QNAME = new QName("http://fcbarcelona.cat/servei/Vendes/Clients/v01/types", "peticioCercaClient");
    private final static QName _PeticioValidacioNif_QNAME = new QName("http://fcbarcelona.cat/servei/Vendes/Clients/v01/types", "peticioValidacioNif");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: es.onebox.fcb.datasources.peoplesoft.wsdl.clients
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link DadesClient }
     * 
     */
    public DadesClient createDadesClient() {
        return new DadesClient();
    }

    /**
     * Create an instance of {@link DadesClient.IdentificadorsOrigen }
     * 
     */
    public DadesClient.IdentificadorsOrigen createDadesClientIdentificadorsOrigen() {
        return new DadesClient.IdentificadorsOrigen();
    }

    /**
     * Create an instance of {@link FaultServeisFCBType }
     * 
     */
    public FaultServeisFCBType createFaultServeisFCBType() {
        return new FaultServeisFCBType();
    }

    /**
     * Create an instance of {@link RespostaAltaClient }
     * 
     */
    public RespostaAltaClient createRespostaAltaClient() {
        return new RespostaAltaClient();
    }

    /**
     * Create an instance of {@link RespostaCercaClient }
     * 
     */
    public RespostaCercaClient createRespostaCercaClient() {
        return new RespostaCercaClient();
    }

    /**
     * Create an instance of {@link RespostaModificacioClient }
     * 
     */
    public RespostaModificacioClient createRespostaModificacioClient() {
        return new RespostaModificacioClient();
    }

    /**
     * Create an instance of {@link RespostaValidacioNif }
     * 
     */
    public RespostaValidacioNif createRespostaValidacioNif() {
        return new RespostaValidacioNif();
    }

    /**
     * Create an instance of {@link PeticioModificacioClient }
     * 
     */
    public PeticioModificacioClient createPeticioModificacioClient() {
        return new PeticioModificacioClient();
    }

    /**
     * Create an instance of {@link PeticioAltaClient }
     * 
     */
    public PeticioAltaClient createPeticioAltaClient() {
        return new PeticioAltaClient();
    }

    /**
     * Create an instance of {@link PeticioCercaClient }
     * 
     */
    public PeticioCercaClient createPeticioCercaClient() {
        return new PeticioCercaClient();
    }

    /**
     * Create an instance of {@link PeticioValidacioNif }
     * 
     */
    public PeticioValidacioNif createPeticioValidacioNif() {
        return new PeticioValidacioNif();
    }

    /**
     * Create an instance of {@link GenericResponse }
     * 
     */
    public GenericResponse createGenericResponse() {
        return new GenericResponse();
    }

    /**
     * Create an instance of {@link DadesIVA }
     * 
     */
    public DadesIVA createDadesIVA() {
        return new DadesIVA();
    }

    /**
     * Create an instance of {@link Adreca }
     * 
     */
    public Adreca createAdreca() {
        return new Adreca();
    }

    /**
     * Create an instance of {@link DadesTargeta }
     * 
     */
    public DadesTargeta createDadesTargeta() {
        return new DadesTargeta();
    }

    /**
     * Create an instance of {@link DadesContacte }
     * 
     */
    public DadesContacte createDadesContacte() {
        return new DadesContacte();
    }

    /**
     * Create an instance of {@link OpcionsBancaries }
     * 
     */
    public OpcionsBancaries createOpcionsBancaries() {
        return new OpcionsBancaries();
    }

    /**
     * Create an instance of {@link ModificarOpcionsBancaries }
     * 
     */
    public ModificarOpcionsBancaries createModificarOpcionsBancaries() {
        return new ModificarOpcionsBancaries();
    }

    /**
     * Create an instance of {@link DadesAltaClient }
     * 
     */
    public DadesAltaClient createDadesAltaClient() {
        return new DadesAltaClient();
    }

    /**
     * Create an instance of {@link DadesModificarClient }
     * 
     */
    public DadesModificarClient createDadesModificarClient() {
        return new DadesModificarClient();
    }

    /**
     * Create an instance of {@link DadesClient.IdentificadorsOrigen.Identificadors }
     * 
     */
    public DadesClient.IdentificadorsOrigen.Identificadors createDadesClientIdentificadorsOrigenIdentificadors() {
        return new DadesClient.IdentificadorsOrigen.Identificadors();
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
     * Create an instance of {@link JAXBElement }{@code <}{@link RespostaAltaClient }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link RespostaAltaClient }{@code >}
     */
    @XmlElementDecl(namespace = "http://fcbarcelona.cat/servei/Vendes/Clients/v01/types", name = "respostaAltaClient")
    public JAXBElement<RespostaAltaClient> createRespostaAltaClient(RespostaAltaClient value) {
        return new JAXBElement<RespostaAltaClient>(_RespostaAltaClient_QNAME, RespostaAltaClient.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RespostaCercaClient }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link RespostaCercaClient }{@code >}
     */
    @XmlElementDecl(namespace = "http://fcbarcelona.cat/servei/Vendes/Clients/v01/types", name = "respostaCercaClient")
    public JAXBElement<RespostaCercaClient> createRespostaCercaClient(RespostaCercaClient value) {
        return new JAXBElement<RespostaCercaClient>(_RespostaCercaClient_QNAME, RespostaCercaClient.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RespostaModificacioClient }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link RespostaModificacioClient }{@code >}
     */
    @XmlElementDecl(namespace = "http://fcbarcelona.cat/servei/Vendes/Clients/v01/types", name = "respostaModificacioClient")
    public JAXBElement<RespostaModificacioClient> createRespostaModificacioClient(RespostaModificacioClient value) {
        return new JAXBElement<RespostaModificacioClient>(_RespostaModificacioClient_QNAME, RespostaModificacioClient.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RespostaValidacioNif }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link RespostaValidacioNif }{@code >}
     */
    @XmlElementDecl(namespace = "http://fcbarcelona.cat/servei/Vendes/Clients/v01/types", name = "respostaValidacioNif")
    public JAXBElement<RespostaValidacioNif> createRespostaValidacioNif(RespostaValidacioNif value) {
        return new JAXBElement<RespostaValidacioNif>(_RespostaValidacioNif_QNAME, RespostaValidacioNif.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PeticioModificacioClient }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link PeticioModificacioClient }{@code >}
     */
    @XmlElementDecl(namespace = "http://fcbarcelona.cat/servei/Vendes/Clients/v01/types", name = "peticioModificacioClient")
    public JAXBElement<PeticioModificacioClient> createPeticioModificacioClient(PeticioModificacioClient value) {
        return new JAXBElement<PeticioModificacioClient>(_PeticioModificacioClient_QNAME, PeticioModificacioClient.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PeticioAltaClient }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link PeticioAltaClient }{@code >}
     */
    @XmlElementDecl(namespace = "http://fcbarcelona.cat/servei/Vendes/Clients/v01/types", name = "peticioAltaClient")
    public JAXBElement<PeticioAltaClient> createPeticioAltaClient(PeticioAltaClient value) {
        return new JAXBElement<PeticioAltaClient>(_PeticioAltaClient_QNAME, PeticioAltaClient.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PeticioCercaClient }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link PeticioCercaClient }{@code >}
     */
    @XmlElementDecl(namespace = "http://fcbarcelona.cat/servei/Vendes/Clients/v01/types", name = "peticioCercaClient")
    public JAXBElement<PeticioCercaClient> createPeticioCercaClient(PeticioCercaClient value) {
        return new JAXBElement<PeticioCercaClient>(_PeticioCercaClient_QNAME, PeticioCercaClient.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PeticioValidacioNif }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link PeticioValidacioNif }{@code >}
     */
    @XmlElementDecl(namespace = "http://fcbarcelona.cat/servei/Vendes/Clients/v01/types", name = "peticioValidacioNif")
    public JAXBElement<PeticioValidacioNif> createPeticioValidacioNif(PeticioValidacioNif value) {
        return new JAXBElement<PeticioValidacioNif>(_PeticioValidacioNif_QNAME, PeticioValidacioNif.class, null, value);
    }

}
