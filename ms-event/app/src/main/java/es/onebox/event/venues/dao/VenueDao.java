package es.onebox.event.venues.dao;

import es.onebox.event.venues.domain.VenueRecord;
import es.onebox.jooq.cpanel.Tables;
import es.onebox.jooq.cpanel.tables.records.CpanelRecintoRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.jooq.Record;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

import static es.onebox.jooq.cpanel.Tables.CPANEL_RECINTO;

@Repository
public class VenueDao extends DaoImpl<CpanelRecintoRecord, Integer> {

    protected VenueDao() {
        super(CPANEL_RECINTO);
    }

    public List<VenueRecord> getVenues(Collection<Long> venueIds) {
        return dsl.select(
                CPANEL_RECINTO.IDRECINTO,
                CPANEL_RECINTO.IDENTIDAD,
                CPANEL_RECINTO.NOMBRE,
                CPANEL_RECINTO.MUNICIPIO,
                CPANEL_RECINTO.CODIGOPOSTAL,
                CPANEL_RECINTO.DIRECCION,
                CPANEL_RECINTO.COORDENADA,
                CPANEL_RECINTO.EMPRESAPROPIETARIA,
                CPANEL_RECINTO.EMPRESAGESTORA,
                CPANEL_RECINTO.CARGOCONTACTO,
                CPANEL_RECINTO.NOMBRECONTACTO,
                CPANEL_RECINTO.APELLIDOSCONTACTO,
                CPANEL_RECINTO.TELEFONOCONTACTO,
                CPANEL_RECINTO.CORREOCONTACTO,
                CPANEL_RECINTO.PATHLOGO,
                CPANEL_RECINTO.GOOGLEPLACEID,
                CPANEL_RECINTO.EXTERNALVENUEID,
                Tables.CPANEL_PAIS.CODIGO,
                Tables.CPANEL_PAIS.NOMBRE,
                Tables.CPANEL_PROVINCIA.NOMBRE,
                Tables.CPANEL_PROVINCIA.CODIGO,
                Tables.CPANEL_TIME_ZONE_GROUP.OLSONID,
                Tables.CPANEL_ENTIDAD.NOMBRE
        )
                .from(CPANEL_RECINTO)
                .innerJoin(Tables.CPANEL_PAIS).on(CPANEL_RECINTO.PAIS.eq(Tables.CPANEL_PAIS.IDPAIS))
                .innerJoin(Tables.CPANEL_TIME_ZONE_GROUP).on(CPANEL_RECINTO.TIMEZONE.eq(Tables.CPANEL_TIME_ZONE_GROUP.ZONEID))
                .innerJoin(Tables.CPANEL_PROVINCIA).on(CPANEL_RECINTO.PROVINCIA.eq(Tables.CPANEL_PROVINCIA.IDPROVINCIA))
                .innerJoin(Tables.CPANEL_ENTIDAD).on(CPANEL_RECINTO.IDENTIDAD.eq(Tables.CPANEL_ENTIDAD.IDENTIDAD))
                .where(CPANEL_RECINTO.IDRECINTO.in(venueIds))
                .fetch(this::mapToVenueRecord);
    }

    private VenueRecord mapToVenueRecord(Record record) {
        VenueRecord venueRecord = new VenueRecord();
        venueRecord.setEntityId(record.get(CPANEL_RECINTO.IDENTIDAD).longValue());
        venueRecord.setId(record.get(CPANEL_RECINTO.IDRECINTO).longValue());
        venueRecord.setAddress(record.get(CPANEL_RECINTO.DIRECCION));
        venueRecord.setMunicipality(record.get(CPANEL_RECINTO.MUNICIPIO));
        venueRecord.setTimeZone(record.get(Tables.CPANEL_TIME_ZONE_GROUP.OLSONID));
        venueRecord.setName(record.get(CPANEL_RECINTO.NOMBRE));
        venueRecord.setEntityName(record.get(Tables.CPANEL_ENTIDAD.NOMBRE));
        venueRecord.setOwnerCompany(record.get(CPANEL_RECINTO.EMPRESAPROPIETARIA));
        venueRecord.setManagementCompany(record.get(CPANEL_RECINTO.EMPRESAGESTORA));
        venueRecord.setContactRole(record.get(CPANEL_RECINTO.CARGOCONTACTO));
        venueRecord.setContactName(record.get(CPANEL_RECINTO.NOMBRECONTACTO));
        venueRecord.setContactSurname(record.get(CPANEL_RECINTO.APELLIDOSCONTACTO));
        venueRecord.setContactPhone(record.get(CPANEL_RECINTO.TELEFONOCONTACTO));
        venueRecord.setContactMail(record.get(CPANEL_RECINTO.CORREOCONTACTO));
        venueRecord.setImagePath(record.get(CPANEL_RECINTO.PATHLOGO));
        venueRecord.setCountry(record.get(Tables.CPANEL_PAIS.NOMBRE));
        venueRecord.setCountryCode(record.get(Tables.CPANEL_PAIS.CODIGO));
        venueRecord.setProvince(record.get(Tables.CPANEL_PROVINCIA.NOMBRE));
        venueRecord.setProvinceCode(record.get(Tables.CPANEL_PROVINCIA.CODIGO));
        venueRecord.setPostalCode(record.get(CPANEL_RECINTO.CODIGOPOSTAL));
        venueRecord.setCoordenates(record.get(CPANEL_RECINTO.COORDENADA));
        venueRecord.setGooglePlaceId(record.get(CPANEL_RECINTO.GOOGLEPLACEID));
        venueRecord.setExternalVenueId(record.get(CPANEL_RECINTO.EXTERNALVENUEID));
        return venueRecord;
    }

}
