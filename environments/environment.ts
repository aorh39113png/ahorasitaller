// src/environments/environment.ts
export interface Environment {
  production: boolean;
  apiURL: string;
}

export const environment: Environment = {
  production: false,
  apiURL: 'http://localhost:4040'
};
