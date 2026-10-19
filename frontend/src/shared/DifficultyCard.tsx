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
    const border = step === activeStep ? "2px solid black" : "1px solid #E4E4E7";
    return (
        <div className='difficultyCardComponent' style={{border: border}}
        onClick={() => onClick()}>            
            <p className='state_card_header_text'>{difficulty}</p>
            <p className='difficulty_sub'>{explanation}</p>
        </div>
    )
}
