import React from 'react';
import { type IconType } from 'react-icons';

interface CustomIconProps extends React.SVGProps<SVGSVGElement> {
  // Accepts the React Icon component itself
  icon: IconType; 
  size?: string | number;
  color?: string;
  variant?: 'default' | 'circular' | 'square';
  fill?: string;
}

export const CustomIcon: React.FC<CustomIconProps> = ({
    icon: IconComponent,
    size,
    color,
    variant = 'default',
    className = '',
    fill,
    ...props
}) => {
    const baseWrapperClass = "inline-flex items-center justify-center";
    const variantClasses = {
        default: "",
        circular: "rounded-full bg-gray-100 p-2",
        square: "rounded-md bg-gray-100 p-2",
    };

    return (
        <span className={`${baseWrapperClass} ${variantClasses[variant]} ${className}`}>
        <IconComponent 
            style={{overflow: 'visible'}}
            size={size} 
            fill={fill ? fill : "none"}
            stroke={color ? color : "var(--text-primary)"} 
            {...props} 
        />
        </span>
    );
};
