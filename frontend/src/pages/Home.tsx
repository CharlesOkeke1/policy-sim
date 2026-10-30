import { useEffect, useState } from 'react'
import { Outlet, useNavigate } from 'react-router';
import '../styles/Home.css';
import lightLogo from '../assets/Policy_Sim_Logo_Light_PNG.png';
import darkLogo from '../assets/Policy_Sim_Logo_Dark_PNG.png';
import NavBarItem from '../shared/NavBarItem';
import homeIcon from '../assets/home.svg';
import gameIcon from '../assets/game.svg';
import addIcon from '../assets/add.svg';
import upIcon from '../assets/up-line.svg';
import logoutIcon from '../assets/round-logout.svg';
import trophyIcon from '../assets/trophy.svg';
import helpIcon from '../assets/help-outline.svg';
import notificationIcon from '../assets/notification.svg';
import dropdownIcon from '../assets/dropdown.svg';
import settingsIcon from '../assets/settings.svg';
import profileIcon from '../assets/user-linear.svg';
import searchIcon from '../assets/search.svg';
import minimizeIcon from '../assets/minimize.svg';
import maximizeIcon from '../assets/maximize.svg';
import { Dashboard } from '../components/Dashboard';
import { MyGames } from '../components/MyGames';
import { NewGame } from '../components/NewGame';
import { GlobalRankings } from '../components/GlobalRankings';
import { Settings } from '../components/Settings';
import { customFetch } from '../utils/FetchWrapper';
import { ThemeSelector } from '../shared/ThemeSelector';

export interface ChildProps {
  setStep: (num: number) => void;
}

type Theme = 'Light' | 'Dark' | 'System';

const Home = () => {
  const router = useNavigate();
  const [user, setUser] = useState<any>();
  const [showUserDropdown, setShowUserDropdown] = useState(false);
  const [currentTheme, setCurrentTheme] = useState<Theme>(document.documentElement.getAttribute("data-theme") as Theme || "Light");
  const [activeStep, setActiveStep] = useState(Number(sessionStorage.getItem("active_step")) ?? 0);
  const [navBarMinimized, setNavBarMinimized] = useState(false);
  const [ActiveComponent, setActiveComponent] = useState(<Dashboard setStep={setActiveStep}/>);

  const navBarMinimizeIcon = navBarMinimized ? maximizeIcon : minimizeIcon;

  useEffect(() => {
    getUser();
  }, []);

  useEffect(() => {
    // if (window.innerWidth !== 1600) setNavBarMinimized(true);
    // else setNavBarMinimized(false);
  })

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

  async function getUser() {
    const url = `http://localhost:8080/api/users/${sessionStorage.getItem("userId")}`;
    const res: any = await customFetch({method: "GET", url: url});
    setUser(res.result);
  }

  async function logoutUser() {
    const payload = {
      token: sessionStorage.getItem("authToken"),
      refreshToken: localStorage.getItem("refreshToken")
    }

    const url = `http://localhost:8080/api/users/logout`;

    fetch(url, {
      method: "POST",
      headers: {
        "Authorization": `Bearer ${sessionStorage.getItem('authToken')}`,
        "Content-Type": "application/json"
      },
      body: JSON.stringify(payload)
    })
    .then((res) => {
      return res.json();
    })
    .then((data) => {
      if (data.code === 200) {
        localStorage.setItem("authenticated", JSON.stringify(false));
        sessionStorage.clear();
        router("/login");
      }
    });    
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
          <NavBarItem minimized={navBarMinimized} onClick={() => pushActiveStep(0)} step={0} current_step={activeStep} iconPath={homeIcon} main_text='Dashboard'/>
          <NavBarItem minimized={navBarMinimized} onClick={() => pushActiveStep(1)} step={1} current_step={activeStep} iconPath={gameIcon} main_text='My Games' />
          <NavBarItem minimized={navBarMinimized} onClick={() => pushActiveStep(2)} step={2} current_step={activeStep} iconPath={addIcon} main_text='Play New Game' />
          <NavBarItem minimized={navBarMinimized} onClick={() => pushActiveStep(3)} step={3} current_step={activeStep} iconPath={trophyIcon} main_text='Global Rankings' />
          <NavBarItem minimized={navBarMinimized} onClick={() => pushActiveStep(4)} step={4} current_step={activeStep} iconPath={settingsIcon} main_text='Settings' />
        </div>
        <div style={{justifyContent: minJustify}}
          className='minimizeBtn' onClick={() => setNavBarMinimized(!navBarMinimized)}>
          <img src={navBarMinimizeIcon}/>
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
            <img src={notificationIcon} className='icon'/>
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
                  <img src={
                    showUserDropdown ? upIcon : dropdownIcon
                  } className='icon dropIcon'/>
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
                return <Dashboard setStep={setActiveStep}/>
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
                <img src={profileIcon}/>
                <p>Profile</p>
              </div>

              <div className='userdrop_profile'>
                <img src={helpIcon}/>
                <p>How to play</p>
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
            <div className='userdrop_row logout' onClick={() => logoutUser()}>
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