import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getToken, setToken, removeToken } from '@/utils/cookie'

export const useUserStore = defineStore('user', () => {
  const token = ref(getToken() || '')
  const userInfo = ref({
    id: '',
    username: '',
    nickname: '',
    avatar: '',
    roles: []
  })

  function login(data) {
    token.value = data.token
    setToken(data.token)
    if (data.userInfo) {
      userInfo.value = data.userInfo
    }
  }

  function logout() {
    token.value = ''
    userInfo.value = {
      id: '',
      username: '',
      nickname: '',
      avatar: '',
      roles: []
    }
    removeToken()
  }

  function setUserInfo(info) {
    userInfo.value = { ...userInfo.value, ...info }
  }

  return {
    token,
    userInfo,
    login,
    logout,
    setUserInfo
  }
}, {
  persist: true
})