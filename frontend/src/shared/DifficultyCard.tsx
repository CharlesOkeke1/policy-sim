import { useState } from 'react';
import './DifficultyCard.css';

interface DifficultyProps {
    difficulty: string;
    explanation: string;
    step: number;
    activeStep: number
    onClick: () => void;
};

export const DifficultyCard = ({difficulty, explanation, activeStep, step, onClick}: DifficultyProps) => {
    const border = step === activeStep ? "2px solid var(--btn-primary-bg)" : "1px solid var(--line)";
    return (
        <div className='difficultyCardComponent' style={{border: border}}
        onClick={() => onClick()}>            
            <p className='state_card_header_text'>{difficulty}</p>
            <p className='difficulty_sub'>{explanation}</p>
        </div>
    )
}
