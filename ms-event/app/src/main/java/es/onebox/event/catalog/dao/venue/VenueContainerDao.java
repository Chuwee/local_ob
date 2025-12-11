package es.onebox.event.catalog.dao.venue;

import es.onebox.jooq.cpanel.tables.records.CpanelContenedorRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEnlaceRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelSectorConfigRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelZonaNoNumeradaRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

import static es.onebox.jooq.cpanel.Tables.CPANEL_CONFIG_RECINTO;
import static es.onebox.jooq.cpanel.Tables.CPANEL_CONTENEDOR;
import static es.onebox.jooq.cpanel.Tables.CPANEL_ENLACE;
import static es.onebox.jooq.cpanel.Tables.CPANEL_SECTOR_CONFIG;
import static es.onebox.jooq.cpanel.Tables.CPANEL_ZONA_NO_NUMERADA;

@Repository
public class VenueContainerDao extends DaoImpl<CpanelContenedorRecord, Integer> {

    public VenueContainerDao() {
        super(CPANEL_CONTENEDOR);
    }

    public List<CpanelContenedorRecord> getByVenueConfigId(Integer venueConfigId) {
        return dsl.select(CPANEL_CONTENEDOR.IDCONTENEDOR, CPANEL_CONTENEDOR.IDCONFIGURACION,
                        CPANEL_CONTENEDOR.NOMBRECONTENEDOR, CPANEL_CONTENEDOR.DESCRIPCION,
                        CPANEL_CONTENEDOR.ESVISTAPRINCIPAL, CPANEL_CONTENEDOR.URLREPRESENTACION)
                .from(CPANEL_CONTENEDOR)
                .where(CPANEL_CONTENEDOR.IDCONFIGURACION.eq(venueConfigId))
                .fetchInto(CpanelContenedorRecord.class);
    }

    public List<CpanelEnlaceRecord> getLinksByVenueConfigId(Integer venueConfigId) {
        return dsl.select(CPANEL_ENLACE.IDENLACE, CPANEL_ENLACE.ORIGEN, CPANEL_ENLACE.DESTINO, CPANEL_ENLACE.REFID)
                .from(CPANEL_ENLACE)
                .innerJoin(CPANEL_CONTENEDOR).on(CPANEL_CONTENEDOR.IDCONTENEDOR.eq(CPANEL_ENLACE.ORIGEN))
                .where(CPANEL_CONTENEDOR.IDCONFIGURACION.eq(venueConfigId))
                .fetchInto(CpanelEnlaceRecord.class);
    }

    public List<CpanelZonaNoNumeradaRecord> getZnnByVenueConfigId(Integer venueConfigId) {
        return dsl.select(CPANEL_ZONA_NO_NUMERADA.IDCONTENEDOR, CPANEL_ZONA_NO_NUMERADA.IDZONA,
                        CPANEL_ZONA_NO_NUMERADA.NOMBRE, CPANEL_ZONA_NO_NUMERADA.SECTOR)
                .from(CPANEL_ZONA_NO_NUMERADA)
                .innerJoin(CPANEL_CONTENEDOR).on(CPANEL_CONTENEDOR.IDCONTENEDOR.eq(CPANEL_ZONA_NO_NUMERADA.IDCONTENEDOR))
                .where(CPANEL_CONTENEDOR.IDCONFIGURACION.eq(venueConfigId))
                .fetchInto(CpanelZonaNoNumeradaRecord.class);
    }

    public List<CpanelSectorConfigRecord> geSectorsByVenueConfigId(Integer venueConfigId) {
        return dsl.select(CPANEL_SECTOR_CONFIG.IDSECTOR, CPANEL_SECTOR_CONFIG.CODIGO, CPANEL_SECTOR_CONFIG.DESCRIPCION)
                .from(CPANEL_SECTOR_CONFIG)
                .innerJoin(CPANEL_CONFIG_RECINTO).on(CPANEL_CONFIG_RECINTO.IDCONFIGURACION.eq(CPANEL_SECTOR_CONFIG.IDCONFIGURACION))
                .where(CPANEL_CONFIG_RECINTO.IDCONFIGURACION.eq(venueConfigId))
                .fetchInto(CpanelSectorConfigRecord.class);
    }

}
