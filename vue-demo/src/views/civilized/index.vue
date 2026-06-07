<template>
  <div class="civilized-page">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input 
        v-model="searchForm.keyword" 
        placeholder="请输入楼栋或寝室号" 
        class="search-input"
        @keyup.enter="handleSearch"
      >
        <template #append>
          <el-button @click="handleSearch">
            <el-icon><Search /></el-icon>
          </el-button>
        </template>
      </el-input>
      
      <el-select 
        v-model="searchForm.building" 
        placeholder="楼栋" 
        class="search-select"
      >
        <el-option label="全部" value="" />
        <el-option label="1号楼" value="1号楼" />
        <el-option label="2号楼" value="2号楼" />
        <el-option label="3号楼" value="3号楼" />
        <el-option label="4号楼" value="4号楼" />
      </el-select>
      
      <el-select 
        v-model="searchForm.level" 
        placeholder="等级" 
        class="search-select"
      >
        <el-option label="全部" value="" />
        <el-option label="校级文明寝室" value="校级" />
        <el-option label="院级文明寝室" value="院级" />
        <el-option label="楼栋文明寝室" value="楼栋" />
      </el-select>
      
      <el-button type="primary" class="add-btn" @click="showAddModal = true">
        <el-icon><Plus /></el-icon>
        评选文明寝室
      </el-button>
    </div>
    
    <!-- 表格 -->
    <div class="table-container">
      <el-table :data="civilizedList" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="building" label="楼栋" width="100" />
        <el-table-column prop="dormNo" label="寝室号" width="100" />
        <el-table-column prop="level" label="等级" width="120">
          <template #default="scope">
            <el-tag :type="getLevelType(scope.row.level)" size="medium">
              {{ getLevelText(scope.row.level) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="students" label="寝室成员" />
        <el-table-column prop="awardDate" label="获奖日期" width="130" />
        <el-table-column prop="validUntil" label="有效期至" width="130" />
        <el-table-column prop="reason" label="获奖理由" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === '有效' ? 'success' : 'warning'">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row.id)">撤销</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    
    <!-- 分页 -->
    <div class="pagination-bar">
      <el-pagination
        :current-page="pagination.pageNum"
        :page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
    
    <!-- 添加/编辑弹窗 -->
    <el-dialog 
      :title="isEdit ? '编辑文明寝室' : '评选文明寝室'" 
      :visible.sync="showAddModal"
      width="500px"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="楼栋" prop="building">
          <el-select v-model="form.building" placeholder="请选择楼栋">
            <el-option label="1号楼" value="1号楼" />
            <el-option label="2号楼" value="2号楼" />
            <el-option label="3号楼" value="3号楼" />
            <el-option label="4号楼" value="4号楼" />
          </el-select>
        </el-form-item>
        <el-form-item label="寝室号" prop="dormNo">
          <el-input v-model="form.dormNo" placeholder="请输入寝室号" />
        </el-form-item>
        <el-form-item label="等级" prop="level">
          <el-select v-model="form.level" placeholder="请选择等级">
            <el-option label="校级文明寝室" value="校级" />
            <el-option label="院级文明寝室" value="院级" />
            <el-option label="楼栋文明寝室" value="楼栋" />
          </el-select>
        </el-form-item>
        <el-form-item label="寝室成员" prop="students">
          <el-input v-model="form.students" placeholder="请输入成员姓名（逗号分隔）" />
        </el-form-item>
        <el-form-item label="获奖日期" prop="awardDate">
          <el-date-picker v-model="form.awardDate" type="date" placeholder="请选择日期" />
        </el-form-item>
        <el-form-item label="有效期至" prop="validUntil">
          <el-date-picker v-model="form.validUntil" type="date" placeholder="请选择日期" />
        </el-form-item>
        <el-form-item label="获奖理由" prop="reason">
          <el-textarea v-model="form.reason" placeholder="请输入获奖理由" :rows="3" />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="showAddModal = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Plus } from '@element-plus/icons-vue'

const searchForm = reactive({
  keyword: '',
  building: '',
  level: ''
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 20
})

const showAddModal = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

const form = reactive({
  id: '',
  building: '',
  dormNo: '',
  level: '',
  students: '',
  awardDate: '',
  validUntil: '',
  reason: '',
  status: '有效'
})

const rules = {
  building: [{ required: true, message: '请选择楼栋', trigger: 'blur' }],
  dormNo: [{ required: true, message: '请输入寝室号', trigger: 'blur' }],
  level: [{ required: true, message: '请选择等级', trigger: 'blur' }],
  students: [{ required: true, message: '请输入寝室成员', trigger: 'blur' }],
  awardDate: [{ required: true, message: '请选择获奖日期', trigger: 'blur' }],
  validUntil: [{ required: true, message: '请选择有效期至', trigger: 'blur' }]
}

const civilizedList = ref([
  { id: 1, building: '1号楼', dormNo: '101', level: '校级', students: '张三,李四,王五,赵六', awardDate: '2024-01-01', validUntil: '2024-12-31', reason: '连续一学期卫生检查优秀', status: '有效' },
  { id: 2, building: '2号楼', dormNo: '203', level: '院级', students: '小红,小明,小华', awardDate: '2024-01-15', validUntil: '2024-12-31', reason: '卫生整洁，学习氛围好', status: '有效' },
  { id: 3, building: '3号楼', dormNo: '105', level: '楼栋', students: '小李,小王,小张', awardDate: '2024-02-01', validUntil: '2024-12-31', reason: '月度卫生检查优秀', status: '有效' },
  { id: 4, building: '1号楼', dormNo: '201', level: '校级', students: '钱七,孙八,周九,吴十', awardDate: '2023-01-01', validUntil: '2023-12-31', reason: '年度文明寝室', status: '过期' },
  { id: 5, building: '4号楼', dormNo: '302', level: '院级', students: '刘一,陈二,杨三', awardDate: '2024-03-01', validUntil: '2024-12-31', reason: '卫生检查连续三个月优秀', status: '有效' }
])

const getLevelType = (level) => {
  const types = {
    '校级': 'danger',
    '院级': 'primary',
    '楼栋': 'success'
  }
  return types[level] || 'info'
}

const getLevelText = (level) => {
  const texts = {
    '校级': '校级文明寝室',
    '院级': '院级文明寝室',
    '楼栋': '楼栋文明寝室'
  }
  return texts[level] || level
}

const handleSearch = () => {
  pagination.pageNum = 1
  ElMessage.info('搜索功能已触发')
}

const handleSizeChange = (size) => {
  pagination.pageSize = size
}

const handleCurrentChange = (page) => {
  pagination.pageNum = page
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, row)
  showAddModal.value = true
}

const handleDelete = (id) => {
  ElMessage.confirm('确定要撤销该文明寝室称号吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  }).then(() => {
    civilizedList.value = civilizedList.value.filter(item => item.id !== id)
    ElMessage.success('撤销成功')
  })
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  if (isEdit.value) {
    const index = civilizedList.value.findIndex(item => item.id === form.id)
    if (index !== -1) {
      civilizedList.value[index] = { ...form }
    }
    ElMessage.success('编辑成功')
  } else {
    const newId = civilizedList.value.length > 0 ? Math.max(...civilizedList.value.map(i => i.id)) + 1 : 1
    civilizedList.value.push({ ...form, id: newId, status: '有效' })
    ElMessage.success('评选成功')
  }
  
  showAddModal.value = false
  resetForm()
}

const resetForm = () => {
  isEdit.value = false
  Object.assign(form, {
    id: '',
    building: '',
    dormNo: '',
    level: '',
    students: '',
    awardDate: '',
    validUntil: '',
    reason: '',
    status: '有效'
  })
}
</script>

<style lang="scss" scoped>
.civilized-page {
  padding: 20px;
}

.search-bar {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.search-input {
  width: 250px;
}

.search-select {
  width: 150px;
}

.add-btn {
  margin-left: auto;
}

.table-container {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.pagination-bar {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>
