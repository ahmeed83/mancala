import MancalaButton from "./components/common/MancalaButton";
import {HeaderText} from "./components/HeaderText";
import {MancalaPitBig} from "./components/MancalaPitBig";
import {useEffect, useState} from "react";
import {createGame} from "./services/client";
import {Spinner} from "./components/common/spinner/Spinner";
import {PitsContainer} from "./components/PitsContainer";

export const App = () => {
    const [loading, isLoading] = useState(false);
    const [game, setGame] = useState([]);
    useEffect(() => {
        isLoading(true);
        createGame().then((res) => {
            setGame(res.data);
            isLoading(false);
        });
    }, []);

    if (loading) {
        return <Spinner/>;
    }
    
    return (
        <div className="p-10">
            <div>
                {game.pits ? (
                    <div>
                        <HeaderText player={game.player}/>
                        <div className="flex px-36">
                            <MancalaPitBig player={2} bigPit={game.pits[13]}/>
                            <PitsContainer setGame={setGame}
                                           playerId={game.player}
                                           gameId={game.gameId}
                                           pits={game.pits}/>
                            <MancalaPitBig player={1} bigPit={game.pits[6]}/>
                        </div>
                        <div className="flex justify-center">
                            <MancalaButton gameId={game.gameId}
                                           text={"Reset Game"}/>
                        </div>
                    </div>
                ) : null}
            </div>

        </div>
    );
}