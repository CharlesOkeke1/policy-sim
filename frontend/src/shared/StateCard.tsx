import { act, useState } from 'react';
import './StateCard.css'; 

interface StateCardProps {
    luck: "Even start" | "Tough start" | "Strong start";
    stateName: string;
    population: number;
    productivity: number;
    stability: number;
    activeStep: number;
    step: number;
    onClick: () => void;
}

export const StateCard = ({luck, stateName, population, productivity, stability,
    onClick, step, activeStep}: StateCardProps) => {

    // I mean i can do this because population can, at the moement only be between this range
    const formatPopulation = (population: number): string => {
        if (population < 1) return population * 1000 + "K";
        else return population  + "M";
    };
    const border = step === activeStep ? "2px solid black" : "";
    let color = "";

    switch (luck) {
        case "Strong start": 
            color = "var(--status-good)";
            break;
        case "Even start": 
            color = "var(--status-warn)";
            break;
        case "Tough start": 
            color = "var(--status-bad)";
            break;
    }

    return (
        <div className='stateCardComponent' style={{border: border}} onClick={() => onClick()}>
            <div className='state_card_header'>
                <p className='state_card_header_text'>{stateName}</p>
                <div className='circular_container'>
                    <input 
                    type='checkbox' 
                    className='circular_checkbox'
                    style={{cursor: 'pointer'}}
                    checked={step === activeStep}
                    onChange={() => onClick()}/>
                </div>
            </div>
            <p className='state_luck' style={{color: color}}>{luck}</p>
            <div className='state_card_stats'>
                <div className='state_card_stat'>
                    <p className='state_card_stat_label'>Population</p>
                    <p className='state_card_stat_value'>{formatPopulation(population)}</p>
                </div>
                <div className='state_card_stat'>
                    <p className='state_card_stat_label'>Productivity</p>
                    <p className='state_card_stat_value'>{productivity}</p>
                </div>
                <div className='state_card_stat'>
                    <p className='state_card_stat_label'>Stability</p>
                    <p className='state_card_stat_value'>{stability}%</p>
                </div>
            </div>
        </div>
    )
}
