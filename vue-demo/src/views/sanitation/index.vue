<template>
  <div class="sanitation-page">
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
        placeholder="检查等级" 
        class="search-select"
      >
        <el-option label="全部" value="" />
        <el-option label="优秀" value="优秀" />
        <el-option label="良好" value="良好" />
        <el-option label="合格" value="合格" />
        <el-option label="不合格" value="不合格" />
      </el-select>
      
      <el-button type="primary" class="add-btn" @click="showAddModal = true">
        <el-icon><Plus /></el-icon>
        添加检查记录
      </el-button>
    </div>
    
    <!-- 表格 -->
    <div class="table-container">
      <el-table :data="sanitationList" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="building" label="楼栋" width="100" />
        <el-table-column prop="dormNo" label="寝室号" width="100" />
        <el-table-column prop="checkDate" label="检查日期" width="130" />
        <el-table-column prop="checker" label="检查人员" width="120" />
        <el-table-column prop="level" label="检查等级" width="100">
          <template #default="scope">
            <el-tag :type="getLevelType(scope.row.level)">
              {{ scope.row.level }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="score" label="得分" width="80" />
        <el-table-column prop="remark" label="备注" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.status === '已整改' ? 'success' : 'warning'">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="small" type="success" @click="handleRectify(scope.row.id)">整改完成</el-button>
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
      :title="isEdit ? '编辑检查记录' : '添加检查记录'" 
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
        <el-form-item label="检查日期" prop="checkDate">
          <el-date-picker v-model="form.checkDate" type="date" placeholder="请选择日期" />
        </el-form-item>
        <el-form-item label="检查人员" prop="checker">
          <el-input v-model="form.checker" placeholder="请输入检查人员" />
        </el-form-item>
        <el-form-item label="检查等级" prop="level">
          <el-select v-model="form.level" placeholder="请选择等级">
            <el-option label="优秀" value="优秀" />
            <el-option label="良好" value="良好" />
            <el-option label="合格" value="合格" />
            <el-option label="不合格" value="不合格" />
          </el-select>
        </el-form-item>
        <el-form-item label="得分" prop="score">
          <el-input v-model="form.score" type="number" placeholder="请输入得分" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-textarea v-model="form.remark" placeholder="请输入备注" :rows="3" />
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
  total: 40
})

const showAddModal = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

const form = reactive({
  id: '',
  building: '',
  dormNo: '',
  checkDate: '',
  checker: '',
  level: '',
  score: '',
  remark: '',
  status: '待整改'
})

const rules = {
  building: [{ required: true, message: '请选择楼栋', trigger: 'blur' }],
  dormNo: [{ required: true, message: '请输入寝室号', trigger: 'blur' }],
  checkDate: [{ required: true, message: '请选择检查日期', trigger: 'blur' }],
  checker: [{ required: true, message: '请输入检查人员', trigger: 'blur' }],
  level: [{ required: true, message: '请选择检查等级', trigger: 'blur' }],
  score: [{ required: true, message: '请输入得分', trigger: 'blur' }]
}

const sanitationList = ref([
  { id: 1, building: '1号楼', dormNo: '101', checkDate: '2024-01-15', checker: '李老师', level: '优秀', score: 95, remark: '卫生整洁', status: '已整改' },
  { id: 2, building: '1号楼', dormNo: '102', checkDate: '2024-01-15', checker: '李老师', level: '良好', score: 85, remark: '整体较好', status: '已整改' },
  { id: 3, building: '2号楼', dormNo: '203', checkDate: '2024-01-14', checker: '王老师', level: '不合格', score: 55, remark: '地面脏乱', status: '待整改' },
  { id: 4, building: '2号楼', dormNo: '302', checkDate: '2024-01-14', checker: '王老师', level: '合格', score: 70, remark: '基本达标', status: '已整改' },
  { id: 5, building: '3号楼', dormNo: '105', checkDate: '2024-01-13', checker: '张老师', level: '良好', score: 82, remark: '卫生状况良好', status: '已整改' }
])

const getLevelType = (level) => {
  const types = {
    '优秀': 'success',
    '良好': 'primary',
    '合格': 'warning',
    '不合格': 'danger'
  }
  return types[level] || 'info'
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

const handleRectify = (id) => {
  const index = sanitationList.value.findIndex(item => item.id === id)
  if (index !== -1) {
    sanitationList.value[index].status = '已整改'
    ElMessage.success('标记整改完成')
  }
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  if (isEdit.value) {
    const index = sanitationList.value.findIndex(item => item.id === form.id)
    if (index !== -1) {
      sanitationList.value[index] = { ...form }
    }
    ElMessage.success('编辑成功')
  } else {
    const newId = sanitationList.value.length > 0 ? Math.max(...sanitationList.value.map(i => i.id)) + 1 : 1
    sanitationList.value.push({ ...form, id: newId, status: '待整改' })
    ElMessage.success('添加成功')
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
    checkDate: '',
    checker: '',
    level: '',
    score: '',
    remark: '',
    status: '待整改'
  })
}
</script>

<style lang="scss" scoped>
.sanitation-page {
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
