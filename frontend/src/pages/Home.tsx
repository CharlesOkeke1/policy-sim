import { useEffect, useState } from 'react'
import { Outlet, useNavigate } from 'react-router';
import '../styles/Home.css';
import lightLogo from '../assets/Policy_Sim_Logo_Light_PNG.png';
import darkLogo from '../assets/Policy_Sim_Logo_Dark_PNG.png';
import NavBarItem from '../shared/NavBarItem';
import logoutIcon from '../assets/round-logout.svg';
import searchIcon from '../assets/search.svg';
import { Dashboard } from '../components/Dashboard';
import { MyGames } from '../components/MyGames';
import { NewGame } from '../components/NewGame';
import { GlobalRankings } from '../components/GlobalRankings';
import { Settings } from '../components/Settings';
import { ThemeSelector } from '../shared/ThemeSelector';
import { useAuth } from '../services/AuthProvider';

import { type IconType } from 'react-icons';
import { 
  FaAngleDown, 
  FaAngleUp, 
  FaBell, 
  FaChevronLeft, 
  FaChevronRight, 
  FaGamepad, 
  FaGear, 
  FaHouse, 
  FaPlus, 
  FaTrophy, 
  FaUser 
} from 'react-icons/fa6';
import { CustomIcon } from '../shared/Icon';
import { IoHelpOutline } from 'react-icons/io5';
import { GoTrophy } from 'react-icons/go';
import { PiTrophy } from 'react-icons/pi';

export interface ChildProps {
  setStep: (num: number) => void;
  setMin: (val: boolean) => void;
}

type Theme = 'Light' | 'Dark' | 'System';

