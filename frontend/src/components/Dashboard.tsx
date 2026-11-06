import React, { use, useEffect, useRef, useState } from 'react'
import plusIcon from '../assets/add.svg';
import dropdownIcon from '../assets/dropdown.svg';
import './Dashboard.css';
import { StatCard } from '../shared/StatCard';
import { MiniStatCard } from '../shared/MiniStatCard';
import { RecentGame } from '../shared/RecentGame';
import {
  ResponsiveContainer,
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  PieChart,
  BarChart, 
  Bar,
  Pie, 
  Cell
} from 'recharts';
import { PieChartLegend } from '../shared/PieChartLegend';
import { RecentActivity } from '../shared/RecentActivity';
import { customFetch } from '../utils/FetchWrapper';
import { formatDate } from '../utils/Formatting';
import { useNavigate } from 'react-router';
import type { ChildProps } from '../pages/Home';
import { FaPlus } from 'react-icons/fa6';
import { CustomIcon } from '../shared/Icon';
import { forkJoin } from 'rxjs';
import { API_DELAY, getUserData, getUserStats } from '../services/ApiService';
import PageLoader from '../shared/PageLoader';

export interface EconomyData {
    name: string;
    population: number;
    productivity: number;
    priceLevel: number;
    governmentDebt: number;
    stability: number;
}

