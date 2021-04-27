export const MancalaPitBig = ({bigPit, player}) => {
    return (
        <div className="text-4xl title-font font-bold">
            {player === 1 ? (
                <PlayerBigPitView color={"blue"} name={"Player 1"} stones={bigPit.stones}/>
            ) : (
                <PlayerBigPitView color={"yellow"} name={"Player 2"} stones={bigPit.stones}/>
            )}
        </div>
    );
}

const PlayerBigPitView = ({color, name, stones}) => {
    return (
        <div className={`${color === "blue" ? "text-blue-600" : "text-yellow-400"}`}>
            <p className="p-2 truncate">{name}</p>
            <div className={`${color === "blue" ? "border-blue-500" : "border-yellow-300"}
                            border-4 rounded-lg text-center relative`}>
                <h2 className="py-44 text-blue-700">
                    ({stones})
                </h2>
            </div>
        </div>
    )
}