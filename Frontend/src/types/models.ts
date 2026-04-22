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
  winners: Record<string, string>;
}

/** Mirrors Backend Services.Network.EventDetailsDTO */
export interface EventDetails {
  id: string;
  name: string;
  divisions: Division[];
}

/** Mirrors Backend Services.DTO.PlayerDTO */
export interface PlayerDTO {
  id: number;
  name: string;
  championshipPoints: number;
}

/** Mirrors Backend Services.DTO.LeaderboardDTO */
export interface LeaderboardDTO {
  ageDivision: string;
  players: PlayerDTO[];
}

/** Mirrors Backend Services.DTO.AccountResponse */
export interface AccountResponse {
  id: number;
  username: string;
  playerId: number;
  dateOfBirth: string;
  role: "PLAYER" | "ORGANIZER" | "ADMIN";
  token?: string;
}

/** Mirrors Backend Services.DTO.SignupRequest */
export interface SignupRequest {
  username: string;
  playerId: number;
  dateOfBirth: string;
  password: string;
}

/** Mirrors Backend Services.DTO.LoginRequest */
export interface LoginRequest {
  username: string;
  password: string;
}

/** Mirrors Backend Services.DTO.PersonalPlayerDTO */
export interface PersonalPlayerDTO {
  id: number;
  name: string;
  championshipPoints: number;
}

/** Mirrors Backend Services.DTO.PersonalResultDTO */
export interface PersonalResultDTO {
  eventId: string;
  eventName: string;
  ageDivision: string;
  placement: number;
  pointsEarned: number;
}

/** Mirrors Backend Services.DTO.PersonalEventDTO */
export interface PersonalEventDTO {
  id: string;
  name: string;
}

/** Mirrors Backend Services.DTO.PersonalDataDTO */
export interface PersonalDataDTO {
  player: PersonalPlayerDTO | null;
  results: PersonalResultDTO[];
  uploadedEvents: PersonalEventDTO[];
}
