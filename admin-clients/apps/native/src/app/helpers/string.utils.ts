export const getInitials = (
    username: string,
    lastname: string
): string => [username, lastname].map(name => name.charAt(0).toUpperCase()).join('');

export const formattedCode = (code: string): string => code.replace(/^.{4}/, '****');

