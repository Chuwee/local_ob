export const safeList = [
    'sm:overflow-auto',
    {
        pattern: /col-span-(1|2|3|4|5|6|7|8|9|10|11|12)/,
        variants: ['sm', 'md', 'lg']
    },
    {
        pattern: /basis-([1-12]|60|72|\[\d*px\])/,
        variants: ['sm', 'md', 'lg']
    },
    {
        pattern: /gap-6/,
        variants: ['sm', 'md', 'lg']
    },
    {
        pattern: /gap-4/,
        variants: ['sm', 'md', 'lg']
    },
    {
        pattern: /flex-row/,
        variants: ['sm', 'md', 'lg']
    }
];
