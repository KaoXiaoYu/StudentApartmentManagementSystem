import Cookies from 'js-cookie'

const TokenKey = 'Authorization'
const UserInfoKey = 'UserInfo'

export function getToken() {
  return Cookies.get(TokenKey)
}

export function setToken(token) {
  return Cookies.set(TokenKey, token)
}

export function removeToken() {
  return Cookies.remove(TokenKey)
}

export function getCookie(name) {
  return Cookies.get(name)
}

export function setCookie(name, value, options = {}) {
  return Cookies.set(name, value, options)
}

export function removeCookie(name) {
  return Cookies.remove(name)
}

export function getUserInfo() {
  const userInfo = Cookies.get(UserInfoKey)
  return userInfo ? JSON.parse(userInfo) : null
}

export function setUserInfo(userInfo) {
  return Cookies.set(UserInfoKey, userInfo)
}

export function removeUserInfo() {
  return Cookies.remove(UserInfoKey)
}