import React, { useEffect, useState } from 'react';
import './NavBarItem.css'

interface NavBarItemProps {
    onClick: () => void;
    main_text: string;
    iconPath: string;
    step: number;
    current_step: number;
    minimized: boolean;
}

const NavBarItem = ({onClick, main_text: main, iconPath: icon, step: step, current_step: curr, minimized}: NavBarItemProps) => {   
    const width = minimized ? '60%' : '';
    return (
        <div
            className="component"
            style={{
                backgroundColor:
                    step === curr
                        ? "var(--bg-active)"
                        : "var(--auth-link)",
                width: width
            }}
            onClick={() => onClick()}
        >
            <img src={icon} className="icon" />
            {
                minimized ?
                "" : <p className="main">{main}</p>
            }
        </div>
    );
}

export default NavBarItem;