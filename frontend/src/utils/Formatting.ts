export function formatDate(date: string): string {
    return new Date(date).toLocaleDateString("en-GB", {
        day: "2-digit",
        month: "short",
        timeZone: "UTC"
    });
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
