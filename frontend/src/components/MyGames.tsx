import { FaPlus } from 'react-icons/fa6';
import type { ChildProps } from '../pages/Home';
import { useAuth } from '../services/AuthProvider';
import { CustomIcon } from '../shared/Icon';
import './MyGames.css';
import './NewGame.css';
import MyGameCard from '../shared/MyGameCard';
import { useState } from 'react';
import { FiChevronLeft, FiChevronRight } from 'react-icons/fi';
import PageLoader from '../shared/PageLoader';
import { useNavigate } from 'react-router';
import { useGameData, usePaginatedGames } from '../services/GameDataKeys';


export const MyGames = ({setStep, windowWidth}: ChildProps) => {
  const [statesQuery] = useGameData();
  const router = useNavigate();
  const pageClass = "current_page";

  const stateList = statesQuery.data ?? [];
  const {user} = useAuth();
  const [activePage, setActivePage] = useState(1);

  const PAGE_SIZE = 6;

  const start = (activePage - 1) * PAGE_SIZE;
  const stop = activePage * PAGE_SIZE;

  const gamesQuery = usePaginatedGames(
    user?.id ?? 0,
    start,
    stop
  );

  const gamesList = gamesQuery.data?.games ?? [];
  const gamesCount = gamesQuery.data?.gameCount ?? 0;

  const loading = gamesQuery.isPending;
  const maxPage = Math.floor(gamesCount / PAGE_SIZE) + 1;

  console.log({
    userId: user?.id,
    status: gamesQuery.status,
    fetchStatus: gamesQuery.fetchStatus,
    data: gamesQuery.data,
    error: gamesQuery.error
  });

  function incrementPage() {
    if (activePage < maxPage) {
      setActivePage(prev => prev + 1);
    }
  }

  function decrementPage() {
    if (activePage > 1) {
      setActivePage(prev => prev - 1);
    }
  }

  return (
    <>
      {
        loading ?
        <PageLoader/>
        :
        (
          gamesList ?
          <div className='my_games'>
            <div className='dashboard_row'>
              <div className='dashboard_text'>
                <p className='dashboard_main'>My games</p>
                <p style={{textWrap: 'wrap', maxWidth: '450px'}}>Every state you have governed: how it ended, and where you can pick it back up.</p>
              </div>
              <div className='dashboard_right'>
                <div className='new_game_btn' onClick={() => setStep(2)}>
                  <div className='icon_bg'>
                    <div style={{marginTop: '3px'}}>
                      <CustomIcon icon={FaPlus} fill='var(--text-primary)' strokeWidth={20} variant="circular" size={"var(--text-stat)"}/>
                    </div>
                  </div>
                  { windowWidth && windowWidth >= 768 ? "New game" : "Game"}
                </div>
              </div>
            </div>
            <div className='game_page_row'>
              <p>Showing {(activePage - 1) * PAGE_SIZE} to {stop} games</p>
              <div className='page_row'>
                <div style={{marginTop: '3px', cursor: "pointer"}} onClick={() => decrementPage()}>
                  <CustomIcon icon={FiChevronLeft} stroke='var(--text-muted)' fill='none' strokeWidth={1} variant="circular" size={"var(--text-stat)"}/>
                </div>
                <div className={pageClass}>
                  {activePage}
                </div>
                {
                  activePage === maxPage ?
                  ""
                  :
                  <div style={{marginTop: '3px', cursor: "pointer"}} onClick={() => incrementPage()}>
                    <CustomIcon icon={FiChevronRight} stroke='var(--text-muted)' fill='none' strokeWidth={1} variant="circular" size={"var(--text-stat)"}/>
                  </div>
                }    
              </div>
            </div>
            <div className='games'>
              {
                gamesList ? 
                gamesList?.map((game: any) => (
                  <MyGameCard
                    stateId={game.stateId}
                    gameId={game.id}
                    gameDifficulty={game.difficulty}
                    gameSeed={game.seed}
                    gameStartDate={game.startDate}
                    isGameCompleted={game.completed}
                    currentMonth={game.rounds?.at(-1)?.currentMonth ?? 0}
                    score={game.score}
                    rank={game.rank}
                    totalRounds={game.noOfRounds}
                    stateList={stateList ? stateList : []}
                    roundList={game.rounds}
                    click={() => router(`/game/${game.id}?mode=resume`, {
                      state: {
                        game: game,
                        difficulty: game.difficulty,
                        seed:game.seed,
                        isInit: false,  
                        noOfStates: stateList?.length,
                        noOfRounds: game.noOfRounds,
                        userLastRound: game.rounds.filter((round: any) => round.currentMonth === game.rounds.at(-1).currentMonth &&
                        game.stateId === round.stateId)[0],
                        prevLastRound: game.rounds.filter((round: any) => round.currentMonth === game.rounds.at(-1).currentMonth - 1 &&
                        game.stateId === round.stateId)[0],
                        state: stateList?.filter((state) => state.id === game.stateId) [0]                  
                      }
                    })}
                  />
                ))
                :
                <div></div>
              }
            </div>  
          </div>
          :
          <div>Nothing to see here</div>          
        )        
      }
    </> 
  )
}
