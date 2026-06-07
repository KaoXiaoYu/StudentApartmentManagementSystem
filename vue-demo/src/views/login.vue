<template>
  <div class="cyber-container">
    <!-- 二次元半透明背景 -->
    <div class="bg-image" :style="{ backgroundImage: `url(${bgUrl})` }"></div>
    
    <!-- 赛博朋克网格 + 扫描线特效 -->
    <div class="cyber-grid"></div>
    <div class="cyber-scanlines"></div>
    
    <!-- 毛玻璃登录卡片 -->
    <div class="cyber-card">
      <div class="card-glow"></div>
      
      <div class="card-header">
        <div class="logo-container">
          <div class="logo-icon">
            <el-icon :size="32"><Grid /></el-icon>
          </div>
          <div class="logo-text">
            <span class="logo-title">✨ 权限管理系统 ✨</span>
            <span class="logo-subtitle">Anime Cyber System</span>
          </div>
        </div>
        <div class="status-indicator">
          <span class="status-dot"></span>
          <span>ONLINE</span>
        </div>
      </div>
      
      <div class="form-section">
        <div class="form-glitch">
          <h2 class="form-title">
            <span class="glitch-text">SYSTEM LOGIN</span>
          </h2>
          <p class="form-subtitle">ENTER YOUR CREDENTIALS</p>
        </div>
        
        <el-form
          ref="loginFormRef"
          :model="loginForm"
          label-position="top"
          class="cyber-form"
        >
          <div class="input-group">
            <div class="input-glow"></div>
            <el-form-item prop="username" class="no-label">
              <el-input
                v-model="loginForm.username"
                placeholder="USERNAME / 账号"
                :prefix-icon="User"
                size="large"
                class="cyber-input"
              />
            </el-form-item>
          </div>
          
          <div class="input-group">
            <div class="input-glow"></div>
            <el-form-item prop="password" class="no-label">
              <el-input
                v-model="loginForm.password"
                placeholder="PASSWORD / 密码"
                type="password"
                show-password
                :prefix-icon="Lock"
                size="large"
                class="cyber-input"
              />
            </el-form-item>
          </div>
          
          <el-button
            type="primary"
            size="large"
            class="cyber-btn"
            :loading="loading"
            @click="handleLogin"
          >
            <span class="btn-text">登 录</span>
            <el-icon :size="18"><ArrowRight /></el-icon>
          </el-button>
        </el-form>
      </div>
      
      <div class="card-footer">
        <div class="footer-line"></div>
        <span class="footer-text">SECURE CONNECTION ESTABLISHED</span>
        <div class="footer-line"></div>
      </div>
      
      <div class="register-link">
        <span class="link-text">还没有账号？</span>
        <el-link type="primary" :underline="false" class="register-btn" @click="$router.push('/register')">
          <span>立即注册</span>
          <el-icon :size="16"><ArrowRight /></el-icon>
        </el-link>
      </div>
    </div>
    
    <!-- 角落装饰 -->
    <div class="corner-decoration top-left">
      <div class="corner-line"></div>
      <div class="corner-line"></div>
    </div>
    <div class="corner-decoration top-right">
      <div class="corner-line"></div>
      <div class="corner-line"></div>
    </div>
    <div class="corner-decoration bottom-left">
      <div class="corner-line"></div>
      <div class="corner-line"></div>
    </div>
    <div class="corner-decoration bottom-right">
      <div class="corner-line"></div>
      <div class="corner-line"></div>
    </div>
    
    <!-- 浮动光效 -->
    <div class="floating-elements">
      <div class="float-element float-1"></div>
      <div class="float-element float-2"></div>
      <div class="float-element float-3"></div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { User, Lock, Grid, ArrowRight } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { setToken, setUserInfo } from '@/utils/cookie'

const router = useRouter()

// ============== 二次元背景图（可修改） ==============
const bgUrl = ref('/src/assets/bg/ZDPGdIkVQXqaRmEh2B0Cain4N5M.cnt.jpg')
// =====================================================

const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = ref({
  username: '',
  password: ''
})

