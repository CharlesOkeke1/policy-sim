import type { Length } from './LengthElement';
import './ThemeSelector.css';

interface ThemeProps {
    currentTheme: string;
    theme: string;
    onClick: () => void;
}

type ThemeEl = {
  fontWeight: string;
  bgColor: string;
  border: string;
  color: string;
}

export const ThemeSelector = ({currentTheme, theme, onClick}: ThemeProps) => {
    const style: ThemeEl = currentTheme === theme ? {
        fontWeight: "var(--weight-medium)",
        bgColor: theme.toLowerCase() === "light" ? "black" : "white",
        border: "1px solid var(--line)",
        color: theme.toLowerCase() === "light" ? "white" : ""
    } : { fontWeight: "", bgColor: "", border: "", color: "" };

    function getThemeIcon(theme: string) {
        const t = theme.toLowerCase().trim();
        let grad = "linear-gradient(to right, white 0% 49%, black 51% 100%)";

        switch(t) {
            case 'light':
                return 'white';2

            case 'dark':
                return 'black';
            
            case 'system':
                return grad;
        }

    }
    return (
        <div className='theme_object' onClick={() => onClick()}
        style={{fontWeight: style.fontWeight, backgroundColor: style.bgColor,
            border: style.border}}>
            <div className='theme_icon' style={{background: getThemeIcon(theme)}}></div>
            <p style={{color: style.color}} className='text_theme'>{theme}</p>
        </div>
    )
}
