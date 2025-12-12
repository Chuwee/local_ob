
package es.onebox.fcb.datasources.peoplesoft.wsdl.tresoreria;

import javax.xml.namespace.QName;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the es.onebox.fcb.datasources.peoplesoft.wsdl.tresoreria package. 
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
    private final static QName _DadesCompteBanc_QNAME = new QName("http://fcbarcelona.cat/servei/Finances/ServeiTresoreria/v01/types", "dadesCompteBanc");
    private final static QName _DadesTransaccio_QNAME = new QName("http://fcbarcelona.cat/servei/Finances/ServeiTresoreria/v01/types", "dadesTransaccio");
    private final static QName _DadesImport_QNAME = new QName("http://fcbarcelona.cat/servei/Finances/ServeiTresoreria/v01/types", "dadesImport");
    private final static QName _OberturaCaixaInput_QNAME = new QName("http://fcbarcelona.cat/servei/Finances/ServeiTresoreria/v01/types", "oberturaCaixaInput");
    private final static QName _OberturaCaixaOutput_QNAME = new QName("http://fcbarcelona.cat/servei/Finances/ServeiTresoreria/v01/types", "oberturaCaixaOutput");
    private final static QName _TancamentCaixaInput_QNAME = new QName("http://fcbarcelona.cat/servei/Finances/ServeiTresoreria/v01/types", "tancamentCaixaInput");
    private final static QName _TancamentCaixaOutput_QNAME = new QName("http://fcbarcelona.cat/servei/Finances/ServeiTresoreria/v01/types", "tancamentCaixaOutput");
    private final static QName _LiquidacionsParcialsInput_QNAME = new QName("http://fcbarcelona.cat/servei/Finances/ServeiTresoreria/v01/types", "liquidacionsParcialsInput");
    private final static QName _LiquidacionsParcialsOutput_QNAME = new QName("http://fcbarcelona.cat/servei/Finances/ServeiTresoreria/v01/types", "liquidacionsParcialsOutput");
    private final static QName _AjustCaixaInput_QNAME = new QName("http://fcbarcelona.cat/servei/Finances/ServeiTresoreria/v01/types", "ajustCaixaInput");
    private final static QName _AjustCaixaOutput_QNAME = new QName("http://fcbarcelona.cat/servei/Finances/ServeiTresoreria/v01/types", "ajustCaixaOutput");
    private final static QName _TraspasCaixaInput_QNAME = new QName("http://fcbarcelona.cat/servei/Finances/ServeiTresoreria/v01/types", "traspasCaixaInput");
    private final static QName _TraspasCaixaOutput_QNAME = new QName("http://fcbarcelona.cat/servei/Finances/ServeiTresoreria/v01/types", "traspasCaixaOutput");
    private final static QName _ExtracteContaBancaria_QNAME = new QName("http://fcbarcelona.cat/servei/Finances/ServeiTresoreria/v01/types", "extracteContaBancaria");
    private final static QName _AltaDipositInput_QNAME = new QName("http://fcbarcelona.cat/servei/Finances/ServeiTresoreria/v01/types", "altaDipositInput");
    private final static QName _AltaDipositOutput_QNAME = new QName("http://fcbarcelona.cat/servei/Finances/ServeiTresoreria/v01/types", "altaDipositOutput");
    private final static QName _CargaExtracteBancariInput_QNAME = new QName("http://fcbarcelona.cat/servei/Finances/ServeiTresoreria/v01/types", "cargaExtracteBancariInput");
    private final static QName _CargaExtracteBancariOutput_QNAME = new QName("http://fcbarcelona.cat/servei/Finances/ServeiTresoreria/v01/types", "cargaExtracteBancariOutput");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: es.onebox.fcb.datasources.peoplesoft.wsdl.tresoreria
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link AltaDipositInputType }
     * 
     */
    public AltaDipositInputType createAltaDipositInputType() {
        return new AltaDipositInputType();
    }

    /**
     * Create an instance of {@link ExtracteCompteBancariaType }
     * 
     */
    public ExtracteCompteBancariaType createExtracteCompteBancariaType() {
        return new ExtracteCompteBancariaType();
    }

    /**
     * Create an instance of {@link ExtracteCompteBancariaType.DiaAnterior }
     * 
     */
    public ExtracteCompteBancariaType.DiaAnterior createExtracteCompteBancariaTypeDiaAnterior() {
        return new ExtracteCompteBancariaType.DiaAnterior();
    }

    /**
     * Create an instance of {@link FaultServeisFCBType }
     * 
     */
    public FaultServeisFCBType createFaultServeisFCBType() {
        return new FaultServeisFCBType();
    }

    /**
     * Create an instance of {@link DadesCompteBancType }
     * 
     */
    public DadesCompteBancType createDadesCompteBancType() {
        return new DadesCompteBancType();
    }

    /**
     * Create an instance of {@link DadesTransaccioType }
     * 
     */
    public DadesTransaccioType createDadesTransaccioType() {
        return new DadesTransaccioType();
    }

    /**
     * Create an instance of {@link DadesImportType }
     * 
     */
    public DadesImportType createDadesImportType() {
        return new DadesImportType();
    }

    /**
     * Create an instance of {@link OberturaCaixaInputType }
     * 
     */
    public OberturaCaixaInputType createOberturaCaixaInputType() {
        return new OberturaCaixaInputType();
    }

    /**
     * Create an instance of {@link OberturaCaixaOutputType }
     * 
     */
    public OberturaCaixaOutputType createOberturaCaixaOutputType() {
        return new OberturaCaixaOutputType();
    }

    /**
     * Create an instance of {@link TancamentCaixaInputType }
     * 
     */
    public TancamentCaixaInputType createTancamentCaixaInputType() {
        return new TancamentCaixaInputType();
    }

    /**
     * Create an instance of {@link TancamentCaixaOutputType }
     * 
     */
    public TancamentCaixaOutputType createTancamentCaixaOutputType() {
        return new TancamentCaixaOutputType();
    }

    /**
     * Create an instance of {@link LiquidacionsParcialsInputType }
     * 
     */
    public LiquidacionsParcialsInputType createLiquidacionsParcialsInputType() {
        return new LiquidacionsParcialsInputType();
    }

    /**
     * Create an instance of {@link LiquidacionsParcialsOutputType }
     * 
     */
    public LiquidacionsParcialsOutputType createLiquidacionsParcialsOutputType() {
        return new LiquidacionsParcialsOutputType();
    }

    /**
     * Create an instance of {@link AjustCaixaInputType }
     * 
     */
    public AjustCaixaInputType createAjustCaixaInputType() {
        return new AjustCaixaInputType();
    }

    /**
     * Create an instance of {@link AjustCaixaOutputType }
     * 
     */
    public AjustCaixaOutputType createAjustCaixaOutputType() {
        return new AjustCaixaOutputType();
    }

    /**
     * Create an instance of {@link TraspasCaixaInputType }
     * 
     */
    public TraspasCaixaInputType createTraspasCaixaInputType() {
        return new TraspasCaixaInputType();
    }

    /**
     * Create an instance of {@link TraspasCaixaOutputType }
     * 
     */
    public TraspasCaixaOutputType createTraspasCaixaOutputType() {
        return new TraspasCaixaOutputType();
    }

    /**
     * Create an instance of {@link AltaDipositOutputType }
     * 
     */
    public AltaDipositOutputType createAltaDipositOutputType() {
        return new AltaDipositOutputType();
    }

    /**
     * Create an instance of {@link CargaExtracteBancariInputType }
     * 
     */
    public CargaExtracteBancariInputType createCargaExtracteBancariInputType() {
        return new CargaExtracteBancariInputType();
    }

    /**
     * Create an instance of {@link CargaExtracteBancariOutputType }
     * 
     */
    public CargaExtracteBancariOutputType createCargaExtracteBancariOutputType() {
        return new CargaExtracteBancariOutputType();
    }

    /**
     * Create an instance of {@link DadesCompteBancDipositType }
     * 
     */
    public DadesCompteBancDipositType createDadesCompteBancDipositType() {
        return new DadesCompteBancDipositType();
    }

    /**
     * Create an instance of {@link DadesDipositType }
     * 
     */
    public DadesDipositType createDadesDipositType() {
        return new DadesDipositType();
    }

    /**
     * Create an instance of {@link SistemesExternsType }
     * 
     */
    public SistemesExternsType createSistemesExternsType() {
        return new SistemesExternsType();
    }

    /**
     * Create an instance of {@link CobramentType }
     * 
     */
    public CobramentType createCobramentType() {
        return new CobramentType();
    }

    /**
     * Create an instance of {@link ClientsSistemaOrigenType }
     * 
     */
    public ClientsSistemaOrigenType createClientsSistemaOrigenType() {
        return new ClientsSistemaOrigenType();
    }

    /**
     * Create an instance of {@link ReferenciesType }
     * 
     */
    public ReferenciesType createReferenciesType() {
        return new ReferenciesType();
    }

    /**
     * Create an instance of {@link ReferenciaType }
     * 
     */
    public ReferenciaType createReferenciaType() {
        return new ReferenciaType();
    }

    /**
     * Create an instance of {@link DadesImportDipositType }
     * 
     */
    public DadesImportDipositType createDadesImportDipositType() {
        return new DadesImportDipositType();
    }

    /**
     * Create an instance of {@link ResultatOperacioTransaccioExternaType }
     * 
     */
    public ResultatOperacioTransaccioExternaType createResultatOperacioTransaccioExternaType() {
        return new ResultatOperacioTransaccioExternaType();
    }

    /**
     * Create an instance of {@link ResultatOperacioDipositsType }
     * 
     */
    public ResultatOperacioDipositsType createResultatOperacioDipositsType() {
        return new ResultatOperacioDipositsType();
    }

    /**
     * Create an instance of {@link ResultatOperacioCargaExtracteBancariType }
     * 
     */
    public ResultatOperacioCargaExtracteBancariType createResultatOperacioCargaExtracteBancariType() {
        return new ResultatOperacioCargaExtracteBancariType();
    }

    /**
     * Create an instance of {@link CapcaleraExtracteBancariType }
     * 
     */
    public CapcaleraExtracteBancariType createCapcaleraExtracteBancariType() {
        return new CapcaleraExtracteBancariType();
    }

    /**
     * Create an instance of {@link CapcaleraComptaBancariaType }
     * 
     */
    public CapcaleraComptaBancariaType createCapcaleraComptaBancariaType() {
        return new CapcaleraComptaBancariaType();
    }

    /**
     * Create an instance of {@link CapcaleraComptaBancariaBalType }
     * 
     */
    public CapcaleraComptaBancariaBalType createCapcaleraComptaBancariaBalType() {
        return new CapcaleraComptaBancariaBalType();
    }

    /**
     * Create an instance of {@link TransaccioComptaBancariaType }
     * 
     */
    public TransaccioComptaBancariaType createTransaccioComptaBancariaType() {
        return new TransaccioComptaBancariaType();
    }

    /**
     * Create an instance of {@link BalanceComptaBancariaType }
     * 
     */
    public BalanceComptaBancariaType createBalanceComptaBancariaType() {
        return new BalanceComptaBancariaType();
    }

    /**
     * Create an instance of {@link DadesExtracteType }
     * 
     */
    public DadesExtracteType createDadesExtracteType() {
        return new DadesExtracteType();
    }

    /**
     * Create an instance of {@link RemitentExtracteType }
     * 
     */
    public RemitentExtracteType createRemitentExtracteType() {
        return new RemitentExtracteType();
    }

    /**
     * Create an instance of {@link AltaDipositInputType.Cobraments }
     * 
     */
    public AltaDipositInputType.Cobraments createAltaDipositInputTypeCobraments() {
        return new AltaDipositInputType.Cobraments();
    }

    /**
     * Create an instance of {@link ExtracteCompteBancariaType.DiaActual }
     * 
     */
    public ExtracteCompteBancariaType.DiaActual createExtracteCompteBancariaTypeDiaActual() {
        return new ExtracteCompteBancariaType.DiaActual();
    }

    /**
     * Create an instance of {@link ExtracteCompteBancariaType.DiaAnterior.TransaccioPrevia }
     * 
     */
    public ExtracteCompteBancariaType.DiaAnterior.TransaccioPrevia createExtracteCompteBancariaTypeDiaAnteriorTransaccioPrevia() {
        return new ExtracteCompteBancariaType.DiaAnterior.TransaccioPrevia();
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
     * Create an instance of {@link JAXBElement }{@code <}{@link DadesCompteBancType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link DadesCompteBancType }{@code >}
     */
    @XmlElementDecl(namespace = "http://fcbarcelona.cat/servei/Finances/ServeiTresoreria/v01/types", name = "dadesCompteBanc")
    public JAXBElement<DadesCompteBancType> createDadesCompteBanc(DadesCompteBancType value) {
        return new JAXBElement<DadesCompteBancType>(_DadesCompteBanc_QNAME, DadesCompteBancType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DadesTransaccioType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link DadesTransaccioType }{@code >}
     */
    @XmlElementDecl(namespace = "http://fcbarcelona.cat/servei/Finances/ServeiTresoreria/v01/types", name = "dadesTransaccio")
    public JAXBElement<DadesTransaccioType> createDadesTransaccio(DadesTransaccioType value) {
        return new JAXBElement<DadesTransaccioType>(_DadesTransaccio_QNAME, DadesTransaccioType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DadesImportType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link DadesImportType }{@code >}
     */
    @XmlElementDecl(namespace = "http://fcbarcelona.cat/servei/Finances/ServeiTresoreria/v01/types", name = "dadesImport")
    public JAXBElement<DadesImportType> createDadesImport(DadesImportType value) {
        return new JAXBElement<DadesImportType>(_DadesImport_QNAME, DadesImportType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OberturaCaixaInputType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link OberturaCaixaInputType }{@code >}
     */
    @XmlElementDecl(namespace = "http://fcbarcelona.cat/servei/Finances/ServeiTresoreria/v01/types", name = "oberturaCaixaInput")
    public JAXBElement<OberturaCaixaInputType> createOberturaCaixaInput(OberturaCaixaInputType value) {
        return new JAXBElement<OberturaCaixaInputType>(_OberturaCaixaInput_QNAME, OberturaCaixaInputType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OberturaCaixaOutputType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link OberturaCaixaOutputType }{@code >}
     */
    @XmlElementDecl(namespace = "http://fcbarcelona.cat/servei/Finances/ServeiTresoreria/v01/types", name = "oberturaCaixaOutput")
    public JAXBElement<OberturaCaixaOutputType> createOberturaCaixaOutput(OberturaCaixaOutputType value) {
        return new JAXBElement<OberturaCaixaOutputType>(_OberturaCaixaOutput_QNAME, OberturaCaixaOutputType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TancamentCaixaInputType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link TancamentCaixaInputType }{@code >}
     */
    @XmlElementDecl(namespace = "http://fcbarcelona.cat/servei/Finances/ServeiTresoreria/v01/types", name = "tancamentCaixaInput")
    public JAXBElement<TancamentCaixaInputType> createTancamentCaixaInput(TancamentCaixaInputType value) {
        return new JAXBElement<TancamentCaixaInputType>(_TancamentCaixaInput_QNAME, TancamentCaixaInputType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TancamentCaixaOutputType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link TancamentCaixaOutputType }{@code >}
     */
    @XmlElementDecl(namespace = "http://fcbarcelona.cat/servei/Finances/ServeiTresoreria/v01/types", name = "tancamentCaixaOutput")
    public JAXBElement<TancamentCaixaOutputType> createTancamentCaixaOutput(TancamentCaixaOutputType value) {
        return new JAXBElement<TancamentCaixaOutputType>(_TancamentCaixaOutput_QNAME, TancamentCaixaOutputType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LiquidacionsParcialsInputType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link LiquidacionsParcialsInputType }{@code >}
     */
    @XmlElementDecl(namespace = "http://fcbarcelona.cat/servei/Finances/ServeiTresoreria/v01/types", name = "liquidacionsParcialsInput")
    public JAXBElement<LiquidacionsParcialsInputType> createLiquidacionsParcialsInput(LiquidacionsParcialsInputType value) {
        return new JAXBElement<LiquidacionsParcialsInputType>(_LiquidacionsParcialsInput_QNAME, LiquidacionsParcialsInputType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LiquidacionsParcialsOutputType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link LiquidacionsParcialsOutputType }{@code >}
     */
    @XmlElementDecl(namespace = "http://fcbarcelona.cat/servei/Finances/ServeiTresoreria/v01/types", name = "liquidacionsParcialsOutput")
    public JAXBElement<LiquidacionsParcialsOutputType> createLiquidacionsParcialsOutput(LiquidacionsParcialsOutputType value) {
        return new JAXBElement<LiquidacionsParcialsOutputType>(_LiquidacionsParcialsOutput_QNAME, LiquidacionsParcialsOutputType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AjustCaixaInputType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link AjustCaixaInputType }{@code >}
     */
    @XmlElementDecl(namespace = "http://fcbarcelona.cat/servei/Finances/ServeiTresoreria/v01/types", name = "ajustCaixaInput")
    public JAXBElement<AjustCaixaInputType> createAjustCaixaInput(AjustCaixaInputType value) {
        return new JAXBElement<AjustCaixaInputType>(_AjustCaixaInput_QNAME, AjustCaixaInputType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AjustCaixaOutputType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link AjustCaixaOutputType }{@code >}
     */
    @XmlElementDecl(namespace = "http://fcbarcelona.cat/servei/Finances/ServeiTresoreria/v01/types", name = "ajustCaixaOutput")
    public JAXBElement<AjustCaixaOutputType> createAjustCaixaOutput(AjustCaixaOutputType value) {
        return new JAXBElement<AjustCaixaOutputType>(_AjustCaixaOutput_QNAME, AjustCaixaOutputType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TraspasCaixaInputType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link TraspasCaixaInputType }{@code >}
     */
    @XmlElementDecl(namespace = "http://fcbarcelona.cat/servei/Finances/ServeiTresoreria/v01/types", name = "traspasCaixaInput")
    public JAXBElement<TraspasCaixaInputType> createTraspasCaixaInput(TraspasCaixaInputType value) {
        return new JAXBElement<TraspasCaixaInputType>(_TraspasCaixaInput_QNAME, TraspasCaixaInputType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TraspasCaixaOutputType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link TraspasCaixaOutputType }{@code >}
     */
    @XmlElementDecl(namespace = "http://fcbarcelona.cat/servei/Finances/ServeiTresoreria/v01/types", name = "traspasCaixaOutput")
    public JAXBElement<TraspasCaixaOutputType> createTraspasCaixaOutput(TraspasCaixaOutputType value) {
        return new JAXBElement<TraspasCaixaOutputType>(_TraspasCaixaOutput_QNAME, TraspasCaixaOutputType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ExtracteCompteBancariaType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ExtracteCompteBancariaType }{@code >}
     */
    @XmlElementDecl(namespace = "http://fcbarcelona.cat/servei/Finances/ServeiTresoreria/v01/types", name = "extracteContaBancaria")
    public JAXBElement<ExtracteCompteBancariaType> createExtracteContaBancaria(ExtracteCompteBancariaType value) {
        return new JAXBElement<ExtracteCompteBancariaType>(_ExtracteContaBancaria_QNAME, ExtracteCompteBancariaType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AltaDipositInputType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link AltaDipositInputType }{@code >}
     */
    @XmlElementDecl(namespace = "http://fcbarcelona.cat/servei/Finances/ServeiTresoreria/v01/types", name = "altaDipositInput")
    public JAXBElement<AltaDipositInputType> createAltaDipositInput(AltaDipositInputType value) {
        return new JAXBElement<AltaDipositInputType>(_AltaDipositInput_QNAME, AltaDipositInputType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AltaDipositOutputType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link AltaDipositOutputType }{@code >}
     */
    @XmlElementDecl(namespace = "http://fcbarcelona.cat/servei/Finances/ServeiTresoreria/v01/types", name = "altaDipositOutput")
    public JAXBElement<AltaDipositOutputType> createAltaDipositOutput(AltaDipositOutputType value) {
        return new JAXBElement<AltaDipositOutputType>(_AltaDipositOutput_QNAME, AltaDipositOutputType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CargaExtracteBancariInputType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link CargaExtracteBancariInputType }{@code >}
     */
    @XmlElementDecl(namespace = "http://fcbarcelona.cat/servei/Finances/ServeiTresoreria/v01/types", name = "cargaExtracteBancariInput")
    public JAXBElement<CargaExtracteBancariInputType> createCargaExtracteBancariInput(CargaExtracteBancariInputType value) {
        return new JAXBElement<CargaExtracteBancariInputType>(_CargaExtracteBancariInput_QNAME, CargaExtracteBancariInputType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CargaExtracteBancariOutputType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link CargaExtracteBancariOutputType }{@code >}
     */
    @XmlElementDecl(namespace = "http://fcbarcelona.cat/servei/Finances/ServeiTresoreria/v01/types", name = "cargaExtracteBancariOutput")
    public JAXBElement<CargaExtracteBancariOutputType> createCargaExtracteBancariOutput(CargaExtracteBancariOutputType value) {
        return new JAXBElement<CargaExtracteBancariOutputType>(_CargaExtracteBancariOutput_QNAME, CargaExtracteBancariOutputType.class, null, value);
    }

}
