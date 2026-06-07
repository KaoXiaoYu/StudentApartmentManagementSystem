<template>
  <div class="permission-page">
    <!-- 角色管理 -->
    <div class="section-card">
      <div class="section-header">
        <h3 class="section-title">角色管理</h3>
        <el-button type="primary" size="small" @click="showRoleModal = true">
          <el-icon><Plus /></el-icon>
          添加角色
        </el-button>
      </div>
      
      <el-table :data="roleList" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="角色名称" width="150" />
        <el-table-column prop="code" label="角色标识" width="150" />
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="menuCount" label="权限数量" width="100" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="scope">
            <el-switch 
              :value="scope.row.status === '启用'" 
              @change="toggleRoleStatus(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button size="small" @click="handleRoleEdit(scope.row)">编辑</el-button>
            <el-button size="small" @click="handleRolePermission(scope.row)">权限配置</el-button>
            <el-button size="small" type="danger" @click="handleRoleDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    
    <!-- 用户管理 -->
    <div class="section-card">
      <div class="section-header">
        <h3 class="section-title">用户管理</h3>
        <el-button type="primary" size="small" @click="showUserModal = true">
          <el-icon><Plus /></el-icon>
          添加用户
        </el-button>
      </div>
      
      <el-table :data="userList" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="真实姓名" width="120" />
        <el-table-column prop="roles" label="角色" />
        <el-table-column prop="phone" label="联系电话" width="130" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="scope">
            <el-switch 
              :value="scope.row.status === '启用'" 
              @change="toggleUserStatus(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button size="small" @click="handleUserEdit(scope.row)">编辑</el-button>
            <el-button size="small" @click="handleUserRole(scope.row)">分配角色</el-button>
            <el-button size="small" type="danger" @click="handleUserDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    
    <!-- 角色弹窗 -->
    <el-dialog :title="isRoleEdit ? '编辑角色' : '添加角色'" :visible.sync="showRoleModal" width="450px">
      <el-form :model="roleForm" :rules="roleRules" ref="roleFormRef" label-width="100px">
        <el-form-item label="角色名称" prop="name">
          <el-input v-model="roleForm.name" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色标识" prop="code">
          <el-input v-model="roleForm.code" placeholder="请输入角色标识" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-textarea v-model="roleForm.description" placeholder="请输入角色描述" :rows="3" />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="showRoleModal = false">取消</el-button>
        <el-button type="primary" @click="handleRoleSubmit">确定</el-button>
      </template>
    </el-dialog>
    
    <!-- 用户弹窗 -->
    <el-dialog :title="isUserEdit ? '编辑用户' : '添加用户'" :visible.sync="showUserModal" width="450px">
      <el-form :model="userForm" :rules="userRules" ref="userFormRef" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="userForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="userForm.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="密码" :prop="isUserEdit ? '' : 'password'">
          <el-input v-model="userForm.password" type="password" placeholder="请输入密码" :disabled="isUserEdit" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="userForm.phone" placeholder="请输入联系电话" />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="showUserModal = false">取消</el-button>
        <el-button type="primary" @click="handleUserSubmit">确定</el-button>
      </template>
    </el-dialog>
    
    <!-- 权限配置弹窗 -->
    <el-dialog title="权限配置" :visible.sync="showPermissionModal" width="500px">
      <div class="permission-tree">
        <el-tree
          :data="menuTree"
          :props="treeProps"
          show-checkbox
          default-expand-all
          :default-checked-keys="checkedMenuIds"
          ref="treeRef"
        />
      </div>
      
      <template #footer>
        <el-button @click="showPermissionModal = false">取消</el-button>
        <el-button type="primary" @click="handlePermissionSave">保存配置</el-button>
      </template>
    </el-dialog>
    
    <!-- 角色分配弹窗 -->
    <el-dialog title="分配角色" :visible.sync="showAssignModal" width="400px">
      <el-form :model="assignForm" label-width="80px">
        <el-form-item label="选择角色">
          <el-checkbox-group v-model="assignForm.roleIds">
            <el-checkbox 
              v-for="role in roleList" 
              :key="role.id" 
              :label="role.id"
            >
              {{ role.name }}
            </el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="showAssignModal = false">取消</el-button>
        <el-button type="primary" @click="handleAssignSave">保存分配</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

// 角色数据
const roleList = ref([
  { id: 1, name: '超级管理员', code: 'admin', description: '拥有系统所有权限', menuCount: 8, status: '启用' },
  { id: 2, name: '管理员', code: 'manager', description: '拥有管理权限', menuCount: 6, status: '启用' },
  { id: 3, name: '宿管员', code: 'warden', description: '寝室管理相关权限', menuCount: 4, status: '启用' },
  { id: 4, name: '学生', code: 'student', description: '学生查看权限', menuCount: 2, status: '启用' }
])

// 用户数据
const userList = ref([
  { id: 1, username: 'admin', realName: '系统管理员', roles: '超级管理员', phone: '13800138000', status: '启用' },
  { id: 2, username: 'manager', realName: '张经理', roles: '管理员', phone: '13800138001', status: '启用' },
  { id: 3, username: 'warden1', realName: '李阿姨', roles: '宿管员', phone: '13800138002', status: '启用' },
  { id: 4, username: 'student1', realName: '张三', roles: '学生', phone: '13800138003', status: '启用' }
])

