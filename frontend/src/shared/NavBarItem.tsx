import React, { useEffect, useState } from 'react';
import './NavBarItem.css'
import type { IconType } from 'react-icons';
import { CustomIcon } from './Icon';
import { MdSettings } from 'react-icons/md';

interface NavBarItemProps {
    onClick: () => void;
    main_text: string;
    iconPath: IconType;
    step: number;
    icon_size?: number;
    current_step: number;
    minimized: boolean;
    fill?: string;
    stroke?: number;
}

const NavBarItem = ({onClick, main_text: main, iconPath: icon, 
    step: step, current_step: curr, minimized, icon_size, fill,
    stroke}: NavBarItemProps) => {   
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
        <div style={{marginTop: "5px"}}>
            <CustomIcon icon={icon} strokeWidth={stroke ? stroke : 30} fill={fill} variant="circular" size={icon_size ? icon_size : "var(--text-h1)"}/>
        </div>
            {
                minimized ?
                "" : <p className="main">{main}</p>
            }
        </div>
    );
}

export default NavBarItem;