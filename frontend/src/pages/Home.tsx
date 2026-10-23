import { useEffect, useState } from 'react'
import { Outlet, useNavigate } from 'react-router';
import '../styles/Home.css';
import lightLogo from '../assets/Policy_Sim_Logo_Light_PNG.png';
import NavBarItem from '../shared/NavBarItem';
import homeIcon from '../assets/home.svg';
import gameIcon from '../assets/game.svg';
import addIcon from '../assets/add.svg';
import logoutIcon from '../assets/round-logout.svg';
import trophyIcon from '../assets/trophy.svg';
import helpIcon from '../assets/help-outline.svg';
import notificationIcon from '../assets/notification.svg';
import dropdownIcon from '../assets/dropdown.svg';
import settingsIcon from '../assets/settings.svg';
import profileIcon from '../assets/user-linear.svg';
import searchIcon from '../assets/search.svg';
import { Dashboard } from '../components/Dashboard';
import { MyGames } from '../components/MyGames';
import { NewGame } from '../components/NewGame';
import { GlobalRankings } from '../components/GlobalRankings';
import { Settings } from '../components/Settings';
import { customFetch } from '../utils/FetchWrapper';
import { ThemeSelector } from '../shared/ThemeSelector';

const Home = () => {
  const router = useNavigate();
  const [user, setUser] = useState<any>();
  const [showUserDropdown, setShowUserDropdown] = useState(false);
  const [currentTheme, setCurrentTheme] = useState<'Light' | 'Dark' | 'System'>('System');
  const [activeStep, setActiveStep] = useState(Number(sessionStorage.getItem("active_step")) ?? 0);
  //const [ActiveComponent, setActiveComponent] = useState(() => Dashboard);

  useEffect(() => {
    getUser();
  }, []);

  function closeUserDrop() {
    if (showUserDropdown) {
      setShowUserDropdown(false);
    }
  }

  function setTheme(theme: typeof currentTheme) {
    /**
     * System themes currently don't work for some reason that i dont know.
     */
    if (theme === "System") {
      const isDarkMode = window.matchMedia('(prefers-color-scheme: dark)').matches;
      const systemTheme = isDarkMode ? 'Dark' : 'Light';
      setCurrentTheme(systemTheme);
      document.documentElement.setAttribute("data-theme", systemTheme.toLowerCase());
    }
    setCurrentTheme(theme);
    document.documentElement.setAttribute("data-theme", theme.toLowerCase());
  }

  async function getUser() {
    const url = `http://localhost:8080/api/users/${sessionStorage.getItem("userId")}`;
    const res: any = await customFetch({method: "GET", url: url});
    setUser(res.result);
  }

  async function logoutUser() {
    const payload = {
      token: sessionStorage.getItem("authToken"),
      refreshToken: sessionStorage.getItem("refreshToken")
    }

    const url = `http://localhost:8080/api/users/logout`;
    const res: any = await customFetch({method: "POST", url: url, body: payload});
    console.log(res.result);
    sessionStorage.clear();
    router("/login");
  }

  function pushActiveStep(step: number) {
    setActiveStep(step);
    sessionStorage.setItem("active_step", JSON.stringify(step))
  }
  
  return (
    <div className='home_container' onDoubleClick={() => closeUserDrop()}>
      <div className='left_nav_bar'>
        <div className='logoBlock'>
          <img src={lightLogo} className='logoImg'/>
          <div>
            <p className='main'>POLICYSIM</p>
            <p className='supporting'>Build. Govern. Prosper.</p>
          </div>
        </div>
        <NavBarItem onClick={() => pushActiveStep(0)} step={0} current_step={activeStep} iconPath={homeIcon} main_text='Dashboard'/>
        <NavBarItem onClick={() => pushActiveStep(1)} step={1} current_step={activeStep} iconPath={gameIcon} main_text='My Games' />
        <NavBarItem onClick={() => pushActiveStep(2)} step={2} current_step={activeStep} iconPath={addIcon} main_text='Play New Game' />
        <NavBarItem onClick={() => pushActiveStep(3)} step={3} current_step={activeStep} iconPath={trophyIcon} main_text='Global Rankings' />
        <NavBarItem onClick={() => pushActiveStep(4)} step={4} current_step={activeStep} iconPath={settingsIcon} main_text='Settings' />
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
                  <img src={dropdownIcon} className='icon dropIcon'/>
                </div>
              </div>
            </div>
          </div>
        </div>

        {
          showUserDropdown ? 
            <div>
              <div className='userdrop_container'>
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