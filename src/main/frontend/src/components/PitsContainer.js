import React from 'react';
import {MancalaPit} from "./MancalaPit";

export const PitsContainer = ({playerId, setGame, gameId, pits}) => {

    return (
        <div className="container px-5 py-24 mx-auto">
            {pits ? (
                <div className="flex flex-wrap">
                    {pits.slice(7, 13).reverse().map((pit, index) => {
                        return <div key={index} className="p-4 md:w-1/6">
                            <MancalaPit playerId={playerId} setGame={setGame} color={"yellow"} gameId={gameId} pit={pit}/>
                        </div>
                    })}
                    {pits.slice(0, 6).map((pit, index) => {
                        return <div key={index} className="p-4 md:w-1/6">
                            <MancalaPit playerId={playerId}  setGame={setGame} color={"blue"} gameId={gameId} pit={pit}/>
                        </div>
                    })}
                </div>
            ) : null}
        </div>
    )
};