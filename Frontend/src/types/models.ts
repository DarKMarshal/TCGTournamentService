/** Mirrors Backend Models.Player */
export interface Player {
  id: number;
  name: string;
  championshipPoints: number;
}

/** Mirrors Backend Models.Result (Jackson serialization of getters) */
export interface Result {
  player: Player;
  placement: number;
  matchPoints: number;
  opponentWinPercentage: number;
  opponentOpponentWinPercentage: number;
  championshipPointsEarned: number;
}

/** Mirrors Backend Services.Network.DivisionDTO */
export interface Division {
  ageDivision: string;
  tournamentType: string;
  results: Result[];
}

/** Mirrors Backend Services.Network.EventSummaryDTO */
export interface EventSummary {
  id: string;
  name: string;
}

/** Mirrors Backend Services.Network.EventDetailsDTO */
export interface EventDetails {
  id: string;
  name: string;
  divisions: Division[];
}
