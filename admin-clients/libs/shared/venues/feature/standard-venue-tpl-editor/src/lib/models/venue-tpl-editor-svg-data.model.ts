import { VenueTplEditorSvgTriggerType } from './venue-tpl-editor-svg-trigger-type.enum';

export interface VenueTplSvgData {
    viewId: number;
    svg: string;
    triggerType?: VenueTplEditorSvgTriggerType;
    modify?: boolean;
}

export const defaultSVGOpenTag = '<svg ' +
    'xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:svg="http://www.w3.org/2000/svg" ' +
    'version="1.2" viewBox="0 0 600 450"  preserveAspectRatio="xMidYMid meet">';

export const defaultSVGCloseTag = '</svg>';

export const defaultSVG = defaultSVGOpenTag + defaultSVGCloseTag;
