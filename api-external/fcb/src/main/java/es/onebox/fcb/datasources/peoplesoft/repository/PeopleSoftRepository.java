package es.onebox.fcb.datasources.peoplesoft.repository;

import es.onebox.fcb.datasources.peoplesoft.ClientsDatasource;
import es.onebox.fcb.datasources.peoplesoft.FacturesDatasource;
import es.onebox.fcb.datasources.peoplesoft.TresoreriaDatasource;
import es.onebox.fcb.datasources.peoplesoft.wsdl.clients.PeticioAltaClient;
import es.onebox.fcb.datasources.peoplesoft.wsdl.clients.PeticioCercaClient;
import es.onebox.fcb.datasources.peoplesoft.wsdl.clients.PeticioModificacioClient;
import es.onebox.fcb.datasources.peoplesoft.wsdl.clients.PeticioValidacioNif;
import es.onebox.fcb.datasources.peoplesoft.wsdl.clients.RespostaAltaClient;
import es.onebox.fcb.datasources.peoplesoft.wsdl.clients.RespostaCercaClient;
import es.onebox.fcb.datasources.peoplesoft.wsdl.clients.RespostaModificacioClient;
import es.onebox.fcb.datasources.peoplesoft.wsdl.factures.PeticioFacturar;
import es.onebox.fcb.datasources.peoplesoft.wsdl.tresoreria.AltaDipositInputType;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

@Repository
public class PeopleSoftRepository {
    private FacturesDatasource facturesDatasource;
    private ClientsDatasource clientsDatasource;
    private TresoreriaDatasource tresoreriaDatasource;

    @Lazy
    public PeopleSoftRepository(FacturesDatasource facturesDatasource, ClientsDatasource clientsDatasource, TresoreriaDatasource tresoreriaDatasource) {
        this.facturesDatasource = facturesDatasource;
        this.clientsDatasource = clientsDatasource;
        this.tresoreriaDatasource = tresoreriaDatasource;
    }

    public void registerOperation(PeticioFacturar peticioFacturar) {
        facturesDatasource.registerOperation(peticioFacturar);
    }

    public boolean checkNif(String nif, String countryCode) {
        PeticioValidacioNif peticioValidacioNif = new PeticioValidacioNif();
        peticioValidacioNif.setDocument(nif);
        peticioValidacioNif.setPais(countryCode);
        return clientsDatasource.validarNif(peticioValidacioNif) != null;
    }

    public RespostaCercaClient searchNif(PeticioCercaClient peticioCercaClient) {
        return clientsDatasource.cercarClient(peticioCercaClient);
    }

    public RespostaAltaClient addClient(PeticioAltaClient peticioAltaClient) {
        return clientsDatasource.altaClient(peticioAltaClient);
    }

    public void altaDiposit(AltaDipositInputType altaDiposit) {
        tresoreriaDatasource.altaDiposit(altaDiposit);
    }

    public RespostaModificacioClient modificarClient(PeticioModificacioClient peticioModificacioClient) {
        return clientsDatasource.modificarClient(peticioModificacioClient);
    }
}
