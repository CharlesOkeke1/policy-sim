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
}

export const ThemeSelector = ({currentTheme, theme, onClick}: ThemeProps) => {
    const style: ThemeEl = currentTheme === theme ? {
        fontWeight: "var(--weight-medium)",
        bgColor: "white",
        border: "1px solid var(--line)"
    } : { fontWeight: "", bgColor: "", border: "" };

    function getThemeIcon(theme: string) {
        const t = theme.toLowerCase().trim();
        let grad = "linear-gradient(to right, white 0% 49%, black 51% 100%)";

        switch(t) {
            case 'light':
                return 'white';

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
            <p>{theme}</p>
        </div>
    )
}
