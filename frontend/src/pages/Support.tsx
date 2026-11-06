import { useState } from 'react';
import { useAuth } from '../services/AuthProvider';
import backButtonIcon from '../assets/left-line.svg';
import '../styles/Support.css';
import { useNavigate } from 'react-router';
import { ButtonLoader } from '../shared/ButtonLoader';
import type { ApiResponse } from '../dtos/Models';
import { customFetch } from '../utils/FetchWrapper';
import { validateEmail } from '../utils/Formatting';
import { FiChevronLeft } from 'react-icons/fi';
import { CustomIcon } from '../shared/Icon';

const Support = () => {
    const {user} = useAuth();
    const router = useNavigate();
    const [senderEmail, setSenderEmail] = useState(user ? user.email : "");
    const [title, setTitle] = useState("");
    const [subject, setSubject] = useState("");
    const [content, setContent] = useState("");
    const [error, setError] = useState("");
    const readyToPost = senderEmail.length > 0 
    && title.length > 0 
    && subject.length > 0
    && content.length> 0
    && validateEmail(senderEmail);
    const [submitted, setSubmitted] = useState(false);

    async function sendEmail() {
        setSubmitted(true);
        const payload = {
            userId: user ? user.id : null,
            senderEmail: senderEmail,
            title: title,
            subject: subject,
            content: content
        }
        const url = "http://localhost:8080/api/users/support";

        const res: ApiResponse<any> = await customFetch({
            method: "POST",
            url: url,
            body: payload
        });

        if (res.code > 200 && res.code < 210) {
            setSubmitted(false);
            router("/home");
        }

        setError(res.error ? res.message: "");        
    }

    function backToApplication() {
        sessionStorage.setItem("active_step", JSON.stringify(0));
        router("/home");
    }

    return (
        <div className='supportPage'>
            <div className='container'>
                <div className='save_and_exit_container backBtn' onClick={() => backToApplication()}>
                    <div style={{marginTop: '3px', cursor: "pointer"}}>
                        <CustomIcon icon={FiChevronLeft} stroke='var(--text-muted)' fill='none' strokeWidth={1} variant="circular" size={"var(--text-stat)"}/>
                    </div>
                    <a className='save_and_exit'>Back to PolicySim</a>
                </div>
                <p className='dashboard_main'>Contact Support</p>
                <p style={{textWrap: 'wrap', fontSize: "var(--text-body)", color: "var(--text-muted)"}}>Found a bug, lost a game, or have an idea? Tell us and we will reply by email.</p>
                <div className='support_container'>
                    <div className='support_input_block'>
                        <p className='support_input_label'>Your email</p>
                        <input 
                        value={senderEmail} 
                        className='support_input'
                        placeholder='you@example.com'
                        type="email"
                        onChange={(e) => setSenderEmail(e.target.value)}/>
                    </div>
                    <div className='support_input_block'>
                        <p className='support_input_label'>Title</p>
                        <input 
                        value={title} 
                        className='support_input'
                        placeholder='e.g. Advance button does nothing in month 142'
                        onChange={(e) => setTitle(e.target.value)}/>
                    </div>
                    <div className='support_input_block'>
                        <p className='support_input_label'>Subject</p>
                        <input 
                        value={subject} 
                        className='support_input'
                        placeholder='How can we help'
                        onChange={(e) => setSubject(e.target.value)}/>
                    </div>
                    <div className='support_input_block'>
                        <p className='support_input_label'>Message</p>
                        <textarea
                            value={content}
                            className="support_input text_area"
                            placeholder="Tell us your concern"
                            onChange={(e) => setContent(e.target.value)}
                        />
                    </div>
                    <button className='send_email_btn' onClick={() => sendEmail()} disabled={!readyToPost}>
                        { submitted ?
                            <ButtonLoader/> : <p>Send email</p>
                        } 
                    </button> 
                    {
                        error ?
                        <div className='error_div'><p>{error}</p></div>  
                            : 
                        <div className='leave_warning'><p>We usually reply within 2 working days</p></div>  
                    }
                </div>
            </div>
        </div>        
    )
}

export default Support