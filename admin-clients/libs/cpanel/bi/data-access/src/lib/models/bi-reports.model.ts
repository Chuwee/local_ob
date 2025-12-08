import { BiImpersonation } from './bi-impersonation.model';

export interface BiReport {
    category: string;
    description: string;
    filtered: boolean;
    id: string;
    name: string;
    subcategory?: string;
    url?: string;
    type: 'DOCUMENT' | 'DOSSIER';
}

export interface GetBiReportsRequest extends BiImpersonation {
    q?: string;
}

export interface BiReportPrompt {
    id: string;
    title: string;
    description: string;
    type: 'ELEMENT' | 'OBJECT' | 'DATE' | 'DATE_RANGE' | 'HIERARCHY';
    restrictions: {
        required: boolean;
        max_selections?: number;
        purchase_date?: boolean;
    };
    default_answers: BiReportPromptAnswer[];
}

export type PostBiReportAnswerRequest = {
    prompt_id: string;
    answers: string[];
    hierarchy_step?: string; //only for hierarchy
    preset?: boolean; //only for date-rage-picker
}[];

export interface PostBiReportAnswerResponse {
    url: string;
}

export interface BiReportPromptAnswer {
    id: string;
    name: string;
}

export interface GetBiReportPromptAnswersRequest extends BiImpersonation {
    q?: string;
    offset?: number;
    limit?: number;
}

export interface BiHistoryReport extends BiReport {
    date: string;
}

export interface GetBiReportsHistoryRequest extends BiImpersonation {
    from?: string;
    to?: string;
}

export interface BiReportPromptHierarchyStep {
    id: string;
    name: string;
}

export interface BiReportPromptHierarchyStepAnswer {
    id: string;
    name: string;
}

export interface GetBiReportPromptHierarchyRequest extends BiImpersonation {
    q?: string;
    offset?: number;
    limit?: number;
    previous?: { step: string; answers: string[] }[];
}

