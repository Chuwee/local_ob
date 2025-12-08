import axios from 'axios';
import * as fs from 'node:fs';
import * as path from 'node:path';

const literalsUrl = 'https://client-dists.oneboxtds.com/cpanel-client-translations/staging/cpanel/en-US.json';
const resultFile = 'pruned-i18n.json';
const projectPaths = ['../../apps', '../../libs'];
const fileExtensions = ['.ts', '.html', '.js'];

const collectionKeysSuffix = '_OPTS';

const keysToExclude = [
    'API_ERRORS',
    'CHANNELS.BOXOFFICE_CONTENTS_IDS',
    'CHANNELS.PORTAL_CONTENTS_IDS',
    'CHANNELS.DELIVERY_METHODS.LIST.OPTS',
    'CHANNELS.DELIVERY_METHODS.EMAIL_CONTENT.OPTS',
    'CHANNELS.EXTERNAL_TOOLS.GTM',
    'CHANNELS.OPTIONS.FORM_FIELDS',
    'CHANNELS.PAYMENT_METHODS.GATEWAY_FIELDS',
    'ENUMS',
    'EVENTS.CHANNEL.SALE_STATUS',
    'EVENTS.SESSION.AVAILABILITY_STATUS',
    'FORMS.ERRORS',
    'ORDER.BUYER_DATA.PROFILE_ATTRIBUTES.FIELD',
    'ORDER.PAYMENT.TYPE',
    'SEASON_TICKET.NOT_LINKED_REASON',
    'TICKET.SEAT_TYPE',
    'USER.ROLES',
    'USER.ROLE_DESCRIPTIONS',
    'USER.PERMISSION_DESCRIPTIONS',
    'USER.MODULES_AVAILABLE',
    'VENUE_TPLS.SEAT_STATUS',
    'VENUE_TPLS.SEAT_STATUS_PLURAL',
    'CUSTOMER.MANAGEMENT_TYPE',
    'EVENTS.CHANNEL.RELEASE_STATUS',
    'EVENTS.CHANNEL.RELEASE_STATUS',
    'EVENTS.SESSION.RELEASE_STATUS',
    'EVENTS.SESSION.SALE_STATUS',
    'EVENTS.SESSION.TIME_UNITS',
    'EVENTS.TIME_UNITS',
    'LOGIN.ACCOUNT_BLOCKED',
    'TICKET.VISIBILITY',
    'USER.ADDITIONAL_PERMISSIONS',
    'PRODUCER.INVOICES.PROVIDERS.REQUEST.STATUS',
    'TICKET_PASSBOOK.FIELDS',
    'ATTENDANT_FIELD',
    'AGGREGATED_METRIC.METRIC',
    'VOUCHER.TRANSACTION_TYPE',
    'GIFT_CARD.EXPIRATION.TIME_UNITS',
    'VENUE.TYPE_OPTIONS',
    'CURRENCIES',
    'EVENTS.RESTRICTIONS_FIELDS',
    'PROFESSIONAL_SELLING.CONDITIONS.COND_HIERARCHICAL_LEVEL_TYPE',
    'MEMBER_EXTERNAL.ADVANCED.TYPES',
    'MEMBER_EXTERNAL.ADVANCED.OPERATIONS',
    'MEMBER_EXTERNAL.ADVANCED.IMPLEMENTATION_DESCRIPTION',
    'MEMBER_EXTERNAL.ADVANCED.FIELD_LABELS',
    'MEMBER_EXTERNAL.ADVANCED.FIELD_LABELS',
    'MEMBER_EXTERNAL.RESTRICTIONS.FIELDS',
    'MEMBER_EXTERNAL.RESTRICTIONS.PLACEHOLDER',
    'PRODUCT.DELIVERY.DATE_UNIT'];

type Literals = {
    [key: string]: string | Literals;
};

async function main(): Promise<void> {
    const literals: Literals = await loadLiterals();
    const allKeys = getAllKeys(literals);
    const [keys, collectionKeys] = divideKeys(allKeys);
    console.log('Simple keys: ', keys.length, '\nCollection keys: ', collectionKeys.length);
    //reg exps
    const regExp = getRegExps(keys, collectionKeys);
    const files = projectPaths.flatMap(projectPath => getFiles(projectPath));
    console.log('\nAnalysis started:');
    const [simpleUsedKeys, colUsedKeys] = searchUsedKey(files, regExp.simpleRegExps, regExp.colRegExps);
    console.log('\nUnused literals:\n ');
    const simpleUnusedKeys = keys.filter(key => !simpleUsedKeys.has(key));
    console.log(simpleUnusedKeys.join('\n'));
    console.log('\nUnused col literals:\n ');
    const colUnusedKeys = collectionKeys.filter(key => !colUsedKeys.has(key));
    console.log(colUnusedKeys.join('\n'));
    console.log(
        '\nResults:\nTotal keys: ', allKeys.length,
        '\nSimple keys: ', simpleUsedKeys.size, '/', keys.length,
        '\nCol keys: ', colUsedKeys.size, '/', collectionKeys.length,
        '\n '
    );
    removeLiterals(literals, simpleUnusedKeys.concat(colUnusedKeys));
    return saveFile(literals);
}

