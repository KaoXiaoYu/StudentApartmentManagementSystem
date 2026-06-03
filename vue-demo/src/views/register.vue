<template>
  <div class="auth-container">
    <div class="auth-card">
      <!-- 左侧品牌展示区 -->
      <div class="auth-left">
        <div class="auth-left-bg"></div>
        <div class="auth-left-content">
          <div class="brand-header">
            <div class="brand-logo">
              <el-icon :size="24"><Grid /></el-icon>
            </div>
            <span class="brand-name">Enterprise Pro</span>
          </div>
          <h1 class="brand-title">
            助力企业数字化<br />转型新高度
          </h1>
          <ul class="brand-features">
            <li>
              <el-icon><CircleCheck /></el-icon>
              <span>全链路业务流程自动化管理</span>
            </li>
            <li>
              <el-icon><CircleCheck /></el-icon>
              <span>金融级数据加密与安全保障</span>
            </li>
            <li>
              <el-icon><CircleCheck /></el-icon>
              <span>实时数据分析与智能报表决策</span>
            </li>
          </ul>
        </div>
        <div class="auth-left-footer">
          <img
            class="illustration"
            src="https://modao.cc/agent-py/media/generated_images/2026-05-25/4f43a216beb74665b0320b0ea00f8668.jpg"
            alt="Business illustration"
          />
          <p class="copyright">&copy; 2026 Enterprise Pro SaaS Group. All rights reserved.</p>
        </div>
      </div>

      <!-- 右侧注册表单 -->
      <div class="auth-right">
        <div class="auth-form-wrapper">
          <div class="form-header">
            <el-link class="back-link" type="info" :underline="false" @click="$router.push('/login')">
              <el-icon><ArrowLeft /></el-icon> 返回登录
            </el-link>
            <h2 class="form-title">创建企业账号</h2>
            <p class="form-subtitle">加入 Enterprise Pro，开启高效管理之旅</p>
          </div>

          <el-form
            ref="registerFormRef"
            :model="registerForm"
            :rules="registerRules"
            label-position="top"
            @submit.prevent="handleRegister"
          >
            <el-form-item label="账号" prop="username">
              <el-input
                v-model="registerForm.username"
                placeholder="请输入账号"
                :prefix-icon="User"
                size="large"
              />
            </el-form-item>

            <el-form-item label="密码" prop="password">
              <el-input
                v-model="registerForm.password"
                placeholder="请输入密码"
                type="password"
                show-password
                :prefix-icon="Lock"
                size="large"
              />
            </el-form-item>

            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input
                v-model="registerForm.confirmPassword"
                placeholder="请再次输入密码"
                type="password"
                show-password
                :prefix-icon="Lock"
                size="large"
              />
            </el-form-item>

            <el-button
              type="primary"
              size="large"
              class="submit-btn"
              :loading="registering"
              @click="handleRegister"
            >
              完成注册
            </el-button>
          </el-form>

          <p class="switch-tip">
            已有账号？<el-link type="primary" :underline="false" @click="$router.push('/login')">立即登录</el-link>
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Grid, CircleCheck, ArrowLeft, User, Lock } from '@element-plus/icons-vue'

const router = useRouter()

const registerFormRef = ref(null)
const registering = ref(false)

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
})

const validateConfirmPass = (rule, value, callback) => {
  if (value !== registerForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const registerRules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少需要6位', trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validateConfirmPass, trigger: 'blur' },
  ],
}

const handleRegister = async () => {
  const valid = await registerFormRef.value.validate().catch(() => false)
  if (!valid) return

  registering.value = true
  setTimeout(() => {
    registering.value = false
    ElMessage.success('注册成功！请登录您的账号')
    setTimeout(() => router.push('/login'), 1500)
  }, 1500)
}
</script>

<style lang="scss" scoped>
.auth-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 16px;
  background-color: #f1f5f9;
}

.auth-card {
  width: 100%;
  max-width: 1000px;
  min-height: 640px;
  display: flex;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  background: #fff;
}

// ===== 左侧品牌区 =====
.auth-left {
  display: none;
  width: 50%;
  background: linear-gradient(135deg, #2563eb, #1d4ed8);
  padding: 48px;
  flex-direction: column;
  justify-content: space-between;
  position: relative;
  overflow: hidden;

  @media (min-width: 768px) {
    display: flex;
  }
}

.auth-left-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  opacity: 0.08;
  background:
    radial-gradient(circle at 100% 0%, white 0%, transparent 50%),
    radial-gradient(circle at 0% 100%, white 0%, transparent 50%);
  pointer-events: none;
}

.auth-left-content {
  position: relative;
  z-index: 1;
}

.brand-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 32px;
}

.brand-logo {
  width: 40px;
  height: 40px;
  background: #fff;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #2563eb;
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1);
}

.brand-name {
  color: #fff;
  font-size: 24px;
  font-weight: 700;
  letter-spacing: -0.5px;
}

.brand-title {
  color: #fff;
  font-size: 36px;
  font-weight: 800;
  line-height: 1.3;
  margin-bottom: 24px;
}

.brand-features {
  list-style: none;
  padding: 0;
  margin: 0;

  li {
    display: flex;
    align-items: center;
    gap: 12px;
    color: #bfdbfe;
    padding: 8px 0;

    .el-icon {
      color: #93c5fd;
      font-size: 18px;
    }
  }
}

.auth-left-footer {
  position: relative;
  z-index: 1;
}

.illustration {
  width: 100%;
  height: 120px;
  object-fit: contain;
  margin-bottom: 16px;
}

.copyright {
  color: #93c5fd;
  font-size: 12px;
}

// ===== 右侧表单 =====
.auth-right {
  width: 100%;
  padding: 48px;
  display: flex;
  flex-direction: column;
  justify-content: center;

  @media (min-width: 768px) {
    width: 50%;
  }
}

.auth-form-wrapper {
  width: 100%;
  max-width: 360px;
  margin: 0 auto;
}

.form-header {
  margin-bottom: 24px;
}

.back-link {
  margin-bottom: 12px;
  font-size: 13px;
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.form-title {
  font-size: 24px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 4px;
}

.form-subtitle {
  color: #64748b;
  font-size: 13px;
}

.submit-btn {
  width: 100%;
  padding: 12px 0;
  font-size: 16px;
  font-weight: 600;
  border-radius: 8px;
  box-shadow: 0 10px 15px -3px rgba(37, 99, 235, 0.2);
}

.switch-tip {
  margin-top: 24px;
  text-align: center;
  font-size: 14px;
  color: #64748b;
}
</style>

