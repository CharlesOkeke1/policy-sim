import '../styles/NotFoundPage.css';
import lightLogo from '../assets/Policy_Sim_Logo_Light_PNG.png';
import { useNavigate } from 'react-router';

const NotFoundPage = () => {
  const router = useNavigate();

  function navToStep(step: number) {
    sessionStorage.setItem("active_step", JSON.stringify(step));
    router("/home");
  }

  return (
    <div className='not_found_page'>
      <div className='not_found_container'>
        <div className='not_found_logo'>
          <img src={lightLogo} className='logoImg not_found_logo_icon'/>
          <p>POLICYSIM</p>
        </div>
        <p className='four_oh_four'>404</p>
        <p className='not_found_header'>This page is not on the map</p>
        <p className='not_found_subheader'>The link may be old, or the page may have moved. None of your games are affected.</p>
        <div className='buttonRow' style={{width: "75%"}}>
          <button className='PauseBtnContainer' onClick={() => navToStep(0)}>
            <p>Back to dashboard</p>
          </button>
          <button className='PauseBtnContainer save_pause_container' onClick={() => navToStep(1)}>
            <p>My games</p>
          </button>
        </div>
        <p className='contact_support_404' onClick={() => router("/support")}>Think this is a mistake? Contact support</p>
      </div>
    </div>
  )
}

export default NotFoundPage;