// 菜单树
const menuTree = ref([
  { id: 1, label: '首页', children: [] },
  { id: 2, label: '学生管理', children: [] },
  { id: 3, label: '寝室信息', children: [] },
  { id: 4, label: '寝室安排', children: [] },
  { id: 5, label: '卫生检查', children: [] },
  { id: 6, label: '文明寝室', children: [] },
  { id: 7, label: '缴费管理', children: [] },
  { id: 8, label: '权限管理', children: [] }
])

const treeProps = {
  children: 'children',
  label: 'label'
}

// 弹窗状态
const showRoleModal = ref(false)
const showUserModal = ref(false)
const showPermissionModal = ref(false)
const showAssignModal = ref(false)
const isRoleEdit = ref(false)
const isUserEdit = ref(false)

// 表单数据
const roleForm = reactive({ id: '', name: '', code: '', description: '' })
const userForm = reactive({ id: '', username: '', realName: '', password: '', phone: '' })
const assignForm = reactive({ userId: '', roleIds: [] })
const checkedMenuIds = ref([])

// 表单引用
const roleFormRef = ref(null)
const userFormRef = ref(null)
const treeRef = ref(null)

// 表单验证规则
const roleRules = {
  name: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入角色标识', trigger: 'blur' }]
}

const userRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

// 角色操作
const handleRoleEdit = (row) => {
  isRoleEdit.value = true
  Object.assign(roleForm, row)
  showRoleModal.value = true
}

const handleRoleDelete = (id) => {
  ElMessage.confirm('确定要删除该角色吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  }).then(() => {
    roleList.value = roleList.value.filter(item => item.id !== id)
    ElMessage.success('删除成功')
  })
}

const toggleRoleStatus = (row) => {
  row.status = row.status === '启用' ? '禁用' : '启用'
  ElMessage.success(`角色已${row.status}`)
}

const handleRoleSubmit = async () => {
  const valid = await roleFormRef.value.validate().catch(() => false)
  if (!valid) return
  
  if (isRoleEdit.value) {
    const index = roleList.value.findIndex(item => item.id === roleForm.id)
    if (index !== -1) {
      roleList.value[index] = { ...roleForm, menuCount: 0, status: '启用' }
    }
    ElMessage.success('编辑成功')
  } else {
    const newId = roleList.value.length > 0 ? Math.max(...roleList.value.map(i => i.id)) + 1 : 1
    roleList.value.push({ ...roleForm, id: newId, menuCount: 0, status: '启用' })
    ElMessage.success('添加成功')
  }
  
  showRoleModal.value = false
  resetRoleForm()
}

const resetRoleForm = () => {
  isRoleEdit.value = false
  Object.assign(roleForm, { id: '', name: '', code: '', description: '' })
}

// 用户操作
const handleUserEdit = (row) => {
  isUserEdit.value = true
  Object.assign(userForm, { id: row.id, username: row.username, realName: row.realName, phone: row.phone })
  showUserModal.value = true
}

const handleUserDelete = (id) => {
  ElMessage.confirm('确定要删除该用户吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  }).then(() => {
    userList.value = userList.value.filter(item => item.id !== id)
    ElMessage.success('删除成功')
  })
}

const toggleUserStatus = (row) => {
  row.status = row.status === '启用' ? '禁用' : '启用'
  ElMessage.success(`用户已${row.status}`)
}

const handleUserSubmit = async () => {
  const valid = await userFormRef.value.validate().catch(() => false)
  if (!valid) return
  
  if (isUserEdit.value) {
    const index = userList.value.findIndex(item => item.id === userForm.id)
    if (index !== -1) {
      userList.value[index] = { ...userList.value[index], ...userForm, roles: userList.value[index].roles }
    }
    ElMessage.success('编辑成功')
  } else {
    const newId = userList.value.length > 0 ? Math.max(...userList.value.map(i => i.id)) + 1 : 1
    userList.value.push({ ...userForm, id: newId, roles: '学生', status: '启用' })
    ElMessage.success('添加成功')
  }
  
  showUserModal.value = false
  resetUserForm()
}

const resetUserForm = () => {
  isUserEdit.value = false
  Object.assign(userForm, { id: '', username: '', realName: '', password: '', phone: '' })
}

// 权限配置
const handleRolePermission = (row) => {
  checkedMenuIds.value = [1, 2, 3, 4]
  showPermissionModal.value = true
}

const handlePermissionSave = () => {
  showPermissionModal.value = false
  ElMessage.success('权限配置已保存')
}

// 角色分配
const handleUserRole = (row) => {
  assignForm.userId = row.id
  assignForm.roleIds = [1]
  showAssignModal.value = true
}

const handleAssignSave = () => {
  const user = userList.value.find(item => item.id === assignForm.userId)
  if (user) {
    const roleNames = roleList.value
      .filter(r => assignForm.roleIds.includes(r.id))
      .map(r => r.name)
      .join(', ')
    user.roles = roleNames || '无'
  }
  showAssignModal.value = false
  ElMessage.success('角色分配成功')
}
</script>

<style lang="scss" scoped>
.permission-page {
  padding: 20px;
}

.section-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.permission-tree {
  max-height: 400px;
  overflow-y: auto;
}
</style>
