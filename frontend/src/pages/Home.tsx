import { useEffect, useRef, useState } from 'react'
import { Outlet, useNavigate } from 'react-router';
import '../styles/Home.css';
import lightLogo from '../assets/Policy_Sim_Logo_Light_PNG.png';
import NavBarItem from '../shared/NavBarItem';
import homeIcon from '../assets/home.svg';
import gameIcon from '../assets/game.svg';
import addIcon from '../assets/add.svg';
import trophyIcon from '../assets/trophy.svg';
import compareIcon from '../assets/compare.svg';
import notificationIcon from '../assets/notification.svg';
import dropdownIcon from '../assets/dropdown.svg';
import settingsIcon from '../assets/settings.svg';
import searchIcon from '../assets/search.svg';
import { Dashboard } from '../components/Dashboard';
import { MyGames } from '../components/MyGames';
import { NewGame } from '../components/NewGame';
import { GlobalRankings } from '../components/GlobalRankings';
import { Settings } from '../components/Settings';

const Home = () => {
  const router = useNavigate();
  const [user, setUser] = useState<any>();
  const [activeStep, setActiveStep] = useState(0);

  useEffect(() => {
    if (sessionStorage.getItem("authToken") == null) {
      router("/login");
    }

    getUser();
  }, []);

  async function getUser() {
    await fetch(`http://localhost:8080/api/users/${sessionStorage.getItem("userId")}`, {
      headers: {
        Authorization: `Bearer ${sessionStorage.getItem("authToken")}`
      }
    }).then((data) => {
      return data.json();
    }).then((response) => {
      setUser(response.result);
    })
  }
  
  return (
    <div className='home_container'>
      <div className='left_nav_bar'>
        <div className='logoBlock'>
          <img src={lightLogo} className='logoImg'/>
          <div>
            <p className='main'>POLICYSIM</p>
            <p className='supporting'>Build. Govern. Prosper.</p>
          </div>
        </div>
        <NavBarItem onClick={() => setActiveStep(0)} step={0} current_step={activeStep} iconPath={homeIcon} main_text='Dashboard'/>
        <NavBarItem onClick={() => setActiveStep(1)} step={1} current_step={activeStep} iconPath={gameIcon} main_text='My Games' />
        <NavBarItem onClick={() => setActiveStep(2)} step={2} current_step={activeStep} iconPath={addIcon} main_text='Play New Game' />
        <NavBarItem onClick={() => setActiveStep(3)} step={3} current_step={activeStep} iconPath={trophyIcon} main_text='Global Rankings' />
        <NavBarItem onClick={() => setActiveStep(4)} step={4} current_step={activeStep} iconPath={settingsIcon} main_text='Settings' />
      </div>
      <div className='main_app'>
        <div className='header'>
          <div className='input_container'>
            <img src={searchIcon} className='search_icon'/>
            <input className='search_input' 
            placeholder='Search for a game or stats'/>
          </div>
          
          <div className='info_section'>
            <img src={notificationIcon} className='icon'/>
            <div className='user_section'>
              <div className='user'>
                <div className='nameIcon'>
                  <p className='user_header_text'>
                    { user ? user.name.charAt(0).toUpperCase() : ""}
                  </p>
                </div>
                <div className='u'>
                  <p className='u_name'>
                    { user ? user.name.split(" ")[0] : "N/A"}
                  </p>
                  <img src={dropdownIcon} className='icon dropIcon'/>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div className='active_component'>
          <Outlet/>
          {(() => {
            switch(activeStep) {
              case 0:
                return <Dashboard/>
              case 1:
                return <MyGames/>
              case 2:
                return <NewGame/>
              case 3:
                return <GlobalRankings/>
              case 4:
                return <Settings/>
            }
          })()}
        </div>
      </div>
    </div>
  )
}

export default Home;