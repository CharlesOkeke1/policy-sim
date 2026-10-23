import './PolicyCard.css';

interface Policy {
    name: string;
    policyId: string
    effect: number;
    effectOnStability: number;
    activePolicyId: string;
    affectedLever: string;
    onClick: () => void;
}

const PolicyCard = ({name, policyId, effect, effectOnStability, 
    activePolicyId, affectedLever, onClick}: Policy) => {
    const border = policyId === activePolicyId ? "2px solid black" : "";

    const sign = effect > 0 ? "+" : ""; 
    const stabilitySign = effectOnStability > 0 ? "+" : "";

    function getColor(effect: number) {
        if (effect > 0) return "var(--status-good)";
        else if (effect === 0) return "var(--text-secondary)";

        return "var(--status-bad)";
    }

    const lever = affectedLever === "None" ? "No effect" : affectedLever;

    return (
        <div className='policyCardComponent' style={{border: border}} onClick={() => onClick()}>
            <div className='policy_card_header'>
                <p className='policy_card_header_text'>{name}</p>
                <div className='circular_container'>
                    <input 
                    type='checkbox' 
                    className='circular_checkbox'
                    style={{cursor: 'pointer'}}
                    checked={policyId === activePolicyId}
                    onChange={() => onClick()}/>
                </div>
            </div>
            <p className='state_luck' style={{color: getColor(effect)}}>{lever} {sign}{effect}%</p>
            <p className='state_card_stat_value' style={{color: getColor(effectOnStability)}}>{stabilitySign}{effectOnStability} stability</p>
        </div>
    )

}

export default PolicyCard;