// 登录逻辑
const handleLogin = async () => {
  if (!loginForm.value.username || !loginForm.value.password) {
    ElMessage.warning('请输入账号和密码')
    return
  }

  loading.value = true
  try {
    // 模拟登录成功
    // const res = await request({
    //   url: '/login',
    //   method: 'post',
    //   data: {
    //     username: loginForm.value.username,
    //     password: loginForm.value.password
    //   }
    // })
    
    // 模拟成功响应
    const res = { code: 200, data: { username: loginForm.value.username, name: '管理员', role: 'admin' } }
    
    if (res.code === 200) {
      ElMessage.success('登录成功！')
      // 保存token到localStorage
      localStorage.setItem('token', 'mock-token-' + Date.now())
      // 保存用户信息
      if (res.data) {
        localStorage.setItem('userInfo', JSON.stringify(res.data))
        setUserInfo(JSON.stringify(res.data))
      }
      // 跳转到首页
      router.push('/dashboard')
    } else {
      ElMessage.error(res.message || '登录失败')
    }
  } catch (err) {
    ElMessage.error('登录失败：' + (err.message || '网络异常'))
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.cyber-container {
  width: 100%;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  padding: 20px;
  box-sizing: border-box;
}

/* 二次元半透明背景 */
.bg-image {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-size: cover;
  background-position: center center;
  background-repeat: no-repeat;
  opacity: 0.8;
  z-index: 0;
}

/* 赛博网格 */
.cyber-grid {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-image: 
    linear-gradient(rgba(0, 255, 255, 0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba(0, 255, 255, 0.03) 1px, transparent 1px);
  background-size: 50px 50px;
  z-index: 1;
}

/* 扫描线 */
.cyber-scanlines {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: repeating-linear-gradient(
    0deg,
    transparent,
    transparent 2px,
    rgba(0, 0, 0, 0.1) 2px,
    rgba(0, 0, 0, 0.1) 4px
  );
  pointer-events: none;
  z-index: 2;
}

/* 主卡片 - 毛玻璃 + 二次元风格 */
.cyber-card {
  position: relative;
  z-index: 10;
  width: 420px;
  padding: 45px;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(16px);
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  overflow: hidden;
}

.card-glow {
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(ellipse at center, rgba(255, 180, 220, 0.15) 0%, transparent 70%);
  animation: pulse 4s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 0.5; transform: scale(1); }
  50% { opacity: 1; transform: scale(1.1); }
}

/* 头部 */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
  padding-bottom: 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.4);
}

.logo-container {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-icon {
  width: 50px;
  height: 50px;
  background: linear-gradient(135deg, #ff95d1, #a8bfff);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  box-shadow: 0 0 20px rgba(255, 150, 200, 0.5);
}

.logo-text {
  display: flex;
  flex-direction: column;
}

.logo-title {
  font-size: 18px;
  font-weight: 700;
  color: #fff;
  text-shadow: 0 0 8px rgba(255, 180, 220, 0.6);
}

.logo-subtitle {
  font-size: 10px;
  color: rgba(255, 255, 255, 0.7);
}

.status-indicator {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 11px;
  color: #a8ffb8;
}

.status-dot {
  width: 8px;
  height: 8px;
  background: #a8ffb8;
  border-radius: 50%;
  animation: blink 2s infinite;
}

@keyframes blink {
  0%,100%{opacity:1}50%{opacity:.3}
}

/* 标题 */
.form-glitch {
  text-align: center;
  margin-bottom: 32px;
}

.form-title {
  font-size: 26px;
  color: #fff;
  text-shadow: 0 0 10px rgba(255, 180, 220, 0.6);
}

.form-subtitle {
  font-size: 12px;
  color: rgba(255,255,255,.6);
  margin-top: 8px;
}

/* 输入框 */
.input-group {
  position: relative;
  margin-bottom: 20px;
}

.input-glow {
  position: absolute;
  top: 50%; left: 50%;
  transform: translate(-50%,-50%);
  width: calc(100% + 8px);
  height: calc(100% + 8px);
  background: linear-gradient(135deg, rgba(255,150,200,0.3), rgba(160,180,255,0.3));
  border-radius: 12px;
  opacity: 0;
  transition: .3s;
}

.input-group:focus-within .input-glow {
  opacity: 1;
}

.cyber-input {
  width: 100%;
}

:deep(.el-input__wrapper) {
  background: rgba(255,255,255,.5) !important;
  border-radius: 8px;
  border: none;
}

/* 按钮 */
.cyber-btn {
  width: 100%;
  padding: 12px;
  background: linear-gradient(45deg, #ff95d1, #a8bfff);
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: bold;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: .3s;
}

.cyber-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(255,150,200,.3);
}

/* 底部 */
.card-footer {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  margin-top: 32px;
  padding-top: 16px;
  border-top: 1px solid rgba(255,255,255,.3);
}

.footer-line {
  flex: 1;
  height: 1px;
  background: rgba(255,255,255,.3);
}

.footer-text {
  font-size: 10px;
  color: rgba(255,255,255,.6);
}

/* 注册链接 */
.register-link {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid rgba(255, 255, 255, 0.2);
}

.link-text {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.7);
}

.register-btn {
  font-size: 13px;
  color: #ff95d1;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 4px;
  transition: .3s;
}

.register-btn:hover {
  color: #ffb8d9;
  transform: translateX(4px);
}

/* 装饰 */
.corner-decoration {
  position: absolute;
  width: 40px;
  height: 40px;
  z-index: 5;
}
.corner-line {
  position: absolute;
  width: 20px;
  height: 1px;
  background: rgba(255,255,255,.4);
}

.top-left {top:16px;left:16px}
.top-right {top:16px;right:16px}
.bottom-left {bottom:16px;left:16px}
.bottom-right {bottom:16px;right:16px}

.floating-elements {
  position: absolute;
  top:0;left:0;
  width:100%;height:100%;
  z-index: 3;
  pointer-events: none;
}

.float-element {
  position: absolute;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(255,180,220,0.2) 0%, transparent 70%);
  animation: float 8s infinite ease-in-out;
}

.float-1{width:100px;height:100px;top:20%;right:10%}
.float-2{width:60px;height:60px;bottom:30%;left:15%;animation-delay:-3s}
.float-3{width:80px;height:80px;top:60%;right:25%;animation-delay:-5s}

@keyframes float {
  0%,100%{transform:translate(0);opacity:.3}
  50%{transform:translateY(-20px);opacity:.6}
}
</style>