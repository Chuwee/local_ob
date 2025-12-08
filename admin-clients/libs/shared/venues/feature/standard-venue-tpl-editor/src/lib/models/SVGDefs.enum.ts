// eslint-disable-next-line @typescript-eslint/naming-convention
export const SVGDefs = {
    attributes: {
        id: 'id',
        version: 'version',
        width: 'width',
        height: 'height',
        viewBox: 'viewBox',
        transform: 'transform',
        class: 'class',
        svgjsNS: 'svgjs',
        namespaceDef: 'xmlns'
    },
    attributeValues: {
        version: '1.2',
        neutralMatrixTransform: 'matrix(1,0,0,1,0,0)'
    },
    nodeTypes: {
        seat: 'circle',
        group: 'g',
        nnz: 'g',
        link: 'g',
        rowLabel: 'text'
    },
    classes: {
        interactive: 'interactive',
        dragMove: 'drag-move'
    },
    colors: {
        defaultStroke: '#333333',
        defaultFill: '#A9A9A9',
        text: '#000000'
    },
    maxSVGLength: 2000000
};
