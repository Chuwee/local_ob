export type IngestorConfiguration = {
    excluded_sections: string[];
    general_admission: string[];
    enabled: boolean;
    ingestor_sources: IngestorSourcesType[];
    schedule_load_type: string[];
};

export type PutIngestorConfigurationRequest = Partial<IngestorConfiguration>;

export const ingestorSources = ['DEFAULT', 'FAVORITES', 'NEXT_HOURS', 'SPLITTED', 'ON_DEMAND', 'MAPPING_INGEST'] as const;
export type IngestorSourcesType = typeof ingestorSources[number];

export const sourcesDependingDefault = ['FAVORITES', 'NEXT_HOURS', 'SPLITTED'];

export const ingestorScheduleLoadType = ['FAVORITES', 'NEXT_HOURS', 'SPLITTED'] as const;
export type IngestorScheduleLoadType = typeof ingestorScheduleLoadType[number];

export enum IngestorStatus {
    active = 'ACTIVE',
    disabled = 'DISABLED'
}