export const Dashboard = ({setStep}: ChildProps) => {
    const [userGames, setUserGames] = useState<any>();
    const [userStats, setUserStats] = useState<any>();
    const [loading, setLoading] = useState(false);
    const router = useNavigate();
    const outcomes = [
        { name: 'Completed', value: userStats?.completed },
        { name: 'In Progress', value: userStats?.inProgress },
        { name: 'Abandoned', value: userStats?.abandoned },
    ];

    useEffect(() => {
        getEndPoints();
    }, []);

    function getEndPoints() {
        setLoading(true);
        forkJoin({
            data: getUserData(),
            stats: getUserStats()
        }).subscribe(({data, stats}) => {
            setUserGames(data);
            setUserStats(stats);
            setTimeout(() => {
                setLoading(false);
          }, API_DELAY);
        });
        
      }
    
    const bars = userStats ? Object.entries(userStats?.positions).map(([rank, count]) => ({
        rank: rank,
        count: count
    })) : [];
    const COLORS = ['var(--status-good)', 'var(--status-warn)', 'var(--status-bad)'];

    //Dummy data
    const data: EconomyData[] = [
        { name: "1", population: 89.0, productivity: 95.6, priceLevel: 78.3, governmentDebt: 82.0, stability: 65.4 },
        { name: "2", population: 75.0, productivity: 82.4, priceLevel: 62.5, governmentDebt: 45.0, stability: 78.2 },
        { name: "3", population: 57.0, productivity: 91.3, priceLevel: 69.6, governmentDebt: 56.0, stability: 69.5 },
        { name: "4", population: 81.0, productivity: 74.8, priceLevel: 59.2, governmentDebt: 29.0, stability: 81.6 },
        { name: "5", population: 50.0, productivity: 68.2, priceLevel: 55.7, governmentDebt: 31.0, stability: 72.8 },
        { name: "6", population: 48.0, productivity: 86.1, priceLevel: 65.4, governmentDebt: 27.0, stability: 84.3 },
        { name: "7", population: 58.0, productivity: 71.5, priceLevel: 57.8, governmentDebt: 34.0, stability: 63.7 },
        { name: "8", population: 30.0, productivity: 77.9, priceLevel: 60.6, governmentDebt: 22.0, stability: 88.1 },
        { name: "9", population: 32.0, productivity: 89.2, priceLevel: 67.3, governmentDebt: 49.0, stability: 70.6 },
        { name: "10", population: 37.0, productivity: 62.7, priceLevel: 53.9, governmentDebt: 18.0, stability: 75.9 },
    ];

    function toNewGame() {
        setStep(2);
    }

    return (
        <>
        {
            loading ? 
            <PageLoader/>
            :
            <div className='dashboard'>
                <div className='dashboard_row'>
                    <div className='dashboard_text'>
                        <p className='dashboard_main'>Dashboard</p>
                        <p style={{textWrap: 'wrap'}}>How your games are going, and how the economies you ran ended up.</p>
                    </div>

                    <div className='dashboard_right'>
                        <div className='calendar'>
                            <input className='date_input' type='date'/>
                            <img src={dropdownIcon} className='date_icon'/>
                        </div>
                        <div className='new_game_btn' onClick={() => toNewGame()}>
                            <div className='icon_bg'>
                                <div style={{marginTop: '3px'}}>
                                    <CustomIcon icon={FaPlus} fill='var(--text-primary)' strokeWidth={20} variant="circular" size={"var(--text-stat)"}/>
                                </div>
                            </div>
                            Start a new game
                        </div>
                    </div>
                </div>

                <div className='stats'>
                    <StatCard label='Games played' stat={userGames?.length ?? "N/A"} supporting='Up 2 vs previous 30 days'></StatCard>
                    <StatCard label='Average score' stat={userStats?.avgScore.toFixed(2)} supporting='Up 4.3 vs previous 30 days'></StatCard>
                    <StatCard label='Finished ranked #1' stat={userStats?.finishedFirst} supporting='Up 1 vs previous 30 days'></StatCard>
                    <StatCard label='Months governed' stat={userStats?.monthsGoverned} supporting={'About ' + Math.floor(userStats?.monthsGoverned/12) + ' simulated years'}></StatCard>
                </div>

                <div className='previous_games'>
                    <div className='chart_container'>
                        <div>
                            <p className='chart_header'>Final score and stability, by game</p>
                            <p className='chart_sub_header'>Your last 12 games, oldest first. Both on a 0–100 scale.</p>
                        </div>
                        <div style={{ maxWidth: '100%', height: 300}}>
                            <ResponsiveContainer style={{ maxWidth: '100%', height: 300 }}>
                                <LineChart data={data} margin={{ top: 5, right: 30, left: 20, bottom: 5 }}>
                                <CartesianGrid stroke="var(--text-primary)" strokeDasharray="3 3" />
                                <XAxis tick={{ fill: "var(--text-secondary)" }} dataKey="name"/>
                                <YAxis tick={{ fill: "var(--text-secondary)" }}/>
                                <Tooltip />
                                <Legend />
                                <Line type="monotone" name="Population" dataKey="population" stroke="#8884d8"/>
                                <Line type="monotone" name="Productivity" dataKey="productivity" stroke="#82ca9d"/>
                                <Line type="monotone" name="Price Level" dataKey="priceLevel" stroke="#ffc658"/>
                                <Line type="monotone" name="Government Debt" dataKey="governmentDebt" stroke="#ff8042"/>
                                <Line type="monotone" name="Stability" dataKey="stability" stroke="#0088fe"/>
                                </LineChart>
                            </ResponsiveContainer>
                        </div>    
                    </div>
                    <div className='recent_games'>
                        <div className='recent_games_header'>
                            <p className='recent_games_text'>Recent games</p>
                            <p className='view_all' onClick={() => setStep(1)}>View all</p>
                        </div>
                        {
                            userGames?.length ? 
                            userGames.slice(0,5)?.map((game: any) => (
                                <RecentGame
                                content={game.completed
                                    ? game.rounds[game.rounds.length-1].currentMonth === game.noOfRounds
                                        ? "Completed"
                                        : "Abandoned"
                                    : "In progress"}
                                name={game.stateName}
                                currentMonth={game.rounds?.at(-1)?.currentMonth}
                                totalRounds={game.noOfRounds}
                                startDate={formatDate(game.startDate)}
                                score={game.score ? game.score.toFixed(2) : "N/A"}
                            />
                            )) : 
                            <div className='recent_games_container'>
                                <p>No recent games to show</p>
                            </div> 
                        }                                       
                    </div>
                </div>

                <div className='previous_games'>
                    <div className='mini_stats_container'>
                        <div>
                            <p className='chart_header'>How your economies ended</p>
                            <p className='chart_sub_header'>Averages at the final month of your completed games, against the previous 30 days.</p>
                        </div>
                        <div className='mini_stats'>
                            <MiniStatCard label='Inflation (annual)' stat='TODO' supporting='Down 0.4 points'></MiniStatCard>
                            <MiniStatCard label='Unemployment' stat='TODO' supporting='Down 0.4 points'></MiniStatCard>
                            <MiniStatCard label='Debt / GDP' stat='TODO' supporting='Down 0.4 points'></MiniStatCard>
                            <MiniStatCard label='Stability' stat='TODO' supporting='Down 0.4 points'></MiniStatCard>
                            <MiniStatCard label='Real growth (annual)' stat='TODO' supporting='Down 0.4 points'></MiniStatCard>
                            <MiniStatCard label='Average final rank' stat='TODO' supporting='Down 0.4 points'></MiniStatCard>
                            <MiniStatCard label='Inflation (annual)' stat='TODO' supporting='Down 0.4 points'></MiniStatCard>
                            <MiniStatCard label='Unemployment' stat='TODO' supporting='Down 0.4 points'></MiniStatCard>
                            <MiniStatCard label='Debt / GDP' stat='TODO' supporting='Down 0.4 points'></MiniStatCard>
                            {/* <MiniStatCard label='Stability' stat='TODO' supporting='Down 0.4 points'></MiniStatCard>
                            <MiniStatCard label='Real growth (annual)' stat='TODO' supporting='Down 0.4 points'></MiniStatCard>
                            <MiniStatCard label='Average final rank' stat='TODO' supporting='Down 0.4 points'></MiniStatCard> */}
                        </div>
                    </div>
                    <div className='recent_games'>
                        <div className='recent_games_header'>
                            <p className='recent_games_text'>Recent activity</p>
                            <p className='view_all'>View all</p>
                        </div>
                        <RecentActivity main='Finished London, ranked #1' supporting='Final score 88.4' type='Completed' time='2h ago'/>
                        <RecentActivity main='Saved Manchester at month 142' supporting='Inflation running above target' type='In progress' time='2h ago'/>
                        <RecentActivity main='Abandoned Plymouth at month 61' supporting='Stability fell to 31' type='Abandoned' time='2h ago'/>
                        <RecentActivity main='Finished London, ranked #1' supporting='Final score 88.4' type='Completed' time='2h ago'/>
                        <RecentActivity main='Finished London, ranked #1' supporting='Final score 88.4' type='Completed' time='2h ago'/>
                    </div>
                </div>

                <div className='game_outcomes'>
                    <div>
                        <p className='chart_header'>Game outcomes</p>
                        <p className='chart_sub_header'>Where your {userGames?.length ?? 0} {userGames?.length === 1 ? "game" : "games"} stand, and where the {userStats?.completed ?? 0} completed ones finished.</p>
                    </div>
                    <div className='outcomes_charts'>
                        <div className='outcomes_pie_chart'>
                            {
                                userGames?.length > 0 ?
                                <div style={{ width: '40%', height: '100%', position: 'relative'}}>
                                    <ResponsiveContainer>
                                        <PieChart>
                                        <Pie
                                            data={outcomes}
                                            type='basis'
                                            dataKey="value"
                                            nameKey="name"
                                            cx="50%"
                                            cy="50%"
                                            outerRadius={100}
                                            innerRadius={50}
                                        >
                                            {outcomes.map((entry, index) => (
                                                <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} strokeWidth={3}/>
                                            ))}
                                        </Pie>
                                        <Tooltip />
                                        </PieChart>
                                    </ResponsiveContainer>
                                </div>
                                : 
                                <div className='no_pie_outcomes'>
                                    <p>No recent games to show</p>
                                </div>
                            }
                            
                            <div className='pie_chart_legend'>
                                <PieChartLegend type='Completed' value={userStats?.completed}  total={userGames?.length}/>
                                <PieChartLegend type='Abandoned' value={userStats?.abandoned}  total={userGames?.length}/>
                                <PieChartLegend type='In progress' value={userStats?.inProgress}  total={userGames?.length}/>
                            </div>
                        </div>
                        <div className='outcomes_bar_chart'>
                            {
                                bars.length > 0 ?
                                <div style={{ width: '100%', height: '100%', position: 'relative'}}>
                                    <ResponsiveContainer>
                                        <BarChart data={bars} margin={{ top: 20, right: 30, left: 20, bottom: 5 }}>
                                            <CartesianGrid stroke="var(--text-primary)" strokeDasharray="3 3" />
                                            <XAxis dataKey="rank" tick={{ fill: "var(--text-secondary)" }}/>
                                            <YAxis tick={{ fill: "var(--text-secondary)" }}/>
                                            <Tooltip />
                                            <Legend/>
                                            <Bar dataKey="count" name='Positions' fill="var(--chart-1)" radius={[4, 4, 0, 0]} />
                                        </BarChart>
                                    </ResponsiveContainer>
                                </div>
                                : 
                                <div className='recent_games_container'>
                                    <p>No recent games to show</p>
                                </div>
                            }
                            
                        </div>
                    </div>                
                </div>
            </div>
            }
        </>
    )
}