export const TOKEN_KEY = "@formoti-Token";
export const isAuthenticated = () => localStorage.getItem(TOKEN_KEY) !== null;
export const getToken = () => localStorage.getItem(TOKEN_KEY);
export const login = token => {
  localStorage.setItem(TOKEN_KEY, token);
};
export const logout = () => {
  localStorage.removeItem("tempResearch1")
  localStorage.removeItem("tempResearch2")
  localStorage.removeItem("tempResearch3")
  localStorage.removeItem(TOKEN_KEY);
};