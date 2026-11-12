import './TextArea.css';

interface TextAreaProps {
    size: "sm" | "md" | "lg" | "xl";
    value: string;
    onTextChange: (e: any) => void;
    placeholder: string;
    type?: string;
}

const TextArea = ({size, value, placeholder, type, onTextChange}: TextAreaProps) => {
    let height = "";

    switch(size) {
        case "sm":
            height = "50px";
            break;
        case "md":
            height = "80px";
            break;
        case "lg":
            height = "110px";
            break;
        case "xl":
            height = "150px";
            break;
    }
    return (
        <div className='input_container'>
            <input
                value={value}
                placeholder={placeholder}
                className='text_input'
                type={type ? type : "text"}
                onChange={(e) => onTextChange(e.target.value)}
                style={{height: height}}
            />
        </div>
    )
}

export default TextArea