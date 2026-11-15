export function formatDate(date: string): string {
    return new Date(date).toLocaleDateString("en-GB", {
        day: "2-digit",
        month: "short",
        timeZone: "UTC"
    });
}

export function formatDateFull(date: string): string {
    return new Date(date).toLocaleDateString("en-GB", {
        day: "2-digit",
        month: "short",
        hour: "2-digit",
        minute: "2-digit",
        second: "2-digit",
        timeZone: "UTC"
    });
}


export function getSecondsBetweenDates(date1: string, date2: string): number {
    const start = new Date(date1).getTime();
    const end = new Date(date2).getTime();

    return Math.abs(end - start) / 1000;
}

export function formatSecondsBetweenDates(secondsAgo: number): string {
    if (secondsAgo <= 0) return "expected now";

    if (secondsAgo > 0 && secondsAgo < 60) {
        return `${secondsAgo}s`;
    }

    const minutesAgo = Math.floor(secondsAgo / 60);
    const secondsPart = secondsAgo - (minutesAgo * 60);
    if (minutesAgo < 60) {
        return `${minutesAgo}min ${secondsPart}s`;
    }

    const hoursAgo = Math.floor(minutesAgo / 60);
    const minsPart = minutesAgo - (hoursAgo * 60);
    if (hoursAgo < 24) {
        return `${hoursAgo}h ${minsPart}min ${secondsPart}s`;
    }
    
    const daysAgo = Math.floor(hoursAgo / 24);
    const hoursPart = hoursAgo - (daysAgo * 24);
    return `${daysAgo}d ${hoursPart}h ${minsPart}min ${secondsPart}s`;
    
}

export function formatDateSeconds(date: string): string {
    const timestamp = new Date(date).getTime();
    const secondsAgo = Math.floor((Date.now() - timestamp) / 1000);

    if (secondsAgo <= 0) return "just now";

    if (secondsAgo > 0 && secondsAgo < 60) {
        return `${secondsAgo}s ago`;
    }

    const minutesAgo = Math.floor(secondsAgo / 60);
    if (minutesAgo < 60) {
        return `${minutesAgo}min ago`;
    }

    const hoursAgo = Math.floor(minutesAgo / 60);
    if (hoursAgo < 24) {
        return `${hoursAgo}h ago`;
    }

    const daysAgo = Math.floor(hoursAgo / 24);
    return `${daysAgo}d ago`;
}

export const validateEmail = (email: string): boolean => {
    const regex = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(?:\.[A-Za-z0-9-]+)*\.[A-Za-z]{2,}$/;
    return regex.test(email);
}