async function loadLiterals(): Promise<Literals> {
    try {
        const response = await axios.get(literalsUrl);
        return response.data as Literals;
    } catch (error) {
        console.error('Error loading literals:', error);
        process.exit(1);
    }
}

function getAllKeys(literals: Literals): string[] {
    return Object.keys(literals).flatMap(key => {
        if (typeof literals[key] === 'string') {
            return [key];
        } else {
            return getAllKeys(literals[key]).map(subKey => key + '.' + subKey);
        }
    });
}

function divideKeys(allKeys: string[]): [string[], string[]] {
    const colKeys = new Set<string>(keysToExclude);
    const keys: string[] = [];
    allKeys.forEach(key => {
        if (key.includes(collectionKeysSuffix)) {
            colKeys.add(key.slice(0, key.indexOf(collectionKeysSuffix) + collectionKeysSuffix.length))
        }
    });
    const collectionKeysArray: string[] = Array.from(colKeys);
    allKeys.forEach(key => {
        if (!collectionKeysArray.some(colKey => key.startsWith(colKey))) {
            keys.push(key);
        }
    });
    return [keys, Array.from(colKeys)];
}

function getRegExps(simpleKeys: string[], colKeys: string[]): { simpleRegExps: Map<string, RegExp>, colRegExps: Map<string, RegExp> } {
    return {
        simpleRegExps: new Map<string, RegExp>(simpleKeys.map(key => [key, new RegExp(`['\`]${key}['\`]`, 'g')])),
        colRegExps: new Map<string, RegExp>(colKeys.map(key => [key, new RegExp(`['\`]${key}`, 'g')]))
    }
}

function searchUsedKey(files: string[], simpleRegExps:Map<string, RegExp>,colRegExps: Map<string, RegExp>): [Set<string>, Set<string>] {
    const simpleUsedKeys = new Set<string>();
    const colUsedKeys = new Set<string>();
    let lastFolderPath: string;
    let folderPath: string;
    let content: string;
    files.forEach(file => {
        folderPath = getSourcePath(file);
        if (folderPath !== lastFolderPath) {
            lastFolderPath = folderPath;
            console.log(` ${folderPath} (found: ${simpleUsedKeys.size + colUsedKeys.size})`);
        }
        content = fs.readFileSync(file, 'utf-8');
        simpleRegExps.forEach((regExp, key) => {
            if (regExp.test(content)) {
                simpleRegExps.delete(key);
                simpleUsedKeys.add(key);
            }
        })
        colRegExps.forEach((regExp, key) => {
            if (regExp.test(content)) {
                colRegExps.delete(key);
                colUsedKeys.add(key);
            }
        })
    });
    return [simpleUsedKeys, colUsedKeys];
}

function getFiles(dir: string): string[] {
    let results: string[] = [];
    const list = fs.readdirSync(dir);
    list.forEach(file => {
        const filePath = path.join(dir, file);
        const stat = fs.statSync(filePath);
        if (stat && stat.isDirectory()) {
            results = results.concat(getFiles(filePath));
        } else if (fileExtensions.includes(path.extname(file))) {
            results.push(filePath);
        }
    });
    return results;
}

function getSourcePath(file: string): string {
    const folderPathParts = path.dirname(file).split('\\');
    const srcElementIndex = folderPathParts.indexOf('src')
    const limitedFolderPathParts = srcElementIndex !== -1 ? folderPathParts.slice(0, srcElementIndex + 1) : folderPathParts;
    return limitedFolderPathParts.join('\\');
}

function removeLiterals(literals: Literals, unusedKeys: string[]): void {
    unusedKeys.forEach(key => {
        const keyParts = key.split('.');
        const lastPart = keyParts.pop();
        let lastRoot = literals;
        keyParts.forEach(keyPart => lastRoot = lastRoot && lastRoot[keyPart] as Literals);
        if (lastRoot) {
            delete lastRoot[String(lastPart)];
        }
    });
    deleteEmptyNodes(literals);
}

function deleteEmptyNodes(literals: Literals | string): boolean {
    if (typeof literals === 'string') {
        return !literals?.length;
    } else {
        const fields = Object.keys(literals);
        if (fields.length) {
            fields.forEach(field => {
                if (deleteEmptyNodes(literals[field])) {
                    delete literals[field];
                }
            });
            return Object.keys(literals).length === 0;
        } else {
            return true;
        }
    }
}

function saveFile(literals: Literals): Promise<void> {
    return new Promise((resolve, reject) => {
        fs.writeFile(resultFile, JSON.stringify(literals, null, 4), (err) => {
            if (err) {
                console.error('Error writing file:', err);
                reject(err);
            } else {
                console.log('File written');
                resolve();
            }
        });
    });
}

console.log('start');
main().then(() =>
    console.log('end')
);
