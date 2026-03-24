import type { EventDetails } from "../types/models";

interface Props {
  event: EventDetails;
}

export default function ResultsTable({ event }: Props) {
  return (
    <div className="results-container">
      <h2>{event.name}</h2>

      {event.divisions.length === 0 && (
        <p className="empty-state">No divisions found for this event.</p>
      )}

      {event.divisions.map((division) => (
        <div key={division.ageDivision} className="division-section">
          <h3>
            {division.ageDivision} Division
            <span className="tournament-type"> — {division.tournamentType}</span>
          </h3>

          {division.results.length === 0 ? (
            <p className="empty-state">No results for this division.</p>
          ) : (
            <table className="results-table">
              <thead>
                <tr>
                  <th>Place</th>
                  <th>Player</th>
                  <th>Match Pts</th>
                  <th>Opp Win %</th>
                  <th>Opp Opp Win %</th>
                  <th>CP Earned</th>
                </tr>
              </thead>
              <tbody>
                {division.results.map((result) => (
                  <tr key={result.player.id}>
                    <td className="cell-center">{result.placement}</td>
                    <td>{result.player.name}</td>
                    <td className="cell-center">{result.matchPoints}</td>
                    <td className="cell-center">
                      {(result.opponentWinPercentage * 100).toFixed(1)}%
                    </td>
                    <td className="cell-center">
                      {(result.opponentOpponentWinPercentage * 100).toFixed(1)}%
                    </td>
                    <td className="cell-center">{result.championshipPointsEarned}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      ))}
    </div>
  );
}
