export const setToken = (token) => {
  localStorage.setItem("token", token)
  if (typeof chrome !== "undefined" && chrome.storage && chrome.storage.local) {
    chrome.storage.local.set({ token: token })
  }
}

export const getToken = () => {
  return localStorage.getItem("token")
}

export const logout = () => {
  localStorage.removeItem("token")
  if (typeof chrome !== "undefined" && chrome.storage && chrome.storage.local) {
    chrome.storage.local.remove("token")
  }
}