const Home = () => {
  const router = useNavigate();
  const {user, logout} = useAuth();
  const [showUserDropdown, setShowUserDropdown] = useState(false);
  const [currentTheme, setCurrentTheme] = useState<Theme>(document.documentElement.getAttribute("data-theme") as Theme || "Light");
  const [activeStep, setActiveStep] = useState(Number(sessionStorage.getItem("active_step")) ?? 0);
  const [navBarMinimized, setNavBarMinimized] = useState(false);
  //const [ActiveComponent, setActiveComponent] = useState(<Dashboard setStep={setActiveStep}/>);
  const navBarMinimizeIcon: IconType = navBarMinimized ? FaChevronRight : FaChevronLeft;
  const [windowWidth, setWindowWidth] = useState(window.innerWidth);

  useEffect(() => {
    function handleResize() {
      const width = window.innerWidth;
      setWindowWidth(width);

      if (width < 900) {
        setNavBarMinimized(true);
      } else {
        setNavBarMinimized(false);
      }
    }

    handleResize(); 
    window.addEventListener("resize", handleResize);
    return () => window.removeEventListener("resize", handleResize);
  }, []);

  function closeUserDrop() {
    if (showUserDropdown) {
      setShowUserDropdown(false);
    }
  }

  function setTheme(theme: typeof currentTheme) {
    if (theme.toLocaleLowerCase() === "system") {
      const isDarkMode = window.matchMedia('(prefers-color-scheme: dark)').matches;
      const systemTheme = isDarkMode ? 'Dark' : 'Light';
      setCurrentTheme("System");
      document.documentElement.setAttribute("data-theme", systemTheme.toLowerCase());
    } else {
      setCurrentTheme(theme);
      document.documentElement.setAttribute("data-theme", theme.toLowerCase());
    }  
  }

  // async function getUser() {
  //   const url = `http://localhost:8080/api/users/${sessionStorage.getItem("userId")}`;
  //   const res: any = await customFetch({method: "GET", url: url});
  //   setUser(res.result);
  // }

  function logoutUser() {
    logout();
  }

  function pushActiveStep(step: number) {
    setActiveStep(step);
    sessionStorage.setItem("active_step", JSON.stringify(step))
  }

  const minJustify = navBarMinimized ? "center" : "flex-end";
  return (
    <>
    <div className='home_container' onClick={() => closeUserDrop()}>
      <div className='left_nav_bar'>
        <div className='nav_bar_items'>
          <div className='logoBlock'>
            <img src={currentTheme.toLocaleLowerCase() === "light" ? lightLogo : darkLogo} className='logoImg'/>
            {
              navBarMinimized ? 
              "" :
              <div>
                <p className='main'>POLICYSIM</p>
                <p className='supporting'>Build. Govern. Prosper.</p>
              </div>

            }
          </div>
          <NavBarItem minimized={navBarMinimized} onClick={() => pushActiveStep(0)} step={0} current_step={activeStep} iconPath={FaHouse} main_text='Dashboard'/>
          <NavBarItem minimized={navBarMinimized} onClick={() => pushActiveStep(1)} step={1} current_step={activeStep} iconPath={FaGamepad} main_text='My Games' />
          <NavBarItem minimized={navBarMinimized} onClick={() => pushActiveStep(2)} step={2} current_step={activeStep} iconPath={FaPlus} main_text='Play New Game' 
          fill='var(--text-primary)' stroke={10}/>
          <NavBarItem minimized={navBarMinimized} onClick={() => pushActiveStep(3)} step={3} current_step={activeStep} iconPath={PiTrophy} main_text='Global Rankings' 
          fill='var(--text-primary)' stroke={1}/>
          <NavBarItem minimized={navBarMinimized} onClick={() => pushActiveStep(4)} step={4} current_step={activeStep} iconPath={FaGear} main_text='Settings' />
        </div>
        
        <div style={{justifyContent: minJustify}} className='minimizeBtnContainer'>
          <div className='minimizeBtn' onClick={() => setNavBarMinimized(!navBarMinimized)}>
            <div style={{marginRight: '3px'}}>
              <CustomIcon icon={navBarMinimizeIcon} fill='var(--text-primary)' strokeWidth={20} variant="circular" size={"var(--text-h1)"}/>
            </div>
          </div>
        </div>
      </div>
      <div className='main_app'>
        <div className='header'>
          <div className='input_container'>
            <img src={searchIcon} className='search_icon'/>
            <input className='search_input' 
            placeholder='Search for a game or stats'/>
          </div>
          
          <div className='info_section'>
            <div style={{marginTop: '3px'}}>
              <CustomIcon icon={FaBell} strokeWidth={30} fill='none' variant="circular" size={"var(--text-stat)"}/>
            </div>
            <div className='user_section'>
              <div className='user'>
                <div className='nameIcon'>
                  <p className='user_header_text'>
                    { user ? user.name.charAt(0).toUpperCase() : ""}
                  </p>
                </div>
                <div className='u' onClick={() => setShowUserDropdown(!showUserDropdown)}>
                  <p className='u_name'>
                    { user ? user.name.split(" ")[0] : "N/A"}
                  </p>
                  <div className='dropIcon'>
                    <CustomIcon icon={showUserDropdown ? FaAngleUp : FaAngleDown} strokeWidth={20} fill='var(--text-primary)' variant="circular" size={"var(--text-stat)"}/>
                  </div>
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
                return <Dashboard setMin={setNavBarMinimized} setStep={setActiveStep}/>
              case 1:
                return <MyGames setMin={setNavBarMinimized} setStep={setActiveStep}/>
              case 2:
                return <NewGame setStep={setActiveStep} setMin={setNavBarMinimized}/>
              case 3:
                return <GlobalRankings/>
              case 4:
                return <Settings/>
            }
          })()}
        </div>
      </div>
    </div>

    {
      showUserDropdown ? 
        <div>
          <div className='userdrop_container' style={{marginTop: '30px'}}>
            <div className='userdrop_user_row userdrop_row'>
              <div className='userdrop_user_icon'>
                <p className='user_header_text'>
                  { user ? user.name.charAt(0).toUpperCase() : ""}
                </p>
              </div>
              <div>
                {/**This implementation is temporary, Sign up will probably ask for a name 
                 * as well Or maybe via settings the user can change it */}
                <p className='user_drop_name'>{ user ? user.name.split("_")[0] : ""}</p> 
                <p className='user_drop_username'>@{ user ? user.name : ""}</p>
              </div>
            </div>
            <div className='userdrop_column'>
              <div className='userdrop_profile'>
                <div style={{marginTop: '5px'}}>
                  <CustomIcon icon={FaUser} strokeWidth={40} fill='none' variant="circular" size={"var(--text-logo-mark)"}/>
                </div>
                <p>Profile</p>
              </div>

              <div className='userdrop_profile'>
                <div style={{marginTop: '5px'}}>
                  <CustomIcon icon={IoHelpOutline} strokeWidth={10} fill='black' variant="circular" size={"var(--text-logo-mark)"}/>
                </div>
                <p>How to play</p>
              </div>

              <div className='userdrop_profile' onClick={() => router("/support")}>
                <div style={{marginTop: '5px'}}>
                  <CustomIcon icon={IoHelpOutline} strokeWidth={10} fill='black' variant="circular" size={"var(--text-logo-mark)"}/>
                </div>
                <p>Contact support</p>
              </div>
            </div>
            <div className='userdrop_column theme_selector'>
              <p className='user_drop_username'>Theme</p>
              <div className='them_container'>
                <ThemeSelector theme='Light' currentTheme={currentTheme} onClick={() => setTheme("Light")}/>
                <ThemeSelector theme='Dark' currentTheme={currentTheme} onClick={() => setTheme("Dark")}/>
                <ThemeSelector theme='System' currentTheme={currentTheme} onClick={() => setTheme("System")}/>
              </div>
            </div>
            <div className='userdrop_row logout' >
              <img src={logoutIcon}/>
              <p>Logout</p>
            </div>
          </div>
        </div>
      : 
        <div></div>
      }
    </>
  )
}

export default Home;