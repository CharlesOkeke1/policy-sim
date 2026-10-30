import './GameTableRow.css';

interface RowProps {
    rank: number;
    name: string;
    score: number;
    realGrowth: number;
    inflation: number;
    unemployment: number;
    debt: number;
    gdp: number;
    stability: number;
    lastPolicy: string;
    userStateName: string;
}

export const GameTableRow = () => {
    return (
        <div>GameTableRow</div>
    )
}
