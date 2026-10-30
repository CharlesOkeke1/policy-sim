import './LengthElement.css';

interface LengthEl {
    step: number,
    activeStep: number;
    years: number;
    onClick: () => void;
}

export type Length = {
  color?: string;
  fontWeight: string;
  bgColor: string;
}


export const LengthElement = ({step, activeStep, years, onClick}: LengthEl) => {
    const style: Length = step === activeStep ? {
        color: "var(--btn-primary-text)",
        fontWeight: "var(--weight-medium)",
        bgColor: "var(--btn-primary-bg)"
    } : { color: "", fontWeight: "", bgColor: ""  };
    return (
        <div className='lengthElementComponent'
        style={{color: style.color, backgroundColor: style.bgColor,
        fontWeight: style.fontWeight}} onClick={() => onClick()}>
            <p>{years} years</p>
        </div>
    )